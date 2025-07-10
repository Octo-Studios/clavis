package it.hurts.octostudios.clavis.common.mixin;

import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerMixin implements RandomizableContainer {
    @Override
    public void unpackLootTable(@Nullable Player player) {
        if (ClavisSavedData.isLocked(getBlockPos(), getLevel())) {
            return;
        }

        RandomizableContainer.super.unpackLootTable(player);
    }
}
