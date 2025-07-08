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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.List;

public class RemoveLockPacket extends Packet {
    public static final Type<RemoveLockPacket> TYPE =
            Packet.createType(Clavis.MODID, "remove_lock");
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveLockPacket> STREAM_CODEC =
            Packet.createCodec(RemoveLockPacket::write, RemoveLockPacket::new);

    Lock lock;

    public RemoveLockPacket(RegistryFriendlyByteBuf buf) {
        this.lock = buf.readJsonWithCodec(Lock.CODEC);
    }

    public RemoveLockPacket(Lock lock) {
        this.lock = lock;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeJsonWithCodec(Lock.CODEC, this.lock);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        LockWorldRenderer.FOR_RENDERING.remove(lock);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}