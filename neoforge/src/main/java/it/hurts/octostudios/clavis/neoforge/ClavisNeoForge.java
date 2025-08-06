package it.hurts.octostudios.clavis.neoforge;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.network.ChunkListeners;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.level.ChunkEvent;

@Mod(Clavis.MODID)
@EventBusSubscriber
public final class ClavisNeoForge {
    public ClavisNeoForge(IEventBus modBus) {
        Clavis.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            new ClavisNeoForgeClient(modBus);
    }

    @SubscribeEvent
    public static void loadChunk(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) || !event.isNewChunk()) {
            return;
        }
        ChunkListeners.onGenerate(level, chunk);
    }
}
