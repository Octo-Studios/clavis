package it.hurts.shatterbyte.clavis.common;

import it.hurts.shatterbyte.clavis.common.client.render.LockWorldRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientLockChecker implements LockChecker {
    @Override
    public boolean isLocked(Level level, Player player, BlockPos pos) {
        return LockWorldRenderer.FOR_RENDERING.stream().anyMatch(lock -> lock.getBox().isInside(pos));
    }
}
