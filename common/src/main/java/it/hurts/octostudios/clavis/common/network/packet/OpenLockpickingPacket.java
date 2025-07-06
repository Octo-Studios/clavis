package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class OpenLockpickingPacket extends Packet {
    public static final Type<OpenLockpickingPacket> TYPE =
            Packet.createType(Clavis.MODID, "lockpicking_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLockpickingPacket> STREAM_CODEC =
            Packet.createCodec(OpenLockpickingPacket::write, OpenLockpickingPacket::new);

    BlockPos blockPos;
    Lock lock;

    public OpenLockpickingPacket(RegistryFriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.lock = buf.readJsonWithCodec(Lock.CODEC);
    }

    public OpenLockpickingPacket(BlockPos blockPos, Lock lock) {
        this.blockPos = blockPos;
        this.lock = lock;
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(Lock.CODEC, lock);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        LockWorldRenderer.FOR_RENDERING.add(lock);
        packetContext.queue(() -> Minecraft.getInstance().setScreen(new LockpickingScreen(blockPos, lock)));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}