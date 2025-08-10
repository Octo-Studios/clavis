package it.hurts.octostudios.clavis.common.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPool.class)
public interface LootPoolAccessor {
//    @Invoker("<init>")
//    static LootPool init(List<LootPoolEntryContainer> entries, List<LootItemCondition> conditions, List<LootItemFunction> functions, NumberProvider rolls, NumberProvider bonusRolls) {
//        return null;
//    }

    @Accessor
    List<LootItemFunction> getFunctions();

    @Accessor
    List<LootPoolEntryContainer> getEntries();

    @Accessor
    List<LootItemCondition> getConditions();

    @Accessor
    NumberProvider getRolls();

    @Accessor
    NumberProvider getBonusRolls();
}