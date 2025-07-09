package it.hurts.octostudios.clavis.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.*;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum ValueModifier {
    TIER(itemStack -> {
        if (itemStack.getItem() instanceof TieredItem tieredItem) {
            return tieredItem.getTier().getAttackDamageBonus()+1;
        }

        return 1f;
    }),

    ENCHANTED(itemStack -> {
        if (itemStack.getEnchantments().isEmpty()) {
            return 1f;
        }
        AtomicReference<Float> multiplier = new AtomicReference<>(1f);
        itemStack.getEnchantments().keySet().forEach(enchantmentHolder -> {
            int level = itemStack.getEnchantments().getLevel(enchantmentHolder);
            multiplier.updateAndGet(v -> v + (level * 0.5f)); // +20% per level
        });
        return multiplier.get();
    }),

    STACK_SIZE(itemStack -> {
        // reward low-stack-size items with a value boost
        int maxStack = itemStack.getMaxStackSize();
        if (maxStack <= 16) {
            return 2f;
        } else if (maxStack <= 64) {
            return 1f;
        }
        return 1f;
    }),

    RARITY(itemStack -> {
        Rarity rarity = itemStack.getRarity();
        return switch (rarity) {
            case UNCOMMON -> 2f;
            case RARE -> 4f;
            case EPIC -> 8f;
            default -> 1f;
        };
    }),

    POTION_EFFECTS(itemStack -> {
        if (itemStack.getItem() instanceof PotionItem || itemStack.getItem() instanceof TippedArrowItem) {
            return 2f; // potions/tipped arrows are inherently more valuable
        }

        return 1f;
    }),

    NBT_DATA(itemStack -> {
        if (!itemStack.getComponents().isEmpty()) {
            return 1f;
        }

        int nbtSize = itemStack.getComponents().size();
        return 1f + (nbtSize * 0.02f); // +2% per NBT tag entry
    });

    ;
    private final Function<ItemStack, Float> modifier;
}
