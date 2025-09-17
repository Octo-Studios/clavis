package it.hurts.shatterbyte.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.LockManager;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.List;

public class LockRequestPacket extends Packet {
    public static final Type<LockRequestPacket> TYPE =
            Packet.createType(Clavis.MOD_ID, "request_locks");
    public static final StreamCodec<RegistryFriendlyByteBuf, LockRequestPacket> STREAM_CODEC =
            Packet.createCodec(LockRequestPacket::write, LockRequestPacket::new);

    ChunkPos pos;

    public LockRequestPacket(RegistryFriendlyByteBuf buf) {
        this.pos = new ChunkPos(buf.readInt(), buf.readInt());
    }

    public LockRequestPacket(ChunkPos pos) {
        this.pos = pos;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(pos.x);
        buf.writeInt(pos.z);
    }

    @Override
    protected void handleServer(NetworkManager.PacketContext packetContext) {
        if (Clavis.CONFIG.isDisableLockRendering()) {
            return;
        }

        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();
        ServerLevel level = player.serverLevel();

        List<Lock> locks = LockManager.getLocksAt(level, player, pos);
        if (locks.isEmpty()) {
            return;
        }

        NetworkManager.sendToPlayer((ServerPlayer) packetContext.getPlayer(), new ReceiveLocksForRenderingPacket(locks));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}