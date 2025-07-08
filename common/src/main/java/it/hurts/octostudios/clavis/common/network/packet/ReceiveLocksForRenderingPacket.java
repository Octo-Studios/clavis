package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class ReceiveLocksForRenderingPacket extends Packet {
    public static final Type<ReceiveLocksForRenderingPacket> TYPE =
            Packet.createType(Clavis.MODID, "receive_locks");
    public static final StreamCodec<RegistryFriendlyByteBuf, ReceiveLocksForRenderingPacket> STREAM_CODEC =
            Packet.createCodec(ReceiveLocksForRenderingPacket::write, ReceiveLocksForRenderingPacket::new);

    List<Lock> locks;

    public ReceiveLocksForRenderingPacket(RegistryFriendlyByteBuf buf) {
        this.locks = buf.readJsonWithCodec(Lock.CODEC.listOf());
    }

    public ReceiveLocksForRenderingPacket(List<Lock> locks) {
        this.locks = locks;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeJsonWithCodec(Lock.CODEC.listOf(), locks);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        LockWorldRenderer.FOR_RENDERING.addAll(locks);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}