package it.hurts.shatterbyte.clavis.common.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.MirrorWidget;
import it.hurts.shatterbyte.clavis.common.registry.MinigameTypeRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ClientMinigameTypeRegistry {
    public static final BiMap<ResourceLocation, Class<? extends AbstractMinigameWidget<?>>> REGISTRY = HashBiMap.create();
    private static final Map<ResourceLocation, ParticleOptions> PARTICLES = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<? extends AbstractMinigameWidget<?>>> FACTORIES = new HashMap<>();

    public static void init() {
        register(MinigameTypeRegistry.GEAR,
                GearMechanismWidget.class,
                GearMechanismWidget::new,
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState())
        );
        register(MinigameTypeRegistry.MIRROR,
                MirrorWidget.class,
                MirrorWidget::new,
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.PURPUR_BLOCK.defaultBlockState())
        );
    }

    public static <T extends AbstractMinigameWidget<?>> void register(ResourceLocation resourceLocation, Class<T> clazz, Supplier<T> factory, ParticleOptions particleOptions) {
        REGISTRY.put(resourceLocation, clazz);
        PARTICLES.put(resourceLocation, particleOptions);
        FACTORIES.put(resourceLocation, factory);
    }

    public static Class<? extends AbstractMinigameWidget<?>> getClass(ResourceLocation resourceLocation) {
        return REGISTRY.get(resourceLocation);
    }

    public static ResourceLocation getId(Class<? extends AbstractMinigameWidget<?>> clazz) {
        return REGISTRY.inverse().get(clazz);
    }

    public static ParticleOptions getLockParticles(ResourceLocation type) {
        return PARTICLES.getOrDefault(type, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()));
    }

    public static Supplier<? extends AbstractMinigameWidget<?>> getFactory(ResourceLocation type) {
        return FACTORIES.getOrDefault(type, GearMechanismWidget::new);
    };
}
