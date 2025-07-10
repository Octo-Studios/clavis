package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.data.LootUtils;
import it.hurts.octostudios.clavis.common.mixin.LootTableAccessor;
import it.hurts.octostudios.octolib.module.network.Packet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FinishLockpickingPacket extends Packet {
    public static final Type<FinishLockpickingPacket> TYPE =
            Packet.createType(Clavis.MODID, "finish_lockpick");
    public static final StreamCodec<RegistryFriendlyByteBuf, FinishLockpickingPacket> STREAM_CODEC =
            Packet.createCodec(FinishLockpickingPacket::write, FinishLockpickingPacket::new);

    BlockPos blockPos;
    float quality;
    Lock lock;

    public FinishLockpickingPacket(RegistryFriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.lock = buf.readJsonWithCodec(Lock.CODEC);
        this.quality = buf.readFloat();
    }

    public FinishLockpickingPacket(BlockPos blockPos, Lock lock, float quality) {
        this.blockPos = blockPos;
        this.lock = lock;
        this.quality = quality;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(Lock.CODEC, lock);
        buf.writeFloat(quality);
    }

    @Override
    protected void handleServer(NetworkManager.PacketContext packetContext) {
        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();
        ServerLevel level = player.serverLevel();

        ClavisSavedData data = ClavisSavedData.get(level);
        data.removeLock(lock, level);

        if (level.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity randomizable && randomizable.getLootTable() != null) {
            ResourceKey<LootTable> resourceKey = randomizable.getLootTable();
            LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(resourceKey);
            LootTableAccessor accessor = ((LootTableAccessor) lootTable);
            CriteriaTriggers.GENERATE_LOOT.trigger(player, resourceKey);

            randomizable.setLootTable(null);
            LootParams.Builder builder = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos));
            builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);

            Function<Long, LootContext> makeCtx = seedOffset -> new LootContext.Builder(builder.create(LootContextParamSets.CHEST))
                    .withOptionalRandomSeed(randomizable.getLootTableSeed() + seedOffset)
                    .create(accessor.getRandomSequence());

            LootContext defaultContext = makeCtx.apply(0L);
            RandomSource randomSource = defaultContext.getRandom();
            ObjectArrayList<ItemStack> mainList = new ObjectArrayList<>();
            if (quality >= 1f) {
                int fullCopies = (int) quality;
                float fraction  = quality - fullCopies;

                for (int i = 0; i < fullCopies; i++) {
                    long seed = randomizable.getLootTableSeed() + i * 31571L;
                    LootContext fullCtx = makeCtx.apply(seed);
                    mainList.addAll(accessor.invokeGetRandomItems(fullCtx));
                }

                if (fraction > 0f) {
                    long seed = randomizable.getLootTableSeed() + 31571L * fullCopies;
                    LootContext fracCtx = makeCtx.apply(seed);
                    ObjectArrayList<ItemStack> secondary = accessor.invokeGetRandomItems(fracCtx);

                    LootUtils.shrinkStacks(secondary, fraction, randomSource);
                    mainList.addAll(secondary);
                }

            } else if (quality < 1f) {
                mainList.addAll(accessor.invokeGetRandomItems(defaultContext));
                LootUtils.shrinkStacks(mainList, quality, randomSource);
            }

            List<Integer> list = accessor.invokeGetAvailableSlots(randomizable, randomSource);
            accessor.invokeShuffleAndSplitItems(mainList, list.size(), randomSource);

            for (ItemStack itemStack : mainList) {
                if (list.isEmpty()) {
                    return;
                }

                if (itemStack.isEmpty()) {
                    randomizable.setItem(list.removeLast(), ItemStack.EMPTY);
                } else {
                    randomizable.setItem(list.removeLast(), itemStack);
                }
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}