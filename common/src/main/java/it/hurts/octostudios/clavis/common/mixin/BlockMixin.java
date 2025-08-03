package it.hurts.octostudios.clavis.common.mixin;

import it.hurts.octostudios.clavis.common.LockManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class BlockMixin {
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    private void injected(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }

        Level level = (Level) (Object) this;
        if (LockManager.isLocked(level, null, pos) && level instanceof ServerLevel serverLevel) {
            LockManager.getLocksAt(serverLevel, null, pos).forEach(lock -> {
                LockManager.removeLock(serverLevel, lock);
            });
        }
    }
}
