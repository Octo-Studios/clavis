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