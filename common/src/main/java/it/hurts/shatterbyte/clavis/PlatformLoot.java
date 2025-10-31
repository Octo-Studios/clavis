package it.hurts.shatterbyte.clavis;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlatformLoot {
    @ExpectPlatform
    public static Player getFakePlayer(ServerLevel level) {
        throw new RuntimeException("No platform-specific fake player found");
    }

    @ExpectPlatform
    public static LootTable stripTreasureMaps(LootTable lootTable, ResourceLocation lootTableId) {
        throw new RuntimeException("No platform-specific treasure map stripping method found");
    }
}
