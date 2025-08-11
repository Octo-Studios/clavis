package it.hurts.octostudios.clavis.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum ValueModifier {
//    TIER(itemStack -> {
//        if (itemStack.getItem() instanceof TieredItem tieredItem) {
//            return tieredItem.getTier().getAttackDamageBonus()/3d+1;
//        }
//
//        return 1d;
//    }),

    ENCHANTED(itemStack -> {
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
    }),

    STACK_SIZE(itemStack -> {
        // reward low-stack-size items with a value boost
        int maxStack = itemStack.getMaxStackSize();
        if (maxStack <= 16) {
            return 1.25d;
        } else if (maxStack <= 64) {
            return 1d;
        }
        return 1d;
    }),

    RARITY(itemStack -> {
        Rarity rarity = itemStack.getRarity();
        return switch (rarity) {
            case UNCOMMON -> 1.25d;
            case RARE -> 1.5d;
            case EPIC -> 2d;
            default -> 1d;
        };
    }),

    CURIOS(itemStack -> {
        if (itemStack.getTags().anyMatch(itemTagKey -> itemTagKey.location().getNamespace().equals("curios") || itemTagKey.location().getNamespace().equals("trinkets"))) {
            return 8d;
        }

        return 1d;
    })

//    VALUABLE_TAGS(itemStack -> {
//        double multiplier = 1f;
//
//        if (itemStack.is(ItemValues.tag("foods/golden"))) {
//            multiplier += 3f;
//        }
//
//        if (itemStack.is(ItemValues.tag("music_discs"))) {
//            multiplier += 2f;
//        }
//
//        return multiplier;
//    })

//    POTION_EFFECTS(itemStack -> {
//        if (itemStack.getItem() instanceof PotionItem) {
//            PotionContents contents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
//            float value = StreamSupport.stream(contents.getAllEffects().spliterator(), false).mapToInt(effect -> (effect.getAmplifier() + 1)).sum();
//            return value + 1; // potions/tipped arrows are inherently more valuable
//        }
//
//        if (itemStack.getItem() instanceof TippedArrowItem) {
//            return 2f;
//        }
//
//        return 1f;
//    }),
//
//    NBT_DATA(itemStack -> {
//        if (!itemStack.getComponents().isEmpty()) {
//            return 1f;
//        }
//
//        int nbtSize = itemStack.getComponents().size();
//        return 1f + (nbtSize * 0.05f); // +2% per NBT tag entry
//    }),
//
//    ARMOR(itemStack -> {
//        if (itemStack.getItem() instanceof ArmorItem armor) {
//            return 1d + (armor.getDefense() - 1) / 4d;
//        }
//
//        return 1d;
//    })

    ;
    private final Function<ItemStack, Double> modifier;
}
