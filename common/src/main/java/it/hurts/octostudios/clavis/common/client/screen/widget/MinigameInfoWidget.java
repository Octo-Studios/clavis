package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.data.TooltipInfoData;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MinigameInfoWidget extends AbstractWidget {
    public static final ResourceLocation EASY = Clavis.path("textures/icon/easy.png");
    public static final ResourceLocation MEDIUM = Clavis.path("textures/icon/medium.png");
    public static final ResourceLocation HARD = Clavis.path("textures/icon/hard.png");
    public static final ResourceLocation TIME = Clavis.path("textures/icon/time.png");
    public static final ResourceLocation QUALITY = Clavis.path("textures/icon/quality.png");
    public static final ResourceLocation STAT_BG = Clavis.path("textures/lockpicking/stat_background.png");

    TooltipInfoData difficultyInfo;
    TooltipInfoData timeInfo;
    TooltipInfoData qualityInfo;

    Minigame<?> game;

    public MinigameInfoWidget(Minigame<?> game) {
        super(0, 0, 160, 16, Component.empty());
        this.game = game;
        Font font = Minecraft.getInstance().font;

        difficultyInfo = new TooltipInfoData(font, "difficulty", Math.round(100/0.75f));
        timeInfo = new TooltipInfoData(font, "time", Math.round(100/0.75f));
        qualityInfo = new TooltipInfoData(font, "quality", Math.round(100/0.75f));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float difficulty = game.getDifficulty();
        long tickCount = game.getTickCount();
        float lootQuality = game.getLootQuality();

        float mouseHovered = Math.clamp((float) (mouseX - this.getX()) / this.getWidth(), 0f, 1f);
        boolean difficultyHovered = isHovered() && mouseHovered < 0.33f;
        boolean timeHovered = isHovered() && mouseHovered >= 0.33f && mouseHovered < 0.66f;
        boolean qualityHovered = isHovered() && mouseHovered >= 0.66f;

        Font font = Minecraft.getInstance().font;
        LocalTime time = LocalTime.ofSecondOfDay(Mth.floor(tickCount/20f));
        String timeString = time.format(DateTimeFormatter.ISO_LOCAL_TIME);
        if (timeString.startsWith("00:")) {
            timeString = timeString.substring(3);
        }
        ResourceLocation difficultyIcon = difficulty < 0.33f ? EASY : difficulty < 0.66f ? MEDIUM : HARD;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 1);
        renderStat(guiGraphics, difficultyHovered,Math.round(difficulty*100)+"%", font, difficultyIcon);
        guiGraphics.pose().translate(54, 0, 0);
        renderStat(guiGraphics, timeHovered, timeString, font, TIME);
        guiGraphics.pose().translate(54, 0, 0);
        renderStat(guiGraphics, qualityHovered,Math.round(lootQuality*100)+"%", font, QUALITY);
        guiGraphics.pose().popPose();

        boolean isDescription = LockpickingScreen.hasShiftDown();

        if (qualityHovered) {
            LockpickingScreen.renderTooltip(qualityInfo, guiGraphics, mouseX, mouseY, partialTick, isDescription);
        } else if (timeHovered) {
            LockpickingScreen.renderTooltip(timeInfo, guiGraphics, mouseX, mouseY, partialTick, isDescription);
        } else if (difficultyHovered) {
            LockpickingScreen.renderTooltip(difficultyInfo, guiGraphics, mouseX, mouseY, partialTick, isDescription);
        }
    }

    private void renderStat(GuiGraphics guiGraphics, boolean isHovered, String string, Font font, ResourceLocation icon) {
        if (isHovered) {
            guiGraphics.fill(0, 0, 52, 16, 0, 0xffd7e3f2);
            guiGraphics.fill(-1, 0, 52+1, 16, 0, 0xffd7e3f2);
            guiGraphics.fill(0, -1, 52, 16+1, 0, 0xffd7e3f2);
        }
        guiGraphics.blit(STAT_BG, 0, 0, 52, 16, 0, 0, 52, 16, 52, 16);
        guiGraphics.blit(icon, 2, 2, 12, 13, 0, 0, 12, 13, 12, 13);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(18, 5.5, 0);
        guiGraphics.pose().scale(0.75f, 0.75f, 1f);
        guiGraphics.drawString(font, Component.literal(string).withStyle(ChatFormatting.BOLD), 0, 0, 0xd7e3f2, false);
        guiGraphics.pose().popPose();
    }

    @Override
    public void playDownSound(SoundManager handler) {
        //super.playDownSound(handler);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
