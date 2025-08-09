package it.hurts.octostudios.clavis.common.network;

import it.hurts.octostudios.clavis.common.LockManager;
import it.hurts.octostudios.clavis.common.LootrCompat;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.data.LootUtils;
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

            float difficulty = LootUtils.calculateDifficulty(level, blockPos, randomizable, 0);
            if (difficulty < 0.05f) {
                return;
            }

            LockManager.addLock(level, new Lock(UUID.randomUUID(), new Box(blockPos), difficulty, randomizable.getLootTableSeed(), false));
        });
    }
}
