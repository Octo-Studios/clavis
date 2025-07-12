package it.hurts.octostudios.clavis.common.data;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UnlockDataStorage {
    private static final String FILE_NAME = "unlocked_locks.dat";

    private final Path worldFolder;

    @Getter
    private UnlockData data;

    public UnlockDataStorage(Path worldFolder) {
        this.worldFolder = worldFolder;
    }

    public void load() throws IOException {
        Path file = worldFolder.resolve(FILE_NAME);
        if (Files.exists(file)) {
            try (InputStream is = Files.newInputStream(file)) {
                CompoundTag tag = NbtIo.readCompressed(is, NbtAccounter.unlimitedHeap());
                data = UnlockData.fromNbt(tag);
            }
        } else {
            data = new UnlockData();
        }
    }

    public void save() throws IOException {
        Path file = worldFolder.resolve(FILE_NAME);
        try (OutputStream os = Files.newOutputStream(file)) {
            NbtIo.writeCompressed(data.toNbt(), os);
        }
    }
}
