package it.hurts.octostudios.clavis.common.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.network.packet.LockRequestPacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.List;

public class ChunkListeners {
    public static void onUnload(ClientLevel level, LevelChunk levelChunk) {
        ChunkPos chunkPos = levelChunk.getPos();
        List<Lock> toRemove = LockWorldRenderer.FOR_RENDERING.stream()
                .filter(lock -> lock.getBox().intersectsChunk(chunkPos))
                .toList();

        toRemove.forEach(LockWorldRenderer.FOR_RENDERING::remove);
    }

    public static void onLoad(ClientLevel level, LevelChunk levelChunk) {
        NetworkManager.sendToServer(new LockRequestPacket(levelChunk.getPos()));
    }

    public static void onGenerate(ServerLevel level, LevelChunk levelChunk) {
        levelChunk
    }
}
