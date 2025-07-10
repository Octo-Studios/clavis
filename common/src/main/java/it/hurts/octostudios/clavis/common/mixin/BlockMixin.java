package it.hurts.octostudios.clavis.common.mixin;

import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class BlockMixin {
//    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
//    private void injected(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
//        if (ClavisSavedData.isLocked(pos, (Level) (Object) this)) {
//            cir.setReturnValue(false);
//        }
//    }
}
