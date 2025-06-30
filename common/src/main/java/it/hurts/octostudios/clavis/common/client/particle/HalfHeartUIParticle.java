package it.hurts.octostudios.clavis.common.client.particle;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.octolib.client.particle.ExtendedUIParticle;
import it.hurts.octostudios.octolib.util.VectorUtils;
import org.joml.Vector2f;

import java.util.Random;

public class HalfHeartUIParticle extends ExtendedUIParticle {
    public static final Texture2D LEFT = new Texture2D(Clavis.path("textures/particle/heart_left.png"), 9, 16);
    public static final Texture2D RIGHT = new Texture2D(Clavis.path("textures/particle/heart_right.png"), 9, 16);
    boolean right;

    public HalfHeartUIParticle(boolean isRight, float heartX, float heartY, Layer layer, float zOffset) {
        super(isRight ? RIGHT : LEFT, 4f, 30, isRight ? heartX + 3.5f : heartX - 3.5f, heartY, layer, zOffset);
        this.right = isRight;
        Random random = new Random();
        this.speed = random.nextFloat(4, 6);

        this.direction = VectorUtils.rotate(new Vector2f(0, -1), isRight ? 10 : -10);
        this.gravity = 0.98f;
        this.rollVelocity = isRight ? random.nextFloat(1,3) : random.nextFloat(-3, -1);
        this.enableBlend(false);
    }
}
