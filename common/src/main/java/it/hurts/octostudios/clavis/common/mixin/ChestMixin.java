package it.hurts.octostudios.clavis.common.mixin;

import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.ChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestMixin {
    @Inject(method = "candidatePartnerFacing", at = @At("HEAD"), cancellable = true)
    private void injected(BlockPlaceContext context, Direction direction, CallbackInfoReturnable<Direction> cir) {
        if (ClavisSavedData.isLocked(context.getClickedPos().relative(direction), context.getLevel())) {
            cir.setReturnValue(null);
        }
    }

}
