package it.hurts.octostudios.clavis.common.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import it.hurts.octostudios.clavis.common.data.*;
import it.hurts.octostudios.clavis.common.mixin.LootTableAccessor;
import it.hurts.octostudios.clavis.common.network.packet.LockRequestPacket;
import it.hurts.octostudios.octolib.OctoLib;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ChunkListeners {
    public static void onUnload(ClientLevel level, LevelChunk levelChunk) {
        ChunkPos chunkPos = levelChunk.getPos();
        List<Lock> toRemove = LockWorldRenderer.FOR_RENDERING.stream()
                .filter(lock -> lock.getBox().intersectsChunk(chunkPos))
                .toList();

        toRemove.forEach(LockWorldRenderer.FOR_RENDERING::remove);
    }

    public static void onLoad(ClientLevel level, LevelChunk levelChunk) {
        NetworkManager.sendToServer(new LockRequestPacket(levelChunk.getPos()));
    }

    public static void onGenerate(ServerLevel level, LevelChunk levelChunk) {
        ClavisSavedData data = ClavisSavedData.get(level);
        levelChunk.getBlockEntities().forEach((blockPos, blockEntity) -> {
            if (blockEntity instanceof RandomizableContainerBlockEntity randomizable && randomizable.getLootTable() != null) {
                LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(randomizable.getLootTable());
                long seed = randomizable.getLootTableSeed();

                Optional<ResourceLocation> randomSequence = ((LootTableAccessor) lootTable).getRandomSequence();

                LootParams.Builder builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos));
                LootContext actualLootContext = new LootContext.Builder(builder.create(LootContextParamSets.CHEST)).withOptionalRandomSeed(seed).create(randomSequence);

                ObjectArrayList<ItemStack> actualItems = ((LootTableAccessor) lootTable).invokeGetRandomItems(actualLootContext);

                AtomicInteger actualValue = new AtomicInteger();
                actualItems.forEach((stack) -> {
                    actualValue.addAndGet(ItemValues.getValue(stack));
                });

                float difficulty = actualValue.get() / 400f;
                OctoLib.LOGGER.info("actualValue: {}", actualValue.get());

                data.addLock(new Lock(new Box(blockPos), difficulty, seed), level);
            }
        });
    }
}
