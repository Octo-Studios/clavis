package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.LockManager;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class CheckIfLockedPacket extends Packet {
    public static final Type<CheckIfLockedPacket> TYPE =
            Packet.createType(Clavis.MODID, "check_locked");
    public static final StreamCodec<RegistryFriendlyByteBuf, CheckIfLockedPacket> STREAM_CODEC =
            Packet.createCodec(CheckIfLockedPacket::write, CheckIfLockedPacket::new);

    BlockPos blockPos;

    public CheckIfLockedPacket(RegistryFriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
    }

    public CheckIfLockedPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
    }

    @Override
    protected void handleServer(NetworkManager.PacketContext packetContext) {
        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();
        List<Lock> locks = LockManager.getLocksAt(player.serverLevel(), player, blockPos);
        if (locks.isEmpty()) {
            return;
        }
        NetworkManager.sendToPlayer(player, new OpenLockpickingPacket(blockPos, locks.getFirst()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}