package it.hurts.octostudios.clavis.fabric;

import it.hurts.octostudios.clavis.common.Clavis;
import net.fabricmc.api.ModInitializer;

public final class ClavisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Clavis.init();
    }
}
