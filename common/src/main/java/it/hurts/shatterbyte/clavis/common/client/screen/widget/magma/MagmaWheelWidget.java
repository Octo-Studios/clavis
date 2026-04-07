package it.hurts.shatterbyte.clavis.common.client.screen.widget.magma;

import com.mojang.math.Axis;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.screen.LockpickingScreen;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.RotatingParent;
import it.hurts.shatterbyte.clavis.common.minigame.Minigame;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

import java.util.Random;

public class MagmaWheelWidget extends AbstractMinigameWidget<RotatingParent<FireballWidget, MagmaWheelWidget>> {
    public static final ResourceLocation BACKGROUND = Clavis.path("textures/minigame/magma_wheel/base.png");
    public static final ResourceLocation CURSOR = Clavis.path("textures/minigame/magma_wheel/cursor.png");
    public static final ResourceLocation RAIL = Clavis.path("textures/minigame/magma_wheel/rail.png");
    public static final ResourceLocation FIREBALL = Clavis.path("textures/minigame/magma_wheel/ball.png");

    boolean isPlaying = true;

    @Setter
    double rot;
    int rotationDir = 1;

    double[] oldRingsRot = new double[]{0,0,0};
    double[] ringsRot = new double[]{0,0,0};

    Tween mainTween = Tween.create();

    public MagmaWheelWidget() {
        super(0, 0, 192, 192, (LockpickingScreen) Minecraft.getInstance().screen);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(BACKGROUND, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);

        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        double mouseXScaled = Minecraft.getInstance().mouseHandler.xpos() / guiScale;
        double mouseYScaled = Minecraft.getInstance().mouseHandler.ypos() / guiScale;

        Vector2d center = new Vector2d(this.getX() + this.width / 2f, this.getY() + this.height / 2f);
        Vector2d mousePos = this.getClampedMousePos(mouseXScaled, mouseYScaled, center);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(center.x, center.y, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotation((float) rot));
        guiGraphics.blit(RAIL, -16, -106, 32, 212, 0, 0, 32, 212, 32, 212);
        guiGraphics.pose().popPose();

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(mousePos.x, mousePos.y, 0);

        guiGraphics.pose().mulPose(Axis.ZP.rotation((float) rot));

        guiGraphics.blit(CURSOR, -9, -9, 18, 18, 0, 0, 18, 18, 18, 18);
        guiGraphics.pose().popPose();
    }

    public static RotatingParent<FireballWidget, MagmaWheelWidget> createRing(
            int centerX, int centerY,
            float rotation,
            MagmaWheelWidget parent
    ) {
        RotatingParent<FireballWidget, MagmaWheelWidget> ring =
                new RotatingParent<>(centerX, centerY, rotation);

        ring.setParent(parent);
        return ring;
    }

    private Vector2d getClampedMousePos(double mouseX, double mouseY, Vector2d center) {
        Vector2d mouse = new Vector2d(mouseX, mouseY);
        Vector2d relative = mouse.sub(center, new Vector2d());

// Step 2: rotate INTO rail space (inverse rotation)
        double cos = Math.cos(-rot);
        double sin = Math.sin(-rot);

        Vector2d local = new Vector2d(
                relative.x * cos - relative.y * sin,
                relative.x * sin + relative.y * cos
        );

// Step 3: constrain to rail (vertical line)
        double railHalfLength = 96; // matches your rail texture height / 2
        local.x = 0; // lock to center of rail
        local.y = Mth.clamp(local.y, -railHalfLength, railHalfLength);

// Step 4: rotate BACK to world space
        cos = Math.cos(rot);
        sin = Math.sin(rot);

        Vector2d constrained = new Vector2d(
                local.x * cos - local.y * sin,
                local.x * sin + local.y * cos
        );

// Step 5: convert back to screen position
        return constrained.add(center);
    }

    @Override
    public void playHurtAnimation() {

    }

    @Override
    public void playWinAnimation() {

    }

    @Override
    public void playLoseAnimation() {

    }

    @Override
    public void processDifficulty(Minigame<? extends AbstractMinigameWidget<?>> game) {
        this.random = new Random(game.getSeed());

        if (this.random.nextBoolean()) {
            this.rot = 1.5707f;
        }

        if (this.random.nextBoolean()) {
            this.rotationDir = -1;
        }

        float difficulty = game.getDifficulty();

        float scaled = (0.25f + difficulty * 0.75f);

        Vector2d center = new Vector2d(this.width / 2f, this.height / 2f);

// radii for 3 rings
        float[] radii = new float[]{24f, 52f, 80f};

        for (int r = 0; r < 3; r++) {
            float radius = radii[r];

            RotatingParent<FireballWidget, MagmaWheelWidget> ring = MagmaWheelWidget.createRing((int) center.x, (int) center.y, 0, this);

            int fireballCount = Mth.ceil(random.nextFloat(2, 4) * scaled);

            for (int i = 0; i < fireballCount; i++) {
                float angle = (360f / fireballCount) * i + random.nextFloat(-10, 10);

                FireballWidget fireball = new FireballWidget(0, 0);
                fireball.setParent(ring);

                // store polar coords
                fireball.angle = angle;
                fireball.radius = radius;

                ring.children.add(fireball);
            }

            this.children.add(ring);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isPlaying) {
            return false;
        }

        Vector2d realMousePos = new Vector2d(mouseX, mouseY);
        Vector2d center = new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f);
        if (realMousePos.distanceSquared(center) > 106*106) {
            return false;
        }

        Vector2d pos = this.getClampedMousePos(realMousePos.x, realMousePos.y, new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f));
        return super.mouseClicked(pos.x, pos.y, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    public void rotate(float alpha) {
        mainTween.kill();
        mainTween = Tween.create();
        mainTween.parallel().tweenMethod(this::setRot, this.rot, this.rot + Math.toRadians(alpha) * rotationDir, 5f)
                .setEaseType(EaseType.EASE_IN_OUT)
                .setTransitionType(TransitionType.QUART);
        mainTween.start();
    }

    @Override
    public void tick() {
        for (int i = 0; i < children.size(); i++) {
            RotatingParent<?, ?> ring = children.get(i);

            float speed = switch (i) {
                case 0 -> 0.5f;
                case 1 -> -0.8f;
                case 2 -> 1.2f;
                default -> 0f;
            };

            ring.setRot(ring.getRot() + speed);
        }
    }
}
