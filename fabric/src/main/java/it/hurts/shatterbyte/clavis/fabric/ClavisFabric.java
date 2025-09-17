package it.hurts.shatterbyte.clavis.fabric;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.network.ChunkListeners;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public final class ClavisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Clavis.init();

        ServerChunkEvents.CHUNK_GENERATE.register(ChunkListeners::onGenerate);
    }
}
