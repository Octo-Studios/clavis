package it.hurts.octostudios.clavis.common.network;

import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.value.IntValue;
import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.network.packet.OpenLockpickingPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LockInteractionBlockers {
    public static EventResult onBreak(Level level, BlockPos pos, BlockState blockState, ServerPlayer serverPlayer, @Nullable IntValue intValue) {
        if (ClavisSavedData.isLocked(pos, level)) {
            return EventResult.interruptFalse();
        }

        return EventResult.pass();
    }

    public static void onBlow(Level level, Explosion explosion, List<Entity> entities) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ClavisSavedData data = ClavisSavedData.get(serverLevel);
        List<BlockPos> toNotBlow = new ArrayList<>(); // blow me... 🥀🥀🥀
        explosion.getToBlow().forEach(pos -> {
            if (!data.getLocksAt(pos).isEmpty()) {
                toNotBlow.add(pos);
            }
        });

        explosion.getToBlow().removeAll(toNotBlow);
    }

    public static EventResult onInteract(Player player, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        if (player.level().isClientSide) {
            if (ClavisSavedData.isLocked(pos, player.level())) {
                return EventResult.interruptFalse();
            }

            return EventResult.pass();
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        List<Lock> locks = ClavisSavedData.get(serverPlayer.serverLevel()).getLocksAt(pos);
        if (locks.isEmpty()) {
            return EventResult.pass();
        }

        NetworkManager.sendToPlayer(serverPlayer, new OpenLockpickingPacket(pos, locks.getFirst()));
        return EventResult.interruptFalse();
    }

    public static boolean onPiston(LevelAccessor level, BlockPos pos) {
        return !ClavisSavedData.isLocked(pos, (Level) level);
    }
}
