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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

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
        if (Minecraft.getInstance().screen instanceof LockpickingScreen screen && screen.getLock().equals(lock)) {
            Minecraft.getInstance().setScreen(null);
        }

        if (!LockWorldRenderer.FOR_RENDERING.remove(lock)) {
            return;
        }
        ClientLevel level = (ClientLevel) packetContext.getPlayer().level();

        Vec3 pos = lock.getBox().getCenter();
        AABB aabb = lock.getBox().getAABB();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            Vec3 p = new Vec3(
                    pos.x,
                    aabb.maxY+0.35f,
                    pos.z
            );
            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), p.x, p.y, p.z, random.nextFloat(-0.15f, 0.15f), random.nextFloat(-0.15f, random.nextFloat(-0.15f, 0.15f)), 0);
        }

        for (int i = 0; i < 50; i++) {
            Vec3 p = new Vec3(
                    Mth.lerp(random.nextFloat(), aabb.minX, aabb.maxX),
                    Mth.lerp(random.nextFloat(), aabb.minY, aabb.maxY),
                    Mth.lerp(random.nextFloat(), aabb.minZ, aabb.maxZ)
            );
            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CHAIN.defaultBlockState()), p.x, p.y, p.z, random.nextFloat(-0.15f, 0.15f), random.nextFloat(-0.15f, random.nextFloat(-0.15f, 0.15f)), 0);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}