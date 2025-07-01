package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class OpenLockpickingPacket implements CustomPacketPayload {
    public static final Type<OpenLockpickingPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "lockpicking_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLockpickingPacket> STREAM_CODEC =
            CustomPacketPayload.codec(OpenLockpickingPacket::write, OpenLockpickingPacket::new);

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

    public void handle(NetworkManager.PacketContext packetContext) {
        this.clientHandle(packetContext);
    }

    @Environment(EnvType.CLIENT)
    private void clientHandle(NetworkManager.PacketContext packetContext) {
        if (packetContext.getEnvironment() != Env.CLIENT) {
            return;
        }

        packetContext.queue(() -> {
            packetContext.getPlayer().sendSystemMessage(Component.literal("Env: "+packetContext.getEnvironment().name()));
            Minecraft.getInstance().setScreen(new LockpickingScreen(blockPos));
        });
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}