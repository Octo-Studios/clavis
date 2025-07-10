package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class AddLockPacket extends Packet {
    public static final Type<AddLockPacket> TYPE =
            Packet.createType(Clavis.MODID, "add_lock");
    public static final StreamCodec<RegistryFriendlyByteBuf, AddLockPacket> STREAM_CODEC =
            Packet.createCodec(AddLockPacket::write, AddLockPacket::new);

    Lock lock;

    public AddLockPacket(RegistryFriendlyByteBuf buf) {
        this.lock = buf.readJsonWithCodec(Lock.CODEC);
    }

    public AddLockPacket(Lock lock) {
        this.lock = lock;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeJsonWithCodec(Lock.CODEC, this.lock);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        LockWorldRenderer.FOR_RENDERING.add(lock);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}