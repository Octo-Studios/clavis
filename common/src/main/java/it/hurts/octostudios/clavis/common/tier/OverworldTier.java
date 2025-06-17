package it.hurts.octostudios.clavis.common.tier;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Random;

public record OverworldTier(int minPinCount, int maxPinCount, float minArrowRotationSpeed, float maxArrowRotationSpeed) {
    public static final OverworldTier EASY = new OverworldTier(2, 4, 160, 200);
    public static final OverworldTier MEDIUM = new OverworldTier(4, 6, 190, 240);
    public static final OverworldTier HARD = new OverworldTier(6, 10, 240, 300);


    public int getPinCount(Random random) {
        return random.nextInt(minPinCount, maxPinCount + 1);
    }

    public float getArrowRotationSpeed(Random random) {
        return (random.nextBoolean() ? -1 : 1) * random.nextFloat(minArrowRotationSpeed, maxArrowRotationSpeed);
    }
}
