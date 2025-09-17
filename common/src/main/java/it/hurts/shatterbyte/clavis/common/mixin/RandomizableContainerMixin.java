package it.hurts.shatterbyte.clavis.common.mixin;

import it.hurts.shatterbyte.clavis.common.LockManager;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerMixin implements RandomizableContainer {
    @Override
    public void unpackLootTable(@Nullable Player player) {
        if (LockManager.isLocked(getLevel(), player, getBlockPos())) {
            return;
        }

        RandomizableContainer.super.unpackLootTable(player);
    }
}
