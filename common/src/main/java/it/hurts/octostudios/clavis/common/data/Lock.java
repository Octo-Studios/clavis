package it.hurts.octostudios.clavis.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Lock {
    public static final Codec<Lock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Box.CODEC.fieldOf("box").forGetter(lock -> lock.box),
            Codec.FLOAT.fieldOf("difficulty").forGetter(lock -> lock.difficulty)
    ).apply(instance, Lock::new));

    Box box;
    float difficulty;
}
