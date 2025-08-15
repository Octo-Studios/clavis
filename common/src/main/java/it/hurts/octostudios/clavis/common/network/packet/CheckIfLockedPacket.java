package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.LockManager;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.data.LootUtils;
import it.hurts.octostudios.clavis.common.registry.ItemRegistry;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CheckIfLockedPacket extends Packet {
    public static final Type<CheckIfLockedPacket> TYPE =
            Packet.createType(Clavis.MODID, "check_locked");
    public static final StreamCodec<RegistryFriendlyByteBuf, CheckIfLockedPacket> STREAM_CODEC =
            Packet.createCodec(CheckIfLockedPacket::write, CheckIfLockedPacket::new);

    BlockPos blockPos;
    InteractionHand hand;

    public CheckIfLockedPacket(RegistryFriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.hand = buf.readEnum(InteractionHand.class);
    }

    public CheckIfLockedPacket(BlockPos blockPos, InteractionHand hand) {
        this.blockPos = blockPos;
        this.hand = hand;
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeEnum(hand);
    }

    @Override
    protected void handleServer(NetworkManager.PacketContext packetContext) {
        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();
        List<Lock> locks = LockManager.getLocksAt(player.serverLevel(), player, blockPos);
        if (locks.isEmpty()) {
            return;
        }

        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.is(ItemRegistry.LOCK_PICK.get())) {
            LootUtils.unlockWithQuality(player.serverLevel(), player, blockPos, locks.getFirst(), 2f);
            itemInHand.shrink(1);
            return;
        }

        NetworkManager.sendToPlayer(player, new OpenLockpickingPacket(blockPos, locks.getFirst()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}