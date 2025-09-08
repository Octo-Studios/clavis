package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MirrorWidget extends AbstractMinigameWidget<GuiEventListener> {
    public static final ResourceLocation BACKGROUND = Clavis.path("textures/minigame/mirror/black_circle.png");
    public static final ResourceLocation FRAME = Clavis.path("textures/minigame/mirror/frame.png");
    public static final ResourceLocation ROTATING_PART = Clavis.path("textures/minigame/mirror/rotating_part.png");
    public static final ResourceLocation MIRROR = Clavis.path("textures/minigame/mirror/mirror.png");
    public static final ResourceLocation CLOUDS = Clavis.path("textures/minigame/mirror/background_clouds.png");

    float oldBackgroundRotation;
    float backgroundRotation;

    float rot;

    public MirrorWidget() {
        super(0, 0, 192, 192, (LockpickingScreen) Minecraft.getInstance().screen);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //int offset = (int) ((192 - 146) / 2f);
        guiGraphics.pose().pushPose();
        guiGraphics.blit(BACKGROUND, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, oldBackgroundRotation, backgroundRotation)));
        guiGraphics.pose().translate(-this.width / 2f - this.getX(), -this.height / 2f - this.getY(), 0);
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(CLOUDS, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().popPose();

        guiGraphics.blit(FRAME, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);


        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rot));
        guiGraphics.pose().translate(-this.width / 2f - this.getX(), -this.height / 2f - this.getY(), 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        guiGraphics.blit(MIRROR, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        RenderSystem.disableBlend();
        guiGraphics.blit(ROTATING_PART, this.getX()-9, (int) (this.getY()+this.height/2f-21), 210, 41, 0, 0, 210, 41, 210, 41);
        guiGraphics.pose().popPose();
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

    @Override
    public void tick() {
        oldBackgroundRotation = backgroundRotation;
        backgroundRotation += 0.35f;

        rot += 0.5f;
    }
}
