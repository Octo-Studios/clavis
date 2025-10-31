package it.hurts.shatterbyte.clavis.neoforge;

import com.mojang.authlib.GameProfile;
import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.clavis.common.data.LootUtils;
import it.hurts.shatterbyte.clavis.common.mixin.LootPoolAccessor;
import it.hurts.shatterbyte.clavis.common.mixin.LootTableAccessor;
import lombok.SneakyThrows;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.lang.reflect.Constructor;
import java.util.*;

public class PlatformLootImpl {
    private static final UUID DEFAULT_UUID = UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77");
    private static final GameProfile DEFAULT_PROFILE = new GameProfile(DEFAULT_UUID, "[Minecraft]");

    private static Map<ServerLevel, Player> PLAYER_CACHE = new HashMap<>();

    public static Player getFakePlayer(ServerLevel level) {
        if (!PLAYER_CACHE.containsKey(level)) {
            PLAYER_CACHE.put(level, new FakePlayer(level, DEFAULT_PROFILE));
        }

        return PLAYER_CACHE.get(level);
    }

    @SneakyThrows
    public static LootTable stripTreasureMaps(LootTable lootTable, ResourceLocation lootTableId) {
        List<LootPool> filteredPools = new ArrayList<>();
        LootTableAccessor lootTableAccessor = (LootTableAccessor) lootTable;

        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            List<LootPoolEntryContainer> filteredEntries = new ArrayList<>();
            LootPoolAccessor accessor = ((LootPoolAccessor) pool);
            for (LootPoolEntryContainer entry : accessor.clavis$getEntries()) {
                if (!LootUtils.isTreasureMapEntry(entry)) {
                    filteredEntries.add(entry);
                }
            }
            LootPool lootPool;

            Constructor<LootPool> constructor = LootPool.class.getDeclaredConstructor(List.class, List.class, List.class, NumberProvider.class, NumberProvider.class, Optional.class);
            constructor.setAccessible(true);
            lootPool = constructor.newInstance(
                    filteredEntries,
                    accessor.clavis$getConditions(),
                    accessor.clavis$getFunctions(),
                    accessor.clavis$getRolls(),
                    accessor.clavis$getBonusRolls(),
                    Optional.empty()
            );

            if (!filteredEntries.isEmpty()) {
                filteredPools.add(lootPool);
            }
        }

        Constructor<LootTable> constructor = LootTable.class.getDeclaredConstructor(LootContextParamSet.class, Optional.class, List.class, List.class);
        constructor.setAccessible(true);
        LootTable table = constructor.newInstance(lootTableAccessor.getParamSet(), lootTableAccessor.getRandomSequence(), filteredPools, lootTableAccessor.getFunctions());
        table.setLootTableId(lootTableId);
        return table;
    }
}
