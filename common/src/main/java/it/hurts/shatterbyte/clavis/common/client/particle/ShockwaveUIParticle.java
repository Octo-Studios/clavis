package it.hurts.shatterbyte.clavis.common.client.particle;

import it.hurts.octostudios.octolib.client.particle.ExtendedUIParticle;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.octostudios.octolib.util.VectorUtils;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.screen.LockpickingScreen;
import org.joml.Vector2f;

import java.util.Random;

public class ShockwaveUIParticle extends ExtendedUIParticle {
    public static final Texture2D TEXTURE = new Texture2D(Clavis.path("textures/particle/shockwave.png"), 10, 10);

    public static void summonShockwaveEffect(LockpickingScreen screen, double mouseX, double mouseY) {
        Vector2f direction = new Vector2f(0, 1);
        for (int i = 0; i < 12; i++) {
            ShockwaveUIParticle particle = new ShockwaveUIParticle((float) mouseX, (float) mouseY, Layer.SCREEN, 1f);
            particle.setDirection(direction);
            particle.setScreen(screen);
            particle.instantiate();

            direction = VectorUtils.rotate(direction, 30f);
        }
    }

    public ShockwaveUIParticle(float x, float y, Layer layer, float zOffset) {
        super(TEXTURE, 5f, 20, x, y, layer, zOffset);
        Random random = new Random();
        this.setColors(OctoColor.WHITE, OctoColor.WHITE, new OctoColor(1f,1f,1f,0f));
        this.setSpeed(random.nextFloat(4.5f, 5f));
        this.setFriction(0.075f);
        this.getTransform().setSize(new Vector2f(0.75f, 0.75f));
        this.getTransform().setRoll(random.nextFloat(0, 360));
        this.getTransform().updateOldValues();
        this.setRollVelocity(random.nextFloat(-10, 10));
        this.enableBlend(true);
    }

    @Override
    public void tick() {
        super.tick();
        this.getTransform().getSize().add(0.1f, 0.1f);
    }
}
