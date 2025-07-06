package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class FinishLockpickingPacket extends Packet {
    public static final Type<FinishLockpickingPacket> TYPE =
            Packet.createType(Clavis.MODID, "finish_lockpick");
    public static final StreamCodec<RegistryFriendlyByteBuf, FinishLockpickingPacket> STREAM_CODEC =
            Packet.createCodec(FinishLockpickingPacket::write, FinishLockpickingPacket::new);

    BlockPos blockPos;
    Lock lock;

    public FinishLockpickingPacket(RegistryFriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.lock = buf.readJsonWithCodec(Lock.CODEC);
    }

    public FinishLockpickingPacket(BlockPos blockPos, Lock lock) {
        this.blockPos = blockPos;
        this.lock = lock;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(Lock.CODEC, lock);
    }

    @Override
    protected void handleServer(NetworkManager.PacketContext packetContext) {
        ClavisSavedData data = ClavisSavedData.get((ServerLevel) packetContext.getPlayer().level());
        data.removeLock(lock);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}