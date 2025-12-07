package it.hurts.shatterbyte.clavis.common.compat.lootr;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.LockManager;
import it.hurts.shatterbyte.clavis.common.data.Box;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.shatterbyte.clavis.common.data.LootUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.processor.ILootrBlockEntityProcessor;

import java.util.UUID;

public class LockPostProcessor implements ILootrBlockEntityProcessor.Post {
  @Override
  public void process(ServerLevel serverLevel, BlockPos entityPos, BlockEntity rbe, BlockState blockState, ResourceKey<LootTable> resourceKey, long seed) {
    if (resourceKey == null) {
      return;
    }

    if (!(rbe instanceof RandomizableContainer container)) {
      return;
    }

    LockManager.getLocksAt(serverLevel, null, entityPos).forEach(lock -> LockManager.removeLock(serverLevel, lock));

    float difficulty = (float) LootUtils.calculateDifficulty(serverLevel, entityPos, container, 20, false, null);

    if (difficulty < Clavis.CONFIG.getDifficultyThreshold()) {
      return;
    }

    LockManager.addLock(serverLevel, new Lock(UUID.randomUUID(), new Box(entityPos), difficulty, seed, true));
  }
}
