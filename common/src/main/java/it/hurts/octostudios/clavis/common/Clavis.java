package it.hurts.octostudios.clavis.common;

import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import it.hurts.octostudios.clavis.common.network.PacketRegistry;
import net.minecraft.resources.ResourceLocation;

public class Clavis {
    public static final String MODID = "clavis";

    public static void init() {
        Rule.registerAll();
        PacketRegistry.register();
    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(Clavis.MODID, path);
    }
}