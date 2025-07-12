package it.hurts.octostudios.clavis.common.network;

import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.value.IntValue;
import it.hurts.octostudios.clavis.common.LockManager;
import it.hurts.octostudios.clavis.common.network.packet.CheckIfLockedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LockInteractionBlockers {
    public static EventResult onBreak(Level level, BlockPos pos, BlockState blockState, ServerPlayer serverPlayer, @Nullable IntValue intValue) {
        if (LockManager.isLocked(level, serverPlayer, pos)) {
            return EventResult.interruptFalse();
        }

        return EventResult.pass();
    }

    public static void onBlow(Level level, Explosion explosion, List<Entity> entities) {
        if (level.isClientSide()) {
            return;
        }

        List<BlockPos> toNotBlow = new ArrayList<>(); // blow me... 🥀🥀🥀
        explosion.getToBlow().forEach(pos -> {
            if (!LockManager.getLocksAt((ServerLevel) level, null, pos).isEmpty()) {
                toNotBlow.add(pos);
            }
        });

        explosion.getToBlow().removeAll(toNotBlow);
    }

    public static EventResult onInteract(Player player, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        if (player.level().isClientSide()) {
            NetworkManager.sendToServer(new CheckIfLockedPacket(pos));
            return cancelInteraction(player, interactionHand, pos, direction);
        }

        return EventResult.pass();
    }

    public static EventResult cancelInteraction(Player player, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        if (LockManager.isLocked(player.level(), player, pos)) {
            return EventResult.interruptFalse();
        }

        return EventResult.pass();
    }
}
