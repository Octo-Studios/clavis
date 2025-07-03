package it.hurts.octostudios.clavis.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class Lock {
    public static final Codec<Lock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Box.CODEC.fieldOf("box").forGetter(lock -> lock.box),
            Codec.FLOAT.fieldOf("difficulty").forGetter(lock -> lock.difficulty),
            Codec.LONG.fieldOf("seed").forGetter(lock -> lock.seed),
            ResourceLocation.CODEC.listOf().optionalFieldOf("rules").forGetter(lock -> Optional.ofNullable(lock.rules))
    ).apply(instance, Lock::new));

    // mandatory
    Box box;
    float difficulty;
    long seed;

    // override
    List<ResourceLocation> rules;

    public Lock(Box box, float difficulty, long seed) {
        this.box = box;
        this.difficulty = difficulty;
        this.seed = seed;
    }
}
