package it.hurts.shatterbyte.clavis.common.client.screen.widget.magma;

import it.hurts.octostudios.octolib.client.particle.UIParticle;
import it.hurts.octostudios.octolib.client.screen.widget.Child;
import it.hurts.shatterbyte.clavis.common.client.particle.MeteorPartUIParticle;
import it.hurts.shatterbyte.clavis.common.client.particle.ShockwaveUIParticle;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.RotatingParent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public class FireballWidget extends AbstractWidget implements Child<RotatingParent> {
    @Override
    public @Nullable RotatingParent getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable RotatingParent rotatingParent) {
        this.parent = rotatingParent;
    }

    RotatingParent parent;

    public float angle;
    public float radius;
    boolean blownUp;

    public FireballWidget(int x, int y) {
        super(x, y, 10, 10, Component.empty());
    }

    public boolean isBlownUp() {
        return blownUp;
    }

    public void blowUp() {
        if (this.blownUp) {
            return;
        }

        this.blownUp = true;

        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        Vector2d center = this.getCenterPos(partialTick);

        if (this.parent.getParent() instanceof MagmaWheelWidget wheel) {
            ShockwaveUIParticle.summonShockwaveEffect(wheel.getScreen(), center.x, center.y);

            for (int i = 0; i < 5; i++) {
                MeteorPartUIParticle particle = new MeteorPartUIParticle(
                        MeteorPartUIParticle.getRandomPart(wheel.random),
                        20,
                        (float) center.x,
                        (float) center.y,
                        this.width,
                        UIParticle.Layer.SCREEN,
                        1
                );
                particle.setScreen(wheel.getScreen());
                particle.instantiate();
            }
        }

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.GENERIC_EXPLODE.value(), 0.75f, 1.35f));
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.FIRE_EXTINGUISH, 0.9f, 1.4f));
    }

    public Vector2d getCenterPos(float partialTick) {
        if (this.parent == null) {
            return new Vector2d(this.getX() + this.width / 2f, this.getY() + this.height / 2f);
        }

        double radians = Math.toRadians(this.angle + this.parent.getVisualRot(partialTick));

        return new Vector2d(
                this.parent.getX() + Math.sin(radians) * this.radius,
                this.parent.getY() - Math.cos(radians) * this.radius
        );
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.blownUp) {
            return;
        }

        Vector2d center = this.getCenterPos(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(center.x, center.y, 0);

        guiGraphics.blit(MagmaWheelWidget.FIREBALL, -5, -5, 10, 10, 0, 0, 10, 10, 10, 10);

        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
