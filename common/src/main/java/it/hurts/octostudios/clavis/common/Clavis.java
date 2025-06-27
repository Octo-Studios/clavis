package it.hurts.octostudios.clavis.common;

import net.minecraft.resources.ResourceLocation;

public class Clavis {
    public static final String MODID = "clavis";

    public static void init() {

    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(Clavis.MODID, path);
    }
}