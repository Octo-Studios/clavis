package it.hurts.octostudios.clavis.common.data;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ValueModifier {
    double apply(ItemStack stack);
}
