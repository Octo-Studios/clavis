package it.hurts.octostudios.clavis.fabric;

import it.hurts.octostudios.clavis.common.ClavisClient;
import net.fabricmc.api.ClientModInitializer;

public final class ClavisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClavisClient.init();
    }
}
