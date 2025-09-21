package it.hurts.shatterbyte.clavis.common.registry;

import it.hurts.shatterbyte.clavis.common.Clavis;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class MinigameTypeRegistry {
    public static final ResourceLocation GEAR = Clavis.path("gear");
    public static final ResourceLocation MIRROR = Clavis.path("mirror");

    public static final Set<ResourceLocation> REGISTRY = new HashSet<>();
}
