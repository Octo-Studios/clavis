package it.hurts.octostudios.clavis.common.data;

import com.mojang.serialization.Codec;
import lombok.EqualsAndHashCode;
import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.stream.IntStream;

@EqualsAndHashCode(callSuper = false)
public class Box {
    public static final Codec<Box> CODEC = Codec.INT_STREAM
            .comapFlatMap(
                    intStream -> Util.fixedSize(intStream, 6).map(is -> new Box(is[0], is[1], is[2], is[3], is[4], is[5])),
                    box -> IntStream.of(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)
            )
            .stable();

    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;

    public Box(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public Box(Vec3i block) {
        this(block, block);
    }

    public Box(Vec3i start, Vec3i end) {
        this(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
    }

    public boolean isInside(Vec3i block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        return  minX <= x && x <= maxX &&
                minY <= y && y <= maxY &&
                minZ <= z && z <= maxZ;
    }

    public boolean intersectsChunk(ChunkPos pos) {
        int minX = pos.getMinBlockX();
        int maxX = pos.getMaxBlockX();
        int minZ = pos.getMinBlockZ();
        int maxZ = pos.getMaxBlockZ();

        return this.minX <= maxX && this.maxX >= minX &&
                this.minZ <= maxZ && this.maxZ >= minZ;
    }

    public AABB getAABB() {
        return new AABB(minX, minY, minZ, maxX+1, maxY+1, maxZ+1);
    }

    public Vec3 getCenter() {
        return new Vec3(Mth.lerp(0.5d, minX, maxX+1), Mth.lerp(0.5d, minY, maxY+1), Mth.lerp(0.5d, minZ, maxZ+1));
    }
}
