package it.hurts.octostudios.clavis.common.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
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
        addBasicValue(c("gems"), 32);
        addBasicValue(c("storage_blocks"), 64);
        addBasicValue(c("ores"), 16);
        addBasicValue(c("ingots"), 8);
        addBasicValue(c("nuggets"), 2);
        addBasicValue(c("dusts"), 4);
        addBasicValue(c("crops"), 4);
        addBasicValue(c("foods"), 5);
        addBasicValue(c("foods/golden"), 64);
        addBasicValue(c("tools"), 20);
        addBasicValue(c("armor"), 24);
    }
}
