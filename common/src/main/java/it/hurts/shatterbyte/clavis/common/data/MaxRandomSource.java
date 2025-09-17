package it.hurts.shatterbyte.clavis.common.data;

import net.minecraft.world.level.levelgen.LegacyRandomSource;

public class MaxRandomSource extends LegacyRandomSource {
    public MaxRandomSource() {
        super(0);
    }

    @Override
    public int next(int size) {
        return (1 << size) - 1;
    }

    @Override
    public int nextInt() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int nextInt(int bound) {
        return bound > 0 ? bound - 1 : 0;
    }

    @Override
    public float nextFloat() {
        return 1f;
    }

    @Override
    public double nextDouble() {
        return 1d;
    }

    @Override
    public int nextIntBetweenInclusive(int min, int max) {
        return max;
    }

    @Override
    public boolean nextBoolean() {
        return true;
    }

    @Override
    public long nextLong() {
        return Long.MAX_VALUE;
    }
}
