package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
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

    public OpenLockpickingPacket(RegistryFriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
    }

    public OpenLockpickingPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        packetContext.queue(() -> Minecraft.getInstance().setScreen(new LockpickingScreen(blockPos)));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}