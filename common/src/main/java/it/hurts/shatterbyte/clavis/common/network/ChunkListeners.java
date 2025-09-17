package it.hurts.shatterbyte.clavis.common.network;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.LockManager;
import it.hurts.shatterbyte.clavis.common.LootrCompat;
import it.hurts.shatterbyte.clavis.common.data.Box;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.shatterbyte.clavis.common.data.LootUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.UUID;

public class ChunkListeners {
    public static void onGenerate(ServerLevel level, LevelChunk levelChunk) {
        levelChunk.getBlockEntities().forEach((blockPos, blockEntity) -> {
            if (!(blockEntity instanceof RandomizableContainerBlockEntity randomizable && randomizable.getLootTable() != null && !LootrCompat.COMPAT.isLootrBlockEntity(randomizable))) {
                return;
            }

            if (randomizable instanceof DispenserBlockEntity) {
                return;
            }

            float difficulty = (float) LootUtils.calculateDifficulty(level, blockPos, randomizable, 0, false, null);
            if (difficulty < Clavis.CONFIG.getDifficultyThreshold()) {
                return;
            }

            LockManager.addLock(level, new Lock(UUID.randomUUID(), new Box(blockPos), difficulty, randomizable.getLootTableSeed(), false));
        });
    }
}
