package it.hurts.shatterbyte.clavis.common;

import dev.architectury.event.events.common.*;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import it.hurts.octostudios.octolib.module.config.ConfigManager;
import it.hurts.shatterbyte.clavis.common.client.FallbackCursorMover;
import it.hurts.shatterbyte.clavis.common.client.NativeCursorMover;
import it.hurts.shatterbyte.clavis.common.client.WaylandCursorMover;
import it.hurts.shatterbyte.clavis.common.config.Config;
import it.hurts.shatterbyte.clavis.common.network.LockInteractionBlockers;
import it.hurts.shatterbyte.clavis.common.network.PacketRegistry;
import it.hurts.shatterbyte.clavis.common.network.command.ClavisCommands;
import it.hurts.shatterbyte.clavis.common.registry.ItemRegistry;
import it.hurts.shatterbyte.clavis.common.registry.MinigameTypeRegistry;
import it.hurts.shatterbyte.clavis.common.registry.SoundEventRegistry;
import net.minecraft.resources.ResourceLocation;

public class Clavis {
    public static final String MOD_ID = "clavis";
    public static final Config CONFIG = new Config();
    public static NativeCursorMover CURSOR_MOVER;

    public static void init() {
        boolean isWayland = System.getenv("WAYLAND_DISPLAY") != null;
        if (isWayland) {
            CURSOR_MOVER = new WaylandCursorMover();
        } else {
            CURSOR_MOVER = new FallbackCursorMover();
        }

        MinigameTypeRegistry.REGISTRY.add(MinigameTypeRegistry.GEAR);
        MinigameTypeRegistry.REGISTRY.add(MinigameTypeRegistry.MIRROR);

        ConfigManager.registerConfig(Clavis.MOD_ID, Clavis.CONFIG);

        ItemRegistry.ITEMS.register();
        PacketRegistry.register();

        SoundEventRegistry.SOUNDS.register();

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