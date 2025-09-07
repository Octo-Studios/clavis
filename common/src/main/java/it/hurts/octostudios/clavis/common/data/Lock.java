package it.hurts.octostudios.clavis.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.hurts.octostudios.clavis.common.Clavis;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

@EqualsAndHashCode(callSuper = false)
public class Lock {
    public static final ResourceLocation DEFAULT = Clavis.path("default");

    public static final Codec<Lock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(lock -> lock.uuid),
            Box.CODEC.fieldOf("box").forGetter(lock -> lock.box),
            Codec.FLOAT.fieldOf("difficulty").forGetter(lock -> lock.difficulty),
            Codec.LONG.fieldOf("seed").forGetter(lock -> lock.seed),
            Codec.BOOL.fieldOf("perPlayer").forGetter(lock -> lock.perPlayer),
            ResourceLocation.CODEC.listOf().optionalFieldOf("rules", new ArrayList<>()).forGetter(lock -> lock.rules),
            ResourceLocation.CODEC.optionalFieldOf("type", DEFAULT).forGetter(lock -> lock.type)
    ).apply(instance, Lock::new));

    public static final Codec<Set<Lock>> SET_CODEC = Lock.CODEC.listOf().xmap(HashSet::new, ArrayList::new);

    // mandatory
    @Getter UUID uuid;
    @Getter Box box;
    @Getter float difficulty;
    @Getter long seed;
    @Getter boolean perPlayer;

    // override
    ResourceLocation type;
    @Getter List<ResourceLocation> rules;

    public ResourceLocation getType(Level level) {
        return type.equals(Lock.DEFAULT) ?
                ResourceLocation.parse(Clavis.CONFIG.getMinigameType().getOrDefault(level.dimension().location().toString(), "clavis:gear")) :
                type;
    }

    public Lock(UUID uuid, Box box, float difficulty, long seed, boolean perPlayer) {
        this.uuid = uuid;
        this.box = box;
        this.difficulty = difficulty;
        this.seed = seed;
        this.perPlayer = perPlayer;
        this.type = DEFAULT;
        this.rules = new ArrayList<>();
    }

    public Lock(UUID uuid, Box box, float difficulty, long seed, boolean perPlayer, List<ResourceLocation> rules, ResourceLocation type) {
        this.uuid = uuid;
        this.box = box;
        this.difficulty = difficulty;
        this.seed = seed;
        this.perPlayer = perPlayer;
        this.type = type;
        this.rules = rules;
    }
}
