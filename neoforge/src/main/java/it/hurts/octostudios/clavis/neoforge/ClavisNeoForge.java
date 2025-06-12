package it.hurts.octostudios.clavis.neoforge;

import it.hurts.octostudios.clavis.common.Clavis;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Clavis.MODID)
public final class ClavisNeoForge {
    public ClavisNeoForge(IEventBus modBus) {
        Clavis.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            new ClavisNeoForgeClient(modBus);
    }
}
