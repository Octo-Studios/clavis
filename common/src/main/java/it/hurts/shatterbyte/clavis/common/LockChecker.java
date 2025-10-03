package it.hurts.shatterbyte.clavis.common;

import it.hurts.shatterbyte.clavis.common.client.render.LockWorldRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface LockChecker {
    boolean isLocked(Level level, Player player, BlockPos pos);
}
