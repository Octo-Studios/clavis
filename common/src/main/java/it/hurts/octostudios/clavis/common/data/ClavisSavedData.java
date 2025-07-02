package it.hurts.octostudios.clavis.common.data;

import com.mojang.serialization.DataResult;
import it.hurts.octostudios.octolib.OctoLib;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;

public class ClavisSavedData extends SavedData {
    private static final String DATA_NAME = "clavis_locks";
    public static final SavedData.Factory<ClavisSavedData> FACTORY = new SavedData.Factory<>(
            ClavisSavedData::new,
            ClavisSavedData::load,
            null
    );

    private final CompoundTag dataTag = new CompoundTag();
    private List<Lock> locks = new ArrayList<>();

    public static ClavisSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private ClavisSavedData() {}

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.merge(dataTag);

        DataResult<Tag> result = Lock.CODEC.listOf().encodeStart(NbtOps.INSTANCE, locks);
        result.resultOrPartial(OctoLib.LOGGER::error).ifPresent(encodedTag -> {
            tag.put("Locks", encodedTag);
        });

        return tag;
    }

    private static ClavisSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        ClavisSavedData data = new ClavisSavedData();
        data.dataTag.merge(tag);

        if (tag.contains("Locks", Tag.TAG_LIST)) {
            DataResult<List<Lock>> result = Lock.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("Locks"));
            result.resultOrPartial(OctoLib.LOGGER::error).ifPresent(list -> data.locks.addAll(list));
        }

        return data;
    }

    public void addLock(Lock lock) {
        this.locks.add(lock);
        this.setDirty();
    }

    public void removeLock(Lock lock) {
        this.locks.remove(lock);
        this.setDirty();
    }
}