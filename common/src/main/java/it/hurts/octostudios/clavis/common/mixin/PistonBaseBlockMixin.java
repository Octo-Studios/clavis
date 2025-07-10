package it.hurts.octostudios.clavis.common.mixin;

import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
    @Inject(method = "isPushable", at = @At("RETURN"), cancellable = true)
    private static void injected(BlockState state, Level level, BlockPos pos, Direction movementDirection, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && ClavisSavedData.isLocked(pos, level)) {
            cir.setReturnValue(false);
        }
    }
}
