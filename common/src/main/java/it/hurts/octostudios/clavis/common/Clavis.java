package it.hurts.octostudios.clavis.common;

import dev.architectury.event.events.common.*;
import it.hurts.octostudios.clavis.common.data.ItemValues;
import it.hurts.octostudios.clavis.common.network.ClavisCommands;
import it.hurts.octostudios.clavis.common.network.LockInteractionBlockers;
import it.hurts.octostudios.clavis.common.network.PacketRegistry;
import net.minecraft.resources.ResourceLocation;

public class Clavis {
    public static final String MODID = "clavis";

    public static void init() {
        PacketRegistry.register();
        ItemValues.register();

        LootrCompat.init();

        LifecycleEvent.SERVER_LEVEL_LOAD.register(LockManager::load);
        LifecycleEvent.SERVER_LEVEL_SAVE.register(LockManager::save);
//        LifecycleEvent.SERVER_BEFORE_START.register(server -> {
//            Registry<LootTable> registry = server.reloadableRegistries().get().registryOrThrow(Registries.LOOT_TABLE);
//            long time = System.currentTimeMillis();
//            registry.registryKeySet().stream()
//                    .filter(key -> key.location().getPath().contains("chests"))
//                    .forEach(key ->
//                    {
//                        float meanDifficulty = LootUtils.calculateDifficulty(server, server.overworld(), null, key, 0, 25);
//                        ItemValues.DIFFICULTY_CACHE.put(key, meanDifficulty);
//                    });
//            OctoLib.LOGGER.info("DONE: {}ms", String.format("%.2f", (System.currentTimeMillis() - time)/1000f));
//        });

        BlockEvent.BREAK.register(LockInteractionBlockers::onBreak);
        ExplosionEvent.DETONATE.register(LockInteractionBlockers::onBlow);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(LockInteractionBlockers::onInteract);
        InteractionEvent.LEFT_CLICK_BLOCK.register(LockInteractionBlockers::cancelInteraction);
        CommandRegistrationEvent.EVENT.register(ClavisCommands::register);
    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(Clavis.MODID, path);
    }
}