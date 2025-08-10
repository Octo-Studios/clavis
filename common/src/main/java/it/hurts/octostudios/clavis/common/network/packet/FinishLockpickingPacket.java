package it.hurts.octostudios.clavis.common.network.packet;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.data.LootUtils;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FinishLockpickingPacket extends Packet {
    public static final Type<FinishLockpickingPacket> TYPE =
            Packet.createType(Clavis.MODID, "finish_lockpick");
    public static final StreamCodec<RegistryFriendlyByteBuf, FinishLockpickingPacket> STREAM_CODEC =
            Packet.createCodec(FinishLockpickingPacket::write, FinishLockpickingPacket::new);

    BlockPos blockPos;
    float quality;
    Lock lock;

    public FinishLockpickingPacket(RegistryFriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.lock = buf.readJsonWithCodec(Lock.CODEC);
        this.quality = buf.readFloat();
    }

    public FinishLockpickingPacket(BlockPos blockPos, Lock lock, float quality) {
        this.blockPos = blockPos;
        this.lock = lock;
        this.quality = quality;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(Lock.CODEC, lock);
        buf.writeFloat(quality);
    }

    @Override
    protected void handleServer(NetworkManager.PacketContext packetContext) {
        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();
        ServerLevel level = player.serverLevel();

        LootUtils.unlockWithQuality(level, player, blockPos, lock, quality);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}