package it.hurts.shatterbyte.clavis.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.LockManager;
import it.hurts.shatterbyte.clavis.common.data.Box;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.shatterbyte.clavis.common.data.LootUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BlockEntityTicker.class)
public class BlockEntityTickerMixin {
    @Inject(require = 0, method = "replaceEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;setLootTable(Lnet/minecraft/resources/ResourceKey;J)V", shift = At.Shift.AFTER))
    private static void injected(Level level, BlockPos entityPos, RandomizableContainerBlockEntity be, BlockState replacement, ResourceKey<LootTable> table, CallbackInfo ci, @Local(name = "rbe") RandomizableContainerBlockEntity rbe) {
        if (level.isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        LockManager.getLocksAt(serverLevel, null, entityPos).forEach(lock -> LockManager.removeLock(serverLevel, lock));

        float difficulty = (float) LootUtils.calculateDifficulty(serverLevel, entityPos, rbe, 20, false, null);

        if (difficulty < Clavis.CONFIG.getDifficultyThreshold()) {
            return;
        }

        LockManager.addLock(serverLevel, new Lock(UUID.randomUUID(), new Box(entityPos), difficulty, rbe.getLootTableSeed(), true));
    }
}
