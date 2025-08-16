package it.hurts.octostudios.clavis.common.data;

import it.hurts.octostudios.clavis.common.Clavis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class ItemValues {
    public static final Function<Double, Function<ItemStack, Double>> DEFAULT_FUNCTION = value -> itemStack -> {
        double finalValue = value;

        for (ValueModifier modifier : ValueModifier.values()) {
            finalValue *= modifier.getModifier().apply(itemStack);
        }

        return finalValue * itemStack.getCount();
    };

//    public static void addBasicValue(ResourceLocation rl, double value) {
//        TAGS.put(tag(rl), value);
//    }

    public static TagKey<Item> tag(ResourceLocation tag) {
        return TagKey.create(Registries.ITEM, tag);
    }

    public static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, c(path));
    }

    public static ResourceLocation c(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    public static double getValue(ItemStack stack) {
        Map<String, Double> tags = Clavis.CONFIG.getValuableTags();

        AtomicReference<Double> value = new AtomicReference<>(0d);
        stack.getTags().forEach(tagKey -> value.set(value.get() + tags.getOrDefault(tagKey.location().toString(), 0d)));

        if (value.get() <= 0d) {
            return DEFAULT_FUNCTION.apply(Clavis.CONFIG.getDefaultBaseItemValue()).apply(stack);
        }

        return DEFAULT_FUNCTION.apply(value.get()).apply(stack);
    }
}
