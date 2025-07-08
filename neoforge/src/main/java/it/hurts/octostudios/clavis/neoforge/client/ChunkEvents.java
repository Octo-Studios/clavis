package it.hurts.octostudios.clavis.neoforge.client;

import it.hurts.octostudios.clavis.common.network.ChunkListeners;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ChunkEvents {
    @SubscribeEvent
    public static void onLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level && event.getChunk() instanceof LevelChunk chunk) {
            ChunkListeners.onLoad(level, chunk);
        }
    }

    @SubscribeEvent
    public static void onUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel level && event.getChunk() instanceof LevelChunk chunk) {
            ChunkListeners.onUnload(level, chunk);
        }
    }

    @SubscribeEvent
    public static void test(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) {
            chunk.getFullStatus()
        }
    }
}
