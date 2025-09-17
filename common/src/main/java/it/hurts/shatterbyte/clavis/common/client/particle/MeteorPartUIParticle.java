package it.hurts.shatterbyte.clavis.common.client.particle;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.octostudios.octolib.client.particle.ExtendedUIParticle;
import net.minecraft.util.Mth;
import org.joml.Vector2f;

import java.util.List;
import java.util.Random;

public class MeteorPartUIParticle extends ExtendedUIParticle {
    public static final Texture2D PART_1 = new Texture2D(Clavis.path("textures/particle/meteor_projectile_1.png"), 5, 5);
    public static final Texture2D PART_2 = new Texture2D(Clavis.path("textures/particle/meteor_projectile_2.png"), 4, 4);
    public static final Texture2D PART_3 = new Texture2D(Clavis.path("textures/particle/meteor_projectile_3.png"), 3, 3);
    public static final Texture2D PART_4 = new Texture2D(Clavis.path("textures/particle/meteor_projectile_4.png"), 2, 2);

    public static final List<Texture2D> PARTS = List.of(PART_1, PART_2, PART_3, PART_4);

    public static Texture2D getRandomPart(Random random) {
        return PARTS.get(random.nextInt(PARTS.size()-1));
    }

    public MeteorPartUIParticle(Texture2D texture, int lifetime, float meteorCenterX, float meteorCenterY, float spread, Layer layer, float zOffset) {
        super(texture, 2f, lifetime, meteorCenterX, meteorCenterY, layer, zOffset);
        Random random = new Random();
        if (spread != 0) {
            float halfSpread = spread / 2f;
            this.getTransform().getPosition().add(
                    random.nextFloat(-halfSpread, halfSpread),
                    random.nextFloat(-halfSpread, halfSpread)
            );
        }
        this.setDirection(new Vector2f(this.getTransform().getPosition()).sub(meteorCenterX, meteorCenterY));
        this.setSpeed(random.nextFloat(0.5f, 2));
        this.setFriction(0.025f);
        this.getTransform().setRoll(random.nextFloat(0, 360));
        this.getTransform().updateOldValues();
        this.setRollVelocity(random.nextFloat(-30, 30));
        this.enableBlend(false);
    }

    @Override
    public void tick() {
        super.tick();

        float timeRatio = 1 - this.getTimeRatio(0);
        timeRatio *= 1.5f;
        timeRatio = Mth.clamp(timeRatio, 0, 1);
        this.getTransform().getSize().mul(timeRatio);
    }
}
