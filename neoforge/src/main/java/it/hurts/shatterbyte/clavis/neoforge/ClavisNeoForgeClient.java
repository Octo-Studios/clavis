package it.hurts.shatterbyte.clavis.neoforge;

import it.hurts.shatterbyte.clavis.common.ClavisClient;
import net.neoforged.bus.api.IEventBus;

public final class ClavisNeoForgeClient {
    public ClavisNeoForgeClient(IEventBus modBus) {
        ClavisClient.init();
    }
}
