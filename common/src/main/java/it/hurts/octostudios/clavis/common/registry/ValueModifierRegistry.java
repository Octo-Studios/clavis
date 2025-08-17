package it.hurts.octostudios.clavis.common.registry;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.data.ValueModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ValueModifierRegistry {
    public static final Map<ResourceLocation, ValueModifier> MODIFIERS = new HashMap<>();

    public static final ValueModifier ENCHANTMENT = createModifier(Clavis.path("enchantment"), itemStack -> {
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);
        if (enchantments.isEmpty()) {
            return 1d;
        }

        AtomicReference<Double> multiplier = new AtomicReference<>(1d);

        enchantments.keySet().forEach(enchantmentHolder -> {
            int level = itemStack.getEnchantments().getLevel(enchantmentHolder);
            multiplier.updateAndGet(v -> v + (level * 0.25d));
        });

        return multiplier.get();
    });

    public static final ValueModifier MAX_STACK = createModifier(Clavis.path("max_stack"), itemStack -> {
        // reward low-stack-size items with a value boost
        int maxStack = itemStack.getMaxStackSize();
        if (maxStack <= 16) {
            return 1.25d;
        } else if (maxStack <= 64) {
            return 1d;
        }
        return 1d;
    });

    public static final ValueModifier RARITY = createModifier(Clavis.path("rarity"), itemStack -> {
        Rarity rarity = itemStack.getRarity();
        return switch (rarity) {
            case UNCOMMON -> 1.25d;
            case RARE -> 1.5d;
            case EPIC -> 2d;
            default -> 1d;
        };
    });

    public static final ValueModifier CURIOS = createModifier(Clavis.path("curios"), itemStack -> {
        if (itemStack.getTags().anyMatch(itemTagKey -> itemTagKey.location().getNamespace().equals("curios") || itemTagKey.location().getNamespace().equals("trinkets"))) {
            return 8d;
        }
        return 1d;
    });

    public static ValueModifier createModifier(ResourceLocation resourceLocation, ValueModifier modifier) {
        MODIFIERS.put(resourceLocation, modifier);
        return modifier;
    }
}
