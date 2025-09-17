package it.hurts.shatterbyte.clavis.common.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;

import java.util.*;

public class UnlockData {
    public final Map<UUID, List<UUID>> unlockedLocks = new HashMap<>();

    // save to NBT compound
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();

        for (Map.Entry<UUID, List<UUID>> entry : unlockedLocks.entrySet()) {
            ListTag locksList = new ListTag();
            for (UUID lockUUID : entry.getValue()) {
                locksList.add(NbtUtils.createUUID(lockUUID));
            }

            tag.put(entry.getKey().toString(), locksList);
        }

        return tag;
    }

    // load from NBT compound
    public static UnlockData fromNbt(CompoundTag tag) {
        UnlockData data = new UnlockData();

        for (String key : tag.getAllKeys()) {
            UUID playerUUID = UUID.fromString(key);

            List<UUID> lockUUIDs = new ArrayList<>();
            ListTag locksList = tag.getList(key, ListTag.TAG_INT_ARRAY);
            locksList.forEach(lockUUID -> {
                lockUUIDs.add(NbtUtils.loadUUID(lockUUID));
            });

            data.unlockedLocks.put(playerUUID, lockUUIDs);
        }

        return data;
    }
}
