package it.hurts.shatterbyte.clavis.common.data;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.registry.ValueModifierRegistry;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class ItemValues {
    public static final BiFunction<ItemStack, Double, Double> DEFAULT_FUNCTION = (itemStack, value) -> {
        double finalValue = value;

        for (ValueModifier modifier : ValueModifierRegistry.MODIFIERS.values()) {
            finalValue *= modifier.apply(itemStack);
        }

        return finalValue * itemStack.getCount();
    };

    public static double getValue(ItemStack stack) {
        Map<String, Double> tags = Clavis.CONFIG.getValuableTags();

        AtomicReference<Double> value = new AtomicReference<>(0d);
        stack.getTags().forEach(tagKey -> value.set(value.get() + tags.getOrDefault(tagKey.location().toString(), 0d)));

        if (value.get() <= 0d) {
            return DEFAULT_FUNCTION.apply(stack, Clavis.CONFIG.getDefaultBaseItemValue());
        }

        return DEFAULT_FUNCTION.apply(stack, value.get());
    }
}
