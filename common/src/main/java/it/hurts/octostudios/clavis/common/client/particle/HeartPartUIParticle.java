package it.hurts.octostudios.clavis.common.client.particle;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.octolib.client.particle.ExtendedUIParticle;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.octostudios.octolib.util.VectorUtils;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class HeartPartUIParticle extends ExtendedUIParticle {
    public static final Texture2D PART = new Texture2D(Clavis.path("textures/particle/heart_part.png"), 6, 6);

    public HeartPartUIParticle(float heartX, float heartY, Layer layer, float zOffset) {
        super(PART, 7f, 20, heartX, heartY, layer, zOffset);
        Random random = new Random();
        this.speed = random.nextFloat(2, 7);
        this.direction = VectorUtils.rotate(new Vector2f(0, -1), random.nextFloat(0, 360));
        this.gravity = 0.98f;
        this.transform.setRoll(random.nextFloat(0, 360));
        this.transform.updateOldValues();
        this.rollVelocity = random.nextFloat(-30,30);
        this.enableBlend(false);
        //this.setBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //this.setColors(OctoColor.WHITE, new OctoColor(1f, 1f, 1f, 0f));
    }
}
