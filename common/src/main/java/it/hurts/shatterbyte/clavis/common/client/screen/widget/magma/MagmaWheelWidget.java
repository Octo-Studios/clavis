package it.hurts.shatterbyte.clavis.common.client.screen.widget.magma;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.screen.LockpickingScreen;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.RotatingParent;
import it.hurts.shatterbyte.clavis.common.minigame.Minigame;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
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
    final float[] ringSpeeds = new float[3];
    @Getter
    @Setter
    OctoColor gameColor = OctoColor.WHITE;

    Tween mainTween = Tween.create();

    public MagmaWheelWidget() {
        super(0, 0, 192, 192, (LockpickingScreen) Minecraft.getInstance().screen);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());
        RenderSystem.enableBlend();
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

        for (RotatingParent<FireballWidget, MagmaWheelWidget> ring : this.children) {
            ring.setMatrix(new Matrix4f(guiGraphics.pose().last().pose()));
            for (FireballWidget fireball : ring.children) {
                fireball.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(mousePos.x, mousePos.y, 0);

        guiGraphics.pose().mulPose(Axis.ZP.rotation((float) rot));

        guiGraphics.blit(CURSOR, -9, -9, 18, 18, 0, 0, 18, 18, 18, 18);
        guiGraphics.pose().popPose();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
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

    private int getRandomFireballCount(float scaled) {
        int count = Mth.ceil(random.nextFloat(2, 4) * scaled);

        if ((count & 1) == 0) {
            count += random.nextBoolean() ? 1 : -1;
        }

        return Mth.clamp(count, 1, 5);
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
        this.mainTween.kill();
        this.mainTween = Tween.create();
        this.mainTween.tweenMethod(this::setGameColor, new OctoColor(1.25f, 0.45f, 0.2f, 1f), OctoColor.WHITE, 0.5f);
        this.mainTween.start();
    }

    @Override
    public void playWinAnimation() {
        if (!this.isPlaying) {
            return;
        }

        this.isPlaying = false;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BLAZE_DEATH, 0.7f, 1.25f));
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_CLUSTER_BREAK, 0.8f, 0.85f));
        this.mainTween.kill();
        this.mainTween = Tween.create();
        this.mainTween.tweenMethod(this::setRot, this.rot, this.rot + Math.toRadians(180), 1f)
                .setEaseType(EaseType.EASE_OUT)
                .setTransitionType(TransitionType.CUBIC);
        this.mainTween.tweenRunnable(() -> this.screen.finish(false));
        this.mainTween.start();
    }

    @Override
    public void playLoseAnimation() {
        if (!this.isPlaying) {
            return;
        }

        this.isPlaying = false;
        this.mainTween.kill();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BLAZE_HURT, 0.75f, 0.8f));
        this.screen.finish(true);
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

        float[] radii = new float[]{24f, 52f, 80f};
        float speedScale = 0.9f + difficulty * 0.2f;

        for (int r = 0; r < 3; r++) {
            float radius = radii[r];
            float baseSpeed = switch (r) {
                case 0 -> 0.82f;
                case 1 -> -0.96f;
                case 2 -> 1.08f;
                default -> 0f;
            };
            this.ringSpeeds[r] = baseSpeed * speedScale * random.nextFloat(0.94f, 1.06f);

            RotatingParent<FireballWidget, MagmaWheelWidget> ring = MagmaWheelWidget.createRing((int) center.x, (int) center.y, 0, this);

            int fireballCount = this.getRandomFireballCount(scaled);
            float angleStep = 360f / fireballCount;
            float ringOffset = random.nextFloat(0f, angleStep);

            for (int i = 0; i < fireballCount; i++) {
                float angle = angleStep * i + ringOffset + random.nextFloat(-12f, 12f);

                FireballWidget fireball = new FireballWidget(0, 0);
                fireball.setParent(ring);

                fireball.angle = angle;
                fireball.radius = radius;

                ring.children.add(fireball);
            }

            this.children.add(ring);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isPlaying || button != 0) {
            return false;
        }

        Vector2d realMousePos = new Vector2d(mouseX, mouseY);
        Vector2d center = new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f);
        if (realMousePos.distanceSquared(center) > 106*106) {
            return false;
        }

        Vector2d pos = this.getClampedMousePos(realMousePos.x, realMousePos.y, center);
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);

        for (RotatingParent<FireballWidget, MagmaWheelWidget> ring : this.children) {
            for (FireballWidget fireball : ring.children) {
                if (fireball.isBlownUp()) {
                    continue;
                }

                if (fireball.getCenterPos(partialTick).distanceSquared(pos) > 36) {
                    continue;
                }

                fireball.blowUp();
                this.getMinigame().processOnClickRules(true);

                if (this.children.stream()
                        .flatMap(rotatingParent -> rotatingParent.children.stream())
                        .allMatch(FireballWidget::isBlownUp)) {
                    this.getMinigame().finish();
                }

                return true;
            }
        }

        this.getMinigame().hurt();
        this.getMinigame().processOnClickRules(false);
        return false;
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
            ring.capturePreviousRot();
            ring.setRot(ring.getRot() + this.ringSpeeds[i]);
        }
    }

    @Override
    public void killTweens() {
        this.mainTween.kill();
    }
}
