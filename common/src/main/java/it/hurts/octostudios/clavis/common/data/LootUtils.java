package it.hurts.octostudios.clavis.common.data;

import it.hurts.octostudios.clavis.common.mixin.LootTableAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static float calculateDifficulty(ServerLevel level, BlockPos pos, RandomizableContainer container) {
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(container.getLootTable());
        long seed = container.getLootTableSeed();

        Optional<ResourceLocation> randomSequence = ((LootTableAccessor) lootTable).getRandomSequence();

        LootParams.Builder builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos));
        LootContext actualLootContext = new LootContext.Builder(builder.create(LootContextParamSets.CHEST)).withOptionalRandomSeed(seed).create(randomSequence);

        ObjectArrayList<ItemStack> actualItems = ((LootTableAccessor) lootTable).invokeGetRandomItems(actualLootContext);

        AtomicInteger actualValue = new AtomicInteger();
        actualItems.forEach((stack) -> {
            actualValue.addAndGet(ItemValues.getValue(stack));
        });

        return actualValue.get() / 400f;
    }
}
