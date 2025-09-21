package it.hurts.shatterbyte.clavis.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.MirrorWidget;
import net.minecraft.resources.ResourceLocation;

public class MinigameTypeRegistry {
    public static final BiMap<ResourceLocation, Class<? extends AbstractMinigameWidget<?>>> REGISTRY = HashBiMap.create();

    public static void init() {
        register(GearMechanismWidget.ID, GearMechanismWidget.class);
        register(MirrorWidget.ID, MirrorWidget.class);
    }

    public static void register(ResourceLocation resourceLocation, Class<? extends AbstractMinigameWidget<?>> clazz) {
        REGISTRY.put(resourceLocation, clazz);
    }

    public static Class<? extends AbstractMinigameWidget<?>> getClass(ResourceLocation resourceLocation) {
        return REGISTRY.get(resourceLocation);
    }

    public static ResourceLocation getId(Class<? extends AbstractMinigameWidget<?>> clazz) {
        return REGISTRY.inverse().get(clazz);
    }
}
