package it.hurts.shatterbyte.clavis.common.registry;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.data.ValueModifier;
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
        double perEnchantmentLevel = Clavis.CONFIG.getModifiers().getPerEnchantmentLevel();

        if (enchantments.isEmpty()) {
            return 1d;
        }

        AtomicReference<Double> multiplier = new AtomicReference<>(1d);

        enchantments.keySet().forEach(enchantmentHolder -> {
            int level = itemStack.getEnchantments().getLevel(enchantmentHolder);
            multiplier.updateAndGet(v -> v + (level * perEnchantmentLevel));
        });

        return multiplier.get();
    });

    public static final ValueModifier MAX_STACK = createModifier(Clavis.path("max_stack"), itemStack -> {
        // reward low-stack-size items with a value boost
        int maxStack = itemStack.getMaxStackSize();
        if (maxStack <= 16) {
            return Clavis.CONFIG.getModifiers().getLowStackSize();
        } else if (maxStack <= 64) {
            return 1d;
        }
        return 1d;
    });

    public static final ValueModifier RARITY = createModifier(Clavis.path("rarity"), itemStack -> {
        Rarity rarity = itemStack.getRarity();
        return Clavis.CONFIG.getModifiers().getRarity().getOrDefault(rarity, 1.0);
    });

    public static final ValueModifier CURIOS = createModifier(Clavis.path("curios"), itemStack -> {
        if (itemStack.getTags().anyMatch(itemTagKey -> itemTagKey.location().getNamespace().equals("curios") || itemTagKey.location().getNamespace().equals("trinkets"))) {
            return Clavis.CONFIG.getModifiers().getIsCuriosOrTrinkets();
        }
        return 1d;
    });

    public static ValueModifier createModifier(ResourceLocation resourceLocation, ValueModifier modifier) {
        MODIFIERS.put(resourceLocation, modifier);
        return modifier;
    }
}
