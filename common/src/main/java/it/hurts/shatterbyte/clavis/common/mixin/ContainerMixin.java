package it.hurts.shatterbyte.clavis.common.mixin;

import it.hurts.shatterbyte.clavis.common.LockManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BaseContainerBlockEntity.class)
public abstract class ContainerMixin extends BlockEntity implements Container {
    public ContainerMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (LockManager.isLocked(getLevel(), null, getBlockPos())) {
            return false;
        }

        return Container.super.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        if (LockManager.isLocked(getLevel(), null, getBlockPos())) {
            return false;
        }

        return Container.super.canTakeItem(target, slot, stack);
    }
}
