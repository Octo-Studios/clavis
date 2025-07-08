package it.hurts.octostudios.clavis.common.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.DataResult;
import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.network.packet.RemoveLockPacket;
import it.hurts.octostudios.octolib.OctoLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClavisSavedData extends SavedData {
    private static final String DATA_NAME = "clavis_locks";
    public static final SavedData.Factory<ClavisSavedData> FACTORY = new SavedData.Factory<>(
            ClavisSavedData::new,
            ClavisSavedData::load,
            null
    );

    private final CompoundTag dataTag = new CompoundTag();
    private final Set<Lock> locks = new HashSet<>();
    private final Multimap<ChunkPos, Lock> lockLookupMap = HashMultimap.create();

    public static ClavisSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private ClavisSavedData() {}

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.merge(dataTag);

        DataResult<Tag> result = Lock.SET_CODEC.encodeStart(NbtOps.INSTANCE, locks);
        result.resultOrPartial(OctoLib.LOGGER::warn).ifPresent(encodedTag -> {
            tag.put("Locks", encodedTag);
        });

        return tag;
    }

    private static ClavisSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        ClavisSavedData data = new ClavisSavedData();
        data.dataTag.merge(tag);

        if (tag.contains("Locks", Tag.TAG_LIST)) {
            DataResult<Set<Lock>> result = Lock.SET_CODEC.parse(NbtOps.INSTANCE, tag.get("Locks"));
            result.resultOrPartial(OctoLib.LOGGER::warn).ifPresent(list -> {
                data.locks.addAll(list);
                list.forEach(data::indexLock);
            });
        }

        return data;
    }

    private void indexLock(Lock lock) {
        int minChunkX = lock.box.minX >> 4;
        int maxChunkX = lock.box.maxX >> 4;
        int minChunkZ = lock.box.minZ >> 4;
        int maxChunkZ = lock.box.maxZ >> 4;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                lockLookupMap.put(new ChunkPos(cx, cz), lock);
            }
        }
    }

    public void addLock(Lock lock, ServerLevel level) {
        this.locks.add(lock);
        this.indexLock(lock);
        this.setDirty();
    }

    public void removeLock(Lock lock, ServerLevel level) {
        locks.remove(lock);

        int minChunkX = lock.box.minX >> 4;
        int maxChunkX = lock.box.maxX >> 4;
        int minChunkZ = lock.box.minZ >> 4;
        int maxChunkZ = lock.box.maxZ >> 4;

        Set<ServerPlayer> toSend = new HashSet<>();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                lockLookupMap.remove(new ChunkPos(cx, cz), lock);

                int finalCx = cx;
                int finalCz = cz;
                toSend.addAll(level.getPlayers(player -> player.getChunkTrackingView().contains(finalCx, finalCz)));
            }
        }

        NetworkManager.sendToPlayers(toSend, new RemoveLockPacket(lock));

        this.setDirty();
    }

    public List<Lock> getLocksAt(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return lockLookupMap.get(chunkPos).stream()
                .filter(lock -> lock.box.isInside(pos))
                .toList();
    }

    public List<Lock> getLocksAt(ChunkPos chunkPos) {
        return new ArrayList<>(lockLookupMap.get(chunkPos));
    }
}