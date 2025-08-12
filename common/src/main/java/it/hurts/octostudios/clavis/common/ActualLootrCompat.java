package it.hurts.octostudios.clavis.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.LootrBarrelBlock;

public class ActualLootrCompat implements LootrCompatProxy {
    @Override
    public Container getEmptyInventory(BlockEntity blockEntity, ServerPlayer player) {
        if (blockEntity instanceof ILootrBlockEntity lootrBlockEntity) {
            return LootrAPI.getInventory(lootrBlockEntity, player, (provider, player1, inventory) -> {});
        }

        return null;
    }

    @Override
    public boolean isLootrBlockEntity(BlockEntity blockEntity) {
        return blockEntity instanceof ILootrBlockEntity;
    }

    @Override
    public void performOpen(BlockEntity blockEntity, ServerPlayer player) {
        if (blockEntity instanceof ILootrBlockEntity lootrBlockEntity) {
            lootrBlockEntity.performOpen(player);
        }
    }

    @Override
    public boolean isStateLootrBarrel(BlockState state) {
        return state.getBlock() instanceof LootrBarrelBlock;
    }
}
