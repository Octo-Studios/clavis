package it.hurts.shatterbyte.clavis.common;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.clavis.common.client.render.LockWorldRenderer;
import it.hurts.shatterbyte.clavis.common.data.ClavisSavedData;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.shatterbyte.clavis.common.data.UnlockDataStorage;
import it.hurts.shatterbyte.clavis.common.network.packet.RemoveLockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LockManager {
    public static UnlockDataStorage UNLOCK_STORAGE;

    public static void addLock(ServerLevel level, Lock lock) {
        if (level == null)
            return;
        ClavisSavedData data = ClavisSavedData.get(level);
        data.addLock(lock, level);
    }

    public static void removeLock(ServerLevel level, Lock lock) {
        if (level == null)
            return;
        ClavisSavedData data = ClavisSavedData.get(level);
        data.removeLock(lock, level);
    }

    public static void unlock(ServerLevel level, @Nullable ServerPlayer player, Lock lock) {
        if (level == null)
            return;
        
        if (!lock.isPerPlayer()) {
            removeLock(level, lock);
            return;
        }

        if (player == null) {
            return;
        }

        UNLOCK_STORAGE.getData().unlockedLocks
                .computeIfAbsent(player.getUUID(), uuid -> new ArrayList<>())
                .add(lock.getUuid());

        NetworkManager.sendToPlayer(player, new RemoveLockPacket(lock));
    }

    public static List<Lock> getLocksAt(ServerLevel level, @Nullable ServerPlayer player, ChunkPos pos) {
        if (level == null)
            return List.of();
        
        ClavisSavedData data = ClavisSavedData.get(level);
        List<Lock> locks = data.getLocksAt(pos);

        if (player == null) {
            return locks;
        }

        List<UUID> unlocked = UNLOCK_STORAGE.getData().unlockedLocks.getOrDefault(player.getUUID(), new ArrayList<>());
        locks.removeIf(lock -> lock.isPerPlayer() && unlocked.contains(lock.getUuid()));

        return locks;
    }

    public static List<Lock> getLocksAt(ServerLevel level, @Nullable ServerPlayer player, BlockPos pos) {
        if (level == null)
            return List.of();
        
        ChunkPos chunkPos = new ChunkPos(pos);
        List<Lock> chunkLocks = getLocksAt(level, player, chunkPos);

        return new ArrayList<>(chunkLocks.stream()
                .filter(lock -> lock.getBox().isInside(pos))
                .toList());
    }

    public static boolean isLocked(Level level, Player player, BlockPos pos) {
        if (level == null)
            return false;
        
        if (level.isClientSide) {
            return LockWorldRenderer.FOR_RENDERING.stream().anyMatch(lock -> lock.getBox().isInside(pos));
        } else if (level instanceof ServerLevel serverLevel) {
            return !LockManager.getLocksAt(serverLevel, (ServerPlayer) player, pos).isEmpty();
        }

        return false;
    }

    public static void load(ServerLevel serverLevel) {
        Path worldFolder = Platform.getGameFolder().resolve(serverLevel.getServer().getWorldPath(LevelResource.ROOT).getParent());
        LockManager.UNLOCK_STORAGE = new UnlockDataStorage(worldFolder);
        try {
            LockManager.UNLOCK_STORAGE.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(ServerLevel serverLevel) {
        if (LockManager.UNLOCK_STORAGE != null) {
            try {
                LockManager.UNLOCK_STORAGE.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
