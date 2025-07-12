package it.hurts.octostudios.clavis.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

@EqualsAndHashCode(callSuper = false)
@Getter
public class Lock {
    public static final Codec<Lock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(lock -> lock.uuid),
            Box.CODEC.fieldOf("box").forGetter(lock -> lock.box),
            Codec.FLOAT.fieldOf("difficulty").forGetter(lock -> lock.difficulty),
            Codec.LONG.fieldOf("seed").forGetter(lock -> lock.seed),
            Codec.BOOL.fieldOf("perPlayer").forGetter(lock -> lock.perPlayer),
            ResourceLocation.CODEC.listOf().optionalFieldOf("rules").forGetter(lock -> Optional.of(lock.rules))
    ).apply(instance, Lock::new));

    public static final Codec<Set<Lock>> SET_CODEC = Lock.CODEC.listOf().xmap(HashSet::new, ArrayList::new);

    // mandatory
    UUID uuid;
    Box box;
    float difficulty;
    long seed;
    boolean perPlayer;

    // override
    List<ResourceLocation> rules = new ArrayList<>();

    public Lock(UUID uuid, Box box, float difficulty, long seed, boolean perPlayer) {
        this.uuid = uuid;
        this.box = box;
        this.difficulty = difficulty;
        this.seed = seed;
        this.perPlayer = perPlayer;
    }

    public Lock(UUID uuid, Box box, float difficulty, long seed, boolean perPlayer, Optional<List<ResourceLocation>> rules) {
        this(uuid, box, difficulty, seed, perPlayer);
        this.rules = rules.orElse(new ArrayList<>());
    }
}
