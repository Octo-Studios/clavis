package it.hurts.octostudios.clavis.fabric;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.network.ChunkListeners;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public final class ClavisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Clavis.init();

        ServerChunkEvents.CHUNK_GENERATE.register(ChunkListeners::onGenerate);
    }
}
