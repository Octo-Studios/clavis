package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import it.hurts.octostudios.clavis.common.Clavis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public class FinishLockpickingPacket implements CustomPacketPayload {
    public static final Type<FinishLockpickingPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "finish_lockpick"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FinishLockpickingPacket> STREAM_CODEC =
            CustomPacketPayload.codec(FinishLockpickingPacket::write, FinishLockpickingPacket::new);

    BlockPos blockPos;

    public FinishLockpickingPacket(RegistryFriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
    }

    public FinishLockpickingPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
    }

    public void handle(NetworkManager.PacketContext packetContext) {
        packetContext.getPlayer().sendSystemMessage(Component.literal("Env: "+packetContext.getEnvironment().name()));
        BlockEntity blockEntity = packetContext.getPlayer().level().getBlockEntity(blockPos);
        if (blockEntity instanceof RandomizableContainerBlockEntity containerBlock) {
            packetContext.getPlayer().openMenu(containerBlock);
        }
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}