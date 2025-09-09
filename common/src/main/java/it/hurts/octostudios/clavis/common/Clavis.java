package it.hurts.octostudios.clavis.common;

import dev.architectury.event.events.common.*;
import it.hurts.octostudios.clavis.common.config.Config;
import it.hurts.octostudios.clavis.common.network.LockInteractionBlockers;
import it.hurts.octostudios.clavis.common.network.PacketRegistry;
import it.hurts.octostudios.clavis.common.network.command.ClavisCommands;
import it.hurts.octostudios.clavis.common.registry.ItemRegistry;
import it.hurts.octostudios.octolib.module.config.ConfigManager;
import net.minecraft.resources.ResourceLocation;

public class Clavis {
    public static final String MOD_ID = "clavis";
    public static final Config CONFIG = new Config();

    public static void init() {
        ConfigManager.registerConfig(Clavis.MOD_ID, Clavis.CONFIG);

        ItemRegistry.ITEMS.register();
        PacketRegistry.register();

        LootrCompat.init();

        LifecycleEvent.SERVER_LEVEL_LOAD.register(LockManager::load);
        LifecycleEvent.SERVER_LEVEL_SAVE.register(LockManager::save);

        BlockEvent.BREAK.register(LockInteractionBlockers::onBreak);
        ExplosionEvent.DETONATE.register(LockInteractionBlockers::onBlow);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(LockInteractionBlockers::onInteract);
        InteractionEvent.LEFT_CLICK_BLOCK.register(LockInteractionBlockers::cancelInteraction);
        CommandRegistrationEvent.EVENT.register(ClavisCommands::register);
    }

    public static ResourceLocation path(String path) {
        return Clavis.path(Clavis.MOD_ID, path);
    }

    public static ResourceLocation path(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}