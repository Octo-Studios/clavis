package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

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

    float rot;

    public MirrorWidget() {
        super(0, 0, 192, 192, (LockpickingScreen) Minecraft.getInstance().screen);
        children().add(new MeteorWidget(60, 35, this));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Vector2d center = new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f);
        Vector2d mousePos = mirrorPosition(new Vector2d(mouseX, mouseY), center, rot);
        Vector2d actualMousePos = new Vector2d(mouseX, mouseY);
        actualMousePos.sub(center);

        if (actualMousePos.lengthSquared() > 86 * 86) {
            actualMousePos.normalize(86);
        }

        //int offset = (int) ((192 - 146) / 2f);
        guiGraphics.pose().pushPose();
        guiGraphics.blit(BACKGROUND, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        drawClouds(guiGraphics, partialTick, BACK_CLOUDS, 1.5f, actualMousePos.x, actualMousePos.y);
        drawClouds(guiGraphics, partialTick, MIDDLE_CLOUDS, 2f, actualMousePos.x, actualMousePos.y);
        drawClouds(guiGraphics, partialTick, TOP_CLOUDS, 3f, actualMousePos.x, actualMousePos.y);
        guiGraphics.pose().popPose();

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        //guiGraphics.fill(mousePos.x, mousePos.y, mousePos.x+1, mousePos.y+1, 0xff00ff00);
        guiGraphics.blit(CROSSHAIR, (int) (mousePos.x-8), (int) (mousePos.y-8), 17, 17, 0, 0, 17, 17, 17, 17);

        guiGraphics.blit(FRAME, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotation(rot));
        guiGraphics.pose().translate(-this.width / 2f - this.getX(), -this.height / 2f - this.getY(), 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        guiGraphics.blit(MIRROR, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        RenderSystem.disableBlend();
        guiGraphics.blit(ROTATING_PART, this.getX()-9, (int) (this.getY()+this.height/2f-21), 210, 41, 0, 0, 210, 41, 210, 41);
        guiGraphics.pose().popPose();
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
        handler.play(SimpleSoundInstance.forUI(SoundEvents.ALLAY_HURT, 0.5f, 0.5f));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2d pos = mirrorPosition(new Vector2d(mouseX, mouseY), new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f), rot);
        return super.mouseClicked(pos.x, pos.y, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.getMinigame().hurt();
        this.getMinigame().processOnClickRules(false);
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

    @Override
    public void tick() {
        oldBackgroundRotation = backgroundRotation;
        backgroundRotation += 0.003f;

        //rot += 0.005f;

        for (MeteorWidget child : this.children()) {
            child.tick();
        }
    }
}
