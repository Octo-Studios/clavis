package it.hurts.octostudios.clavis.neoforge;

import it.hurts.octostudios.clavis.common.ClavisClient;
import net.neoforged.bus.api.IEventBus;

public final class ClavisNeoForgeClient {
    public ClavisNeoForgeClient(IEventBus modBus) {
        ClavisClient.init();
    }
}
