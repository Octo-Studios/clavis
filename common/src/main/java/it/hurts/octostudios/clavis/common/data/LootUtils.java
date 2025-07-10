package it.hurts.octostudios.clavis.common.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public class LootUtils {
    public static void shrinkStacks(ObjectArrayList<ItemStack> stacks, double factor, RandomSource random) {
        if (factor <= 0) {
            stacks.clear();
            return;
        }

        for (int i = stacks.size() - 1; i >= 0; i--) {
            ItemStack stack = stacks.get(i);
            int original = stack.getCount();
            double scaled = original * factor;
            int base = (int) Math.floor(scaled);

            if (random.nextDouble() < (scaled - base)) {
                base++;
            }
            if (base <= 0) {
                stacks.remove(i);
            } else {
                stack.setCount(base);
            }
        }
    }
}
