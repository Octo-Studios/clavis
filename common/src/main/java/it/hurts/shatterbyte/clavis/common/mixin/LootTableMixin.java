package it.hurts.shatterbyte.clavis.common.mixin;

import it.hurts.shatterbyte.clavis.common.LockManager;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public class LootTableMixin {
    @Inject(method = "fill", at = @At("HEAD"), cancellable = true)
    private void injected(Container container, LootParams params, long seed, CallbackInfo ci) {
        if (container instanceof BlockEntity block && LockManager.isLocked(block.getLevel(), null, block.getBlockPos())) {
            ci.cancel();
        }
    }

}
