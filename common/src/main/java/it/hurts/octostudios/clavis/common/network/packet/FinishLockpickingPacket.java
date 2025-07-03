package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public class FinishLockpickingPacket extends Packet {
    public static final Type<FinishLockpickingPacket> TYPE =
            Packet.createType(Clavis.MODID, "finish_lockpick");
    public static final StreamCodec<RegistryFriendlyByteBuf, FinishLockpickingPacket> STREAM_CODEC =
            Packet.createCodec(FinishLockpickingPacket::write, FinishLockpickingPacket::new);

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

    @Override
    protected void handleServer(NetworkManager.PacketContext packetContext) {
        packetContext.getPlayer().sendSystemMessage(Component.literal("Env: "+packetContext.getEnvironment().name()));
        BlockEntity blockEntity = packetContext.getPlayer().level().getBlockEntity(blockPos);
        if (blockEntity instanceof RandomizableContainerBlockEntity containerBlock) {
            packetContext.getPlayer().openMenu(containerBlock);
        }
        ClavisSavedData.get((ServerLevel) packetContext.getPlayer().level()).addLock(new Lock(new Box(new Vec3i(0,0,0), new Vec3i(5,5,5)), 0.77f, 0L));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}