package it.hurts.shatterbyte.clavis.common.client.particle;

import it.hurts.octostudios.octolib.client.particle.ExtendedUIParticle;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.shatterbyte.clavis.common.client.screen.LockpickingScreen;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.Random;

public class MouseTeleportUIParticle extends ExtendedUIParticle {
    public static void drawLine(LockpickingScreen screen, Vector2d pos1, Vector2d pos2) {
        Vector2f direction = new Vector2f((float) (pos2.x - pos1.x), (float) (pos2.y - pos1.y));
        float stepSize = 8f;
        direction.normalize(stepSize);

        Vector2d currentPos = new Vector2d(pos1);
        double totalDistance = pos1.distance(pos2);
        double traveled = 0;

        while (traveled < totalDistance) {
            MouseTeleportUIParticle particle = new MouseTeleportUIParticle(
                    (float) currentPos.x,
                    (float) currentPos.y,
                    direction,
                    Layer.SCREEN,
                    1f
            );
            particle.setScreen(screen);
            particle.instantiate();

            currentPos.add(direction);
            traveled += stepSize;
        }
    }

    public MouseTeleportUIParticle(float x, float y, Vector2f direction, Layer layer, float zOffset) {
        super(ShockwaveUIParticle.TEXTURE, 3f, 10, x, y, layer, zOffset);
        Random random = new Random();
        this.setDirection(direction);
        this.setFriction(0.1f);
        this.setColors(OctoColor.WHITE, OctoColor.WHITE, new OctoColor(1f,1f,1f,0f));
        this.getTransform().setRoll(random.nextFloat(0, 360));
        this.getTransform().setSize(new Vector2f(0.75f, 0.75f));
        this.getTransform().updateOldValues();
        this.setRollVelocity(15f);
        this.enableBlend(true);
    }
}
