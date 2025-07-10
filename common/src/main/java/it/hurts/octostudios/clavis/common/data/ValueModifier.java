package it.hurts.octostudios.clavis.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Getter
public enum ValueModifier {
    TIER(itemStack -> {
        if (itemStack.getItem() instanceof TieredItem tieredItem) {
            return tieredItem.getTier().getAttackDamageBonus()/2f+1;
        }

        return 1f;
    }),

    ENCHANTED(itemStack -> {
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);
        if (enchantments.isEmpty()) {
            return 1f;
        }

        AtomicReference<Float> multiplier = new AtomicReference<>(1f);

        enchantments.keySet().forEach(enchantmentHolder -> {
            int level = itemStack.getEnchantments().getLevel(enchantmentHolder);
            multiplier.updateAndGet(v -> v + (level * 0.2f));
        });

        if (itemStack.is(Items.ENCHANTED_BOOK)) {
            multiplier.updateAndGet(v -> v + 20);
        }

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
            case UNCOMMON -> 1.5f;
            case RARE -> 2f;
            case EPIC -> 4f;
            default -> 1f;
        };
    }),

    POTION_EFFECTS(itemStack -> {
        if (itemStack.getItem() instanceof PotionItem) {
            PotionContents contents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            float value = StreamSupport.stream(contents.getAllEffects().spliterator(), false).mapToInt(effect -> (effect.getAmplifier() + 1)).sum();
            return value + 1; // potions/tipped arrows are inherently more valuable
        }

        if (itemStack.getItem() instanceof TippedArrowItem) {
            return 3f;
        }

        return 1f;
    }),

    NBT_DATA(itemStack -> {
        if (!itemStack.getComponents().isEmpty()) {
            return 1f;
        }

        int nbtSize = itemStack.getComponents().size();
        return 1f + (nbtSize * 0.05f); // +2% per NBT tag entry
    }),

    FOIL(itemStack -> {
        if (itemStack.hasFoil()) {
            return 1.25f;
        }

        return 1f;
    }),

    ARMOR(itemStack -> {
        if (itemStack.getItem() instanceof ArmorItem armor) {
            return 1f + (armor.getDefense() - 1) / 2f;
        }

        return 1f;
    })

    ;
    private final Function<ItemStack, Float> modifier;
}
