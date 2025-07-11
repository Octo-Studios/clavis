package it.hurts.octostudios.clavis.common.data;

import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ClavisPlayerDataManager {
    public static final String UNLOCKED_TAG = "clavis:unlocked_locks";

    // Save upon unlocking
    public static void unlockLock(ServerPlayer player, UUID lockUuid) {
        CompoundTag data = player.getEntityData();
        ListTag list = data.getList(UNLOCKED_TAG, Tag.TAG_INT_ARRAY);
        list.add(NbtUtils.createUUID(lockUuid));
        data.put(UNLOCKED_TAG, list);
    }

    // Check lock status
    public static boolean hasUnlocked(ServerPlayer player, UUID lockUuid) {
        CompoundTag data = player.getEntityData();
        ListTag list = data.getList(UNLOCKED_TAG, Tag.TAG_INT_ARRAY);
        for (Tag t : list) if (NbtUtils.loadUUID((IntArrayTag)t).equals(lockUuid)) return true;
        return false;
    }
}
