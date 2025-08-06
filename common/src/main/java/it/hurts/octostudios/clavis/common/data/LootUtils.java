package it.hurts.octostudios.clavis.common.data;

import it.hurts.octostudios.clavis.common.mixin.LootTableAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

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

    public static float calculateDifficulty(MinecraftServer server, ServerLevel level, @Nullable BlockPos pos, ResourceKey<LootTable> lootTableKey, long seed, int randomIterations) {
        float value = 0f;
        int totalIterations = 0;
        Random random = new Random(seed);

        LootTable lootTable = server.reloadableRegistries().getLootTable(lootTableKey);
        Optional<ResourceLocation> randomSequence = ((LootTableAccessor) lootTable).getRandomSequence();
        LootParams.Builder builder = new LootParams.Builder(level);
        builder.withParameter(LootContextParams.ORIGIN, pos == null ? Vec3.ZERO : Vec3.atCenterOf(pos));

        do {
            long currentSeed = totalIterations > 0 ? random.nextLong() : seed;
            LootContext actualLootContext = new LootContext.Builder(builder.create(LootContextParamSets.CHEST)).withOptionalRandomSeed(currentSeed).create(randomSequence);

            ObjectArrayList<ItemStack> actualItems = ((LootTableAccessor) lootTable).invokeGetRandomItems(actualLootContext);

            AtomicReference<Float> actualValue = new AtomicReference<>(0f);
            actualItems.forEach((stack) -> {
                actualValue.updateAndGet(f -> ItemValues.getValue(stack) + f);
            });

            totalIterations++;
            value += actualValue.get();
        } while (totalIterations < randomIterations);

        value /= totalIterations;

        return Math.min(value / 128f, 2f);
    }

    public static float calculateDifficulty(ServerLevel level, BlockPos pos, RandomizableContainer container, int randomIterations) {
        return calculateDifficulty(level.getServer(), level, pos, container.getLootTable(), container.getLootTableSeed(), randomIterations);
    }
}
