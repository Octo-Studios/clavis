package it.hurts.octostudios.clavis.common.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Optional;

@Mixin(LootTable.class)
public interface LootTableAccessor {
    @Accessor
    Optional<ResourceLocation> getRandomSequence();

    @Invoker("getRandomItems")
    ObjectArrayList<ItemStack> invokeGetRandomItems(LootContext context);

    @Invoker("shuffleAndSplitItems")
    void invokeShuffleAndSplitItems(ObjectArrayList<ItemStack> stacks, int emptySlotsCount, RandomSource random);

    @Invoker("getAvailableSlots")
    List<Integer> invokeGetAvailableSlots(Container inventory, RandomSource random);
}
