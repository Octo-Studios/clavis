package it.hurts.shatterbyte.clavis.common.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Optional;

@Mixin(LootTable.class)
public interface LootTableAccessor {
//    @Invoker("<init>")
//    static LootTable init(LootContextParamSet paramSet, Optional<ResourceLocation> randomSequence, List<LootPool> pools, List<LootItemFunction> functions) {
//        return null;
//    }

    @Accessor
    Optional<ResourceLocation> getRandomSequence();

    @Accessor
    List<LootPool> getPools();

    @Invoker("getRandomItems")
    ObjectArrayList<ItemStack> invokeGetRandomItems(LootContext context);

    @Invoker("shuffleAndSplitItems")
    void invokeShuffleAndSplitItems(ObjectArrayList<ItemStack> stacks, int emptySlotsCount, RandomSource random);

    @Invoker("getAvailableSlots")
    List<Integer> invokeGetAvailableSlots(Container inventory, RandomSource random);

    @Accessor
    LootContextParamSet getParamSet();

    @Accessor
    List<LootItemFunction> getFunctions();
}
