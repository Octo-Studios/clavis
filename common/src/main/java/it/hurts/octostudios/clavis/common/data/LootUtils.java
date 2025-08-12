package it.hurts.octostudios.clavis.common.data;

import dev.architectury.platform.Platform;
import it.hurts.octostudios.clavis.common.LockManager;
import it.hurts.octostudios.clavis.common.LootrCompat;
import it.hurts.octostudios.clavis.common.mixin.LootPoolAccessor;
import it.hurts.octostudios.clavis.common.mixin.LootPoolSingletonContainerAccessor;
import it.hurts.octostudios.clavis.common.mixin.LootTableAccessor;
import it.hurts.octostudios.octolib.OctoLib;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.SneakyThrows;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

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

    public static double calculateDifficulty(ServerLevel level, BlockPos pos, RandomizableContainer container, int randomIterations) {
        double value = 0f;
        int totalIterations = 0;
        Random random = new Random(container.getLootTableSeed());

        LootTable rawLootTable = level.getServer().reloadableRegistries().getLootTable(container.getLootTable());
        LootTable noMapsTable = stripTreasureMaps(rawLootTable);
        LootTableAccessor accessor = (LootTableAccessor) noMapsTable;

        Optional<ResourceLocation> randomSequence = accessor.getRandomSequence();

        LootParams.Builder builder = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, pos == null ? Vec3.ZERO : Vec3.atCenterOf(pos));

        Function<Long, LootContext> makeCtx = seed -> new LootContext.Builder(builder.create(LootContextParamSets.CHEST)).withOptionalRandomSeed(seed).create(randomSequence);

        do {
            long currentSeed = totalIterations > 0 ? random.nextLong() : container.getLootTableSeed();

            LootContext actualLootContext = makeCtx.apply(currentSeed);
            RandomSource randomSource = actualLootContext.getRandom();

            ObjectArrayList<ItemStack> actualItems = buildMainItemList(accessor, makeCtx, actualLootContext, currentSeed, 1f, randomSource);
            actualItems.size(Math.min(actualItems.size(), container.getContainerSize()));

            double iterationValue = 0d;
            for (ItemStack stack : actualItems) {
                if (stack == null) {
                    continue;
                }

                iterationValue += ItemValues.getValue(stack);
            }

            totalIterations++;
            value += iterationValue;
        } while (totalIterations < randomIterations);

        value /= totalIterations;
        return Math.clamp(value / 192f, 0.01f, 1.5f);
    }

    private static final long SEED_STEP = 31571L;

    public static void unlockWithQuality(ServerLevel level, ServerPlayer player, BlockPos blockPos, Lock lock, float quality) {
        LockManager.unlock(level, player, lock);

        // validate block entity and loot table
        if (!(level.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity randomizable && randomizable.getLootTable() != null)) {
            return;
        }

        ResourceKey<LootTable> resourceKey = randomizable.getLootTable();
        LootTable lootTable = getLootTableSafe(level, resourceKey);
        if (lootTable == null) {
            // either warn or return — original code assumes loot table exists
            OctoLib.LOGGER.warn("loot table not found for {}", resourceKey);
            return;
        }

        LootTableAccessor accessor = (LootTableAccessor) lootTable;
        CriteriaTriggers.GENERATE_LOOT.trigger(player, resourceKey);

        LootParams.Builder baseBuilder = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos));
        baseBuilder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);

        // prepare container (handle Lootr compatibility)
        boolean isLootr = LootrCompat.COMPAT.isLootrBlockEntity(randomizable);
        Container container = isLootr ? LootrCompat.COMPAT.getEmptyInventory(randomizable, player) : randomizable;
        long lootTableSeed = isLootr ? player.getUUID().getLeastSignificantBits() + randomizable.getLootTableSeed() : randomizable.getLootTableSeed();

        Function<Long, LootContext> makeCtx = createLootContextFactory(baseBuilder, accessor);
        LootContext defaultContext = makeCtx.apply(lootTableSeed);
        RandomSource randomSource = defaultContext.getRandom();

        // build item list according to quality
        ObjectArrayList<ItemStack> mainList = buildMainItemList(accessor, makeCtx, defaultContext, lootTableSeed, quality, randomSource);

        // get available slots and split/shuffle items for those slots
        List<Integer> availableSlots = accessor.invokeGetAvailableSlots(randomizable, randomSource);
        accessor.invokeShuffleAndSplitItems(mainList, availableSlots.size(), randomSource);

        if (container == null) {
            OctoLib.LOGGER.warn("container is null");
            return;
        }

        // populate container from the prepared mainList + availableSlots (last-in behavior preserved)
        populateContainerFromList(container, mainList, availableSlots);

        LootrCompat.COMPAT.performOpen(randomizable, player);
    }

    private static LootTable getLootTableSafe(ServerLevel level, ResourceKey<LootTable> resourceKey) {
        try {
            return level.getServer().reloadableRegistries().getLootTable(resourceKey);
        } catch (Exception e) {
            OctoLib.LOGGER.warn("error loading loot table {}: {}", resourceKey, e.getMessage());
            return null;
        }
    }

    private static Function<Long, LootContext> createLootContextFactory(LootParams.Builder baseBuilder, LootTableAccessor accessor) {
        return seedOffset -> new LootContext.Builder(baseBuilder.create(LootContextParamSets.CHEST)).withOptionalRandomSeed(seedOffset).create(accessor.getRandomSequence());
    }

    private static ObjectArrayList<ItemStack> buildMainItemList(LootTableAccessor accessor, Function<Long, LootContext> makeCtx, LootContext defaultContext, long lootTableSeed, float quality, RandomSource randomSource) {
        ObjectArrayList<ItemStack> mainList = new ObjectArrayList<>();

        if (quality >= 1f) {
            int fullCopies = (int) quality;
            float fraction = quality - fullCopies;

            for (int i = 0; i < fullCopies; i++) {
                long seed = lootTableSeed + i * SEED_STEP;
                LootContext fullCtx = makeCtx.apply(seed);
                mainList.addAll(accessor.invokeGetRandomItems(fullCtx));
            }

            if (fraction > 0f) {
                long seed = lootTableSeed + SEED_STEP * fullCopies;
                LootContext fracCtx = makeCtx.apply(seed);
                ObjectArrayList<ItemStack> fractionalItems = accessor.invokeGetRandomItems(fracCtx);
                LootUtils.shrinkStacks(fractionalItems, fraction, randomSource);
                mainList.addAll(fractionalItems);
            }
        } else { // quality < 1f (including <= 0)
            mainList.addAll(accessor.invokeGetRandomItems(defaultContext));
            LootUtils.shrinkStacks(mainList, quality, randomSource);
        }

        // --- merge stacks of the same item+components into aggregated counts ---
        ObjectArrayList<ItemStack> aggregated = new ObjectArrayList<>();
        for (ItemStack stack : mainList) {
            if (stack.isEmpty()) {
                continue;
            }

            boolean merged = false;
            for (ItemStack a : aggregated) {
                if (ItemStack.isSameItemSameComponents(a, stack)) {
                    // combine counts
                    a.grow(stack.getCount());
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                aggregated.add(stack.copy());
            }
        }

        // --- split aggregated totals into proper stacks respecting max stack size ---
        ObjectArrayList<ItemStack> finalList = new ObjectArrayList<>();
        for (ItemStack agg : aggregated) {
            int total = agg.getCount();
            int max = agg.getMaxStackSize();
            while (total > 0) {
                int take = Math.min(total, max);
                ItemStack piece = agg.copy();
                piece.setCount(take);
                finalList.add(piece);
                total -= take;
            }
        }

        return finalList;
    }

    private static void populateContainerFromList(Container container, ObjectArrayList<ItemStack> items, List<Integer> availableSlots) {
        // preserve original behavior: removeLast() for slot selection
        for (ItemStack itemStack : items) {
            if (availableSlots.isEmpty()) {
                return;
            }
            int slotIndex = availableSlots.removeLast();
            if (itemStack.isEmpty()) {
                container.setItem(slotIndex, ItemStack.EMPTY);
            } else {
                container.setItem(slotIndex, itemStack);
            }
        }
    }

    @SneakyThrows
    private static LootTable stripTreasureMaps(LootTable lootTable) {
        List<LootPool> filteredPools = new ArrayList<>();
        LootTableAccessor lootTableAccessor = (LootTableAccessor) lootTable;

        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            List<LootPoolEntryContainer> filteredEntries = new ArrayList<>();
            LootPoolAccessor accessor = ((LootPoolAccessor) pool);
            for (LootPoolEntryContainer entry : accessor.getEntries()) {
                if (!isTreasureMapEntry(entry)) {
                    filteredEntries.add(entry);
                }
            }
            LootPool lootPool;

            if (Platform.isNeoForge()) {
                Constructor<LootPool> constructor = LootPool.class.getDeclaredConstructor(List.class, List.class, List.class, NumberProvider.class, NumberProvider.class, Optional.class);
                constructor.setAccessible(true);
                lootPool = constructor.newInstance(
                        filteredEntries,
                        accessor.getConditions(),
                        accessor.getFunctions(),
                        accessor.getRolls(),
                        accessor.getBonusRolls(),
                        Optional.empty()
                );
            } else {
                Constructor<LootPool> constructor = LootPool.class.getDeclaredConstructor(List.class, List.class, List.class, NumberProvider.class, NumberProvider.class);
                constructor.setAccessible(true);
                lootPool = constructor.newInstance(
                        filteredEntries,
                        accessor.getConditions(),
                        accessor.getFunctions(),
                        accessor.getRolls(),
                        accessor.getBonusRolls()
                );
            }

            if (!filteredEntries.isEmpty()) {
                filteredPools.add(lootPool);
            }
        }

        Constructor<LootTable> constructor = LootTable.class.getDeclaredConstructor(LootContextParamSet.class, Optional.class, List.class, List.class);
        constructor.setAccessible(true);
        return constructor.newInstance(lootTableAccessor.getParamSet(), lootTableAccessor.getRandomSequence(), filteredPools, lootTableAccessor.getFunctions());
    }

    private static boolean isTreasureMapEntry(LootPoolEntryContainer entry) {
        if (entry instanceof LootItem lootItem) {
            LootPoolSingletonContainerAccessor containerAccessor = (LootPoolSingletonContainerAccessor) lootItem;
            return containerAccessor.getFunctions().stream().anyMatch(f -> f instanceof ExplorationMapFunction);
        }
        return false;
    }

    public static int getColorForDifficulty(float difficulty) {
        return difficulty < 0.33f ? 0xff33ff22 : difficulty < 0.66f ? 0xffffcc00 : 0xffff0011;
    }
}
