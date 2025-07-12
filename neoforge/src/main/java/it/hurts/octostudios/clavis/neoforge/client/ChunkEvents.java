package it.hurts.octostudios.clavis.neoforge.client;

import it.hurts.octostudios.clavis.common.ClavisClient;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ChunkEvents {
    @SubscribeEvent
    public static void onLoad(ChunkEvent.Load event) {
        if (event.getChunk() instanceof LevelChunk chunk && event.getLevel() instanceof ClientLevel level) {
            ClavisClient.onLoadChunk(level, chunk);

        }
    }

    @SubscribeEvent
    public static void onUnload(ChunkEvent.Unload event) {
        if (event.getChunk() instanceof LevelChunk chunk && event.getLevel() instanceof ClientLevel level) {
            ClavisClient.onUnloadChunk(level, chunk);
        }
    }

    @SubscribeEvent
    public static void onEntityTravel(EntityTravelToDimensionEvent event) {
        LockWorldRenderer.clear();
    }
}
