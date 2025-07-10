package it.hurts.octostudios.clavis.common.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ItemValues {
    public static final Map<TagKey<Item>, Function<ItemStack, Integer>> TAGS = new HashMap<>();
    public static final Function<Integer, Function<ItemStack, Integer>> DEFAULT_FUNCTION = value -> itemStack -> {
        int finalValue = value;

        for (ValueModifier modifier : ValueModifier.values()) {
            finalValue *= modifier.getModifier().apply(itemStack);
        }

        return finalValue * itemStack.getCount();
    };

    public static void addBasicValue(ResourceLocation rl, int value) {
        TAGS.put(TagKey.create(Registries.ITEM, rl), DEFAULT_FUNCTION.apply(value));
    }

    public static ResourceLocation c(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    public static void register() {
        addBasicValue(c("gems"), 24);
        addBasicValue(c("storage_blocks"), 64);
        addBasicValue(c("ores"), 4);
        addBasicValue(c("ingots"), 8);
        addBasicValue(c("nuggets"), 2);
        addBasicValue(c("dusts"), 4);
        addBasicValue(c("crops"), 4);
        addBasicValue(c("foods/golden"), 32);
        addBasicValue(c("tools"), 20);
        addBasicValue(c("armors"), 8);
        addBasicValue(c("music_discs"), 24);
    }

    public static int getValue(ItemStack stack) {
        AtomicInteger value = new AtomicInteger();
        ItemValues.TAGS.forEach((itemTagKey, function) -> {
            if (stack.getTags().anyMatch(tag -> tag.equals(itemTagKey))) {
                value.set(Math.max(value.get(), function.apply(stack)));
            }
        });

        if (value.get() <= 0) {
            return DEFAULT_FUNCTION.apply(1).apply(stack);
        }

        return value.get();
    }
}
