package it.hurts.shatterbyte.clavis.common.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.particle.MouseTeleportUIParticle;
import it.hurts.shatterbyte.clavis.common.client.particle.ShockwaveUIParticle;
import it.hurts.shatterbyte.clavis.common.client.screen.LockpickingScreen;
import it.hurts.shatterbyte.clavis.common.minigame.Minigame;
import it.hurts.shatterbyte.clavis.common.mixin.MouseHandlerAccessor;
import it.hurts.shatterbyte.clavis.common.registry.SoundEventRegistry;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

import java.util.Random;

public class MirrorWidget extends AbstractMinigameWidget<MeteorWidget> {
    public static final ResourceLocation BACKGROUND = Clavis.path("textures/minigame/mirror/black_circle.png");
    public static final ResourceLocation FRAME = Clavis.path("textures/minigame/mirror/frame.png");
    public static final ResourceLocation ROTATING_PART = Clavis.path("textures/minigame/mirror/rotating_part.png");
    public static final ResourceLocation MIRROR = Clavis.path("textures/minigame/mirror/mirror.png");
    public static final ResourceLocation BACK_CLOUDS = Clavis.path("textures/minigame/mirror/background_back_clouds.png");
    public static final ResourceLocation MIDDLE_CLOUDS = Clavis.path("textures/minigame/mirror/background_middle_clouds.png");
    public static final ResourceLocation TOP_CLOUDS = Clavis.path("textures/minigame/mirror/background_top_clouds.png");
    public static final ResourceLocation CROSSHAIR = Clavis.path("textures/minigame/mirror/crosshair.png");
    public static final ResourceLocation CROSSHAIR_SQUARE = Clavis.path("textures/minigame/mirror/crosshair_square.png");

    float oldBackgroundRotation;
    float backgroundRotation;

    @Getter @Setter
    OctoColor gameColor = OctoColor.WHITE;

    Tween crosshairTween = Tween.create();

    @Setter
    float crosshairScale = 1f;

    @Getter
    boolean playing = true;

    Tween mainTween = Tween.create();

    //float oldRot;
    @Setter
    double rot;

    public MirrorWidget() {
        super(0, 0, 192, 192, (LockpickingScreen) Minecraft.getInstance().screen);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float partial = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        Vector2d center = new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f);
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        double i = Minecraft.getInstance().mouseHandler.xpos()/guiScale;
        double j = Minecraft.getInstance().mouseHandler.ypos()/guiScale;
        Vector2d mousePos = mirrorPosition(new Vector2d(i, j), center, rot);
        Vector2d actualMousePos = new Vector2d(i, j);
        actualMousePos.sub(center);

        if (actualMousePos.lengthSquared() > 86 * 86) {
            actualMousePos.normalize(86);
        }

        RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());
        RenderSystem.enableBlend();

        //int offset = (int) ((192 - 146) / 2f);
        //swapMousePositions();
        guiGraphics.pose().pushPose();
        guiGraphics.blit(BACKGROUND, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        drawClouds(guiGraphics, partialTick, BACK_CLOUDS, 1.5f, actualMousePos.x, actualMousePos.y);
        drawClouds(guiGraphics, partialTick, MIDDLE_CLOUDS, 2f, actualMousePos.x, actualMousePos.y);
        drawClouds(guiGraphics, partialTick, TOP_CLOUDS, 3f, actualMousePos.x, actualMousePos.y);
        guiGraphics.pose().popPose();

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        //guiGraphics.fill(mousePos.x, mousePos.y, mousePos.x+1, mousePos.y+1, 0xff00ff00);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(mousePos.x, mousePos.y, 0);
        guiGraphics.pose().scale(crosshairScale, crosshairScale, 1);
        guiGraphics.blit(CROSSHAIR, -8, -8, 17, 17, 0, 0, 17, 17, 17, 17);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,2);
        guiGraphics.blit(FRAME, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotation((float) rot));
        guiGraphics.pose().translate(-this.width / 2f - this.getX(), -this.height / 2f - this.getY(), 1);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        guiGraphics.blit(MIRROR, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        RenderSystem.disableBlend();

        guiGraphics.pose().translate(0,0,1);
        guiGraphics.blit(ROTATING_PART, this.getX()-9, (int) (this.getY()+this.height/2f-21), 210, 41, 0, 0, 210, 41, 210, 41);
        guiGraphics.pose().popPose();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    private void drawClouds(GuiGraphics guiGraphics, float partialTick, ResourceLocation topClouds, float speedFactor, double x, double y) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(-x/96f*(speedFactor-1.25f), -y/96f*(speedFactor-1.25f), 0);
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotation(Mth.lerp(partialTick, oldBackgroundRotation, backgroundRotation)*speedFactor));
        guiGraphics.pose().translate(-this.width / 2f - this.getX(), -this.height / 2f - this.getY(), 0);

        guiGraphics.blit(topClouds, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().popPose();
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.ALLAY_HURT, 0.5f, 0.3f));
    }

    public void regenerateAll() {
        if (this.children().stream().noneMatch(MeteorWidget::isCracked)) {
            return;
        }

        this.children().forEach(MeteorWidget::regenerate);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEventRegistry.METEOR_REGENERATE.get(), 0.66f, 1f));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isPlaying()) {
            return false;
        }

        Vector2d realMousePos = new Vector2d(mouseX, mouseY);
        Vector2d center = new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f);
        if (realMousePos.distanceSquared(center) > 108*108) {
            return false;
        }

        Vector2d pos = mirrorPosition(realMousePos, new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f), rot);
        return super.mouseClicked(pos.x, pos.y, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.getMinigame().hurt();
        this.getMinigame().processOnClickRules(false);
    }

    @Override
    public void playHurtAnimation() {
        this.mainTween.kill();
        this.mainTween = Tween.create();
        this.mainTween.tweenMethod(this::setGameColor, new OctoColor(0.8f, 0.2f, 1f, 1f), OctoColor.WHITE, 0.5f);
        this.mainTween.start();
    }

    @Override
    public void playWinAnimation() {
        this.playing = false;
        this.children().forEach(meteor -> {
            meteor.playCrackAnimation();
            meteor.playCrackAnimation();
        });
        this.children().clear();

        rotationTween.kill();
        rotationTween = Tween.create();
        rotationTween.tweenMethod(this::setRot, this.rot, this.rot + Math.toRadians(180), 1f)
                .setEaseType(EaseType.EASE_OUT)
                .setTransitionType(TransitionType.CUBIC);
        rotationTween.tweenRunnable(() -> this.screen.finish(false));
        rotationTween.start();
    }

    @Override
    public void playLoseAnimation() {
        this.playing = false;
        this.screen.finish(true);
    }

    @Override
    public void processDifficulty(Minigame<? extends AbstractMinigameWidget<?>> game) {
        this.random = new Random(game.getSeed());

        if (this.random.nextBoolean()) {
            this.rot = 1.5707f;
        }

        float difficulty = game.getDifficulty();

        float scaled = (0.25f + difficulty * 0.75f);

        int meteorCount = Mth.ceil(random.nextFloat(4, 8) * scaled);
        for (int i = 0; i < meteorCount; i++) {
            MeteorWidget meteor = new MeteorWidget(0, 0, this.getRandomMeteorSize(), this);
            Vector2d randomPos = this.getRandomPos().sub(meteor.getWidth()/2f, meteor.getHeight()/2f);
            meteor.setPosition((int) randomPos.x, (int) randomPos.y);
            meteor.precisePosition = randomPos;
            meteor.oldPos = new Vector2d(randomPos);
            this.children.add(meteor);
        }
    }

    @Override
    public void killTweens() {
        mainTween.kill();
        rotationTween.kill();
    }

    private static Vector2d mirrorPosition(Vector2d point, Vector2d center, double angle) {
        double px = point.x - center.x;
        double py = point.y - center.y;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double rx = cos * px + sin * py;
        double ry = -sin * px + cos * py;

        ry = -ry;

        double fx = cos * rx - sin * ry;
        double fy = sin * rx + cos * ry;

        Vector2d pos = new Vector2d(fx, fy);
        if (pos.lengthSquared() > 86 * 86) {
            pos.normalize(86);
        }

        pos.add(center);

        return pos;
    }

    private Vector2d getMirroredMousePos(double mouseX, double mouseY) {
        return mirrorPosition(new Vector2d(mouseX, mouseY), new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f), rot);
    }

    public void swapMousePositions() {
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        double mouseX = Minecraft.getInstance().mouseHandler.xpos()/guiScale;
        double mouseY = Minecraft.getInstance().mouseHandler.ypos()/guiScale;

        Vector2d original = new Vector2d(mouseX, mouseY);
        Vector2d swapped = getMirroredMousePos(mouseX, mouseY);

        MouseHandlerAccessor accessor = (MouseHandlerAccessor) Minecraft.getInstance().mouseHandler;
        double sX = swapped.x * guiScale;
        double sY = swapped.y * guiScale;

        long windowHandle = Minecraft.getInstance().getWindow().getWindow();

        Clavis.CURSOR_MOVER.moveMouse(windowHandle, sX, sY);

        accessor.invokeOnMove(windowHandle, sX, sY);

        MouseTeleportUIParticle.drawLine(this.screen, swapped, original);
        crosshairTween.kill();
        crosshairTween = Tween.create();
        crosshairTween.tweenMethod(this::setCrosshairScale, 1.5f, 1f, 1f)
                .setTransitionType(TransitionType.QUART)
                .setEaseType(EaseType.EASE_OUT);
        crosshairTween.start();
    }

    public void doShockwave() {
        Vector2d mousePos;

        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        double mouseX = Minecraft.getInstance().mouseHandler.xpos()/guiScale;
        double mouseY = Minecraft.getInstance().mouseHandler.ypos()/guiScale;

        mousePos = getMirroredMousePos(mouseX, mouseY);

        for (MeteorWidget meteor : this.children()) {
            Vector2d centerPos = meteor.getCenterPos(true);
            double dist = mousePos.distance(centerPos);
            if (dist > 96) {
                continue;
            }

            Vector2d direction = new Vector2d(mousePos).sub(centerPos).normalize((96-dist)/32f);
            if (dist <= Math.max(meteor.getWidth(), meteor.getHeight())/2f) {
                direction.mul(0.25f);
            }
            meteor.velocity.sub(direction);
        }

        ShockwaveUIParticle.summonShockwaveEffect(this.screen, mousePos.x, mousePos.y);
    }

    @Override
    public void tick() {
        oldBackgroundRotation = backgroundRotation;
        backgroundRotation += 0.003f;

        //rot += 0.01f;

        for (MeteorWidget child : this.children()) {
            child.tick();
        }
    }

    Tween rotationTween = Tween.create();

    public void rotate(float alpha) {
        rotationTween.kill();
        rotationTween = Tween.create();
        rotationTween.parallel().tweenMethod(this::setRot, this.rot, this.rot + Math.toRadians(alpha), 5f)
                .setEaseType(EaseType.EASE_IN_OUT)
                .setTransitionType(TransitionType.QUART);
        rotationTween.start();
    }

    public Vector2d getRandomPos() {
        Vector2d randomPos = new Vector2d(this.getRandom().nextDouble(-90, 90), this.getRandom().nextDouble(-90, 90));
        if (randomPos.length() > 80 ) {
            randomPos.normalize(80);
        }
        randomPos.add(96, 96);
        return randomPos;
    }

    public int getRandomMeteorSize() {
        float difficulty = this.minigame.getDifficulty();
        int smallestSize = difficulty > 0.66f ? 0 : difficulty > 0.33f ? 1 : 2;

        if (smallestSize == 2) {
            return 2;
        }

        return random.nextInt(smallestSize, 3);
    }
}
