package it.hurts.octostudios.clavis.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DummyLootrCompat implements LootrCompatProxy {
    @Override
    public Container getEmptyInventory(BlockEntity blockEntity, ServerPlayer player) {
        return null;
    }

    @Override
    public boolean isLootrBlockEntity(BlockEntity blockEntity) {
        return false;
    }

    @Override
    public void performOpen(BlockEntity blockEntity, ServerPlayer player) {

    }
}
