package it.hurts.octostudios.clavis.fabric;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.network.ChunkListeners;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;

public final class ClavisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Clavis.init();

        ClientChunkEvents.CHUNK_LOAD.register(ChunkListeners::onLoad);
        ClientChunkEvents.CHUNK_UNLOAD.register(ChunkListeners::onUnload);
        ServerChunkEvents.CHUNK_GENERATE.register(ChunkListeners::onGenerate);
    }
}
