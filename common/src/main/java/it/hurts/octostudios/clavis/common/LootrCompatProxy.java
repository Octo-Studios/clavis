package it.hurts.octostudios.clavis.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface LootrCompatProxy {
    Container getEmptyInventory(BlockEntity blockEntity, ServerPlayer player);
    boolean isLootrBlockEntity(BlockEntity blockEntity);
    void performOpen(BlockEntity blockEntity, ServerPlayer player);
}
