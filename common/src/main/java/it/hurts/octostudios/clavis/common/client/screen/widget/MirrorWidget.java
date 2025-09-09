package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

public class MirrorWidget extends AbstractMinigameWidget<RotatingParent<MeteorWidget, MirrorWidget>> {
    public static final ResourceLocation BACKGROUND = Clavis.path("textures/minigame/mirror/black_circle.png");
    public static final ResourceLocation FRAME = Clavis.path("textures/minigame/mirror/frame.png");
    public static final ResourceLocation ROTATING_PART = Clavis.path("textures/minigame/mirror/rotating_part.png");
    public static final ResourceLocation MIRROR = Clavis.path("textures/minigame/mirror/mirror.png");
    public static final ResourceLocation CLOUDS = Clavis.path("textures/minigame/mirror/background_clouds.png");
    public static final ResourceLocation CROSSHAIR = Clavis.path("textures/minigame/mirror/crosshair.png");
    public static final ResourceLocation CROSSHAIR_SQUARE = Clavis.path("textures/minigame/mirror/crosshair_square.png");

    float oldBackgroundRotation;
    float backgroundRotation;

    float rot;

    public MirrorWidget() {
        super(0, 0, 192, 192, (LockpickingScreen) Minecraft.getInstance().screen);
        children().add(MeteorWidget.create(60, 35, 0f, this));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //int offset = (int) ((192 - 146) / 2f);
        guiGraphics.pose().pushPose();
        guiGraphics.blit(BACKGROUND, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotation(Mth.lerp(partialTick, oldBackgroundRotation, backgroundRotation)));
        guiGraphics.pose().translate(-this.width / 2f - this.getX(), -this.height / 2f - this.getY(), 0);
        guiGraphics.blit(CLOUDS, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().popPose();

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        Vector2d mousePos = mirrorPosition(new Vector2d(mouseX, mouseY), new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f), rot);
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2d pos = mirrorPosition(new Vector2d(mouseX, mouseY), new Vector2d(this.getX()+this.width/2f, this.getY()+this.height/2f), rot);
        return super.mouseClicked(pos.x, pos.y, button);
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

        fx += center.x;
        fy += center.y;

        return new Vector2d(fx, fy);
    }

    @Override
    public void tick() {
        oldBackgroundRotation = backgroundRotation;
        backgroundRotation += 0.01f;

        rot += 0.005f;
    }
}
