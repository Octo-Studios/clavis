package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.octostudios.octolib.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2f;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MinigameInfoWidget extends AbstractWidget {
    public static final ResourceLocation EASY = Clavis.path("textures/icon/easy.png");
    public static final ResourceLocation MEDIUM = Clavis.path("textures/icon/medium.png");
    public static final ResourceLocation HARD = Clavis.path("textures/icon/hard.png");
    public static final ResourceLocation TIME = Clavis.path("textures/icon/time.png");
    public static final ResourceLocation QUALITY = Clavis.path("textures/icon/quality.png");
    public static final ResourceLocation STAT_BG = Clavis.path("textures/lockpicking/stat_background.png");

    Minigame<?> game;

    public MinigameInfoWidget(Minigame<?> game) {
        super(0, 0, 160, 16, Component.empty());
        this.game = game;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float difficulty = game.getDifficulty();
        long tickCount = game.getTickCount();
        float lootQuality = game.getLootQuality();

        Font font = Minecraft.getInstance().font;
        LocalTime time = LocalTime.ofSecondOfDay(Mth.floor(tickCount/20f));
        String timeString = time.format(DateTimeFormatter.ISO_LOCAL_TIME);
        if (timeString.startsWith("00:")) {
            timeString = timeString.substring(3);
        }
        ResourceLocation difficultyIcon = difficulty < 0.33f ? EASY : difficulty < 0.66f ? MEDIUM : HARD;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 1);
        renderStat(guiGraphics, Math.round(difficulty*100)+"%", font, difficultyIcon);
        guiGraphics.pose().translate(54, 0, 0);
        renderStat(guiGraphics, timeString, font, TIME);
        guiGraphics.pose().translate(54, 0, 0);
        renderStat(guiGraphics, Math.round(lootQuality*100)+"%", font, QUALITY);
        guiGraphics.pose().popPose();
    }

    private void renderStat(GuiGraphics guiGraphics, String string, Font font, ResourceLocation icon) {
        guiGraphics.blit(STAT_BG, 0, 0, 52, 16, 0, 0, 52, 16, 52, 16);
        guiGraphics.blit(icon, 2, 2, 12, 13, 0, 0, 12, 13, 12, 13);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(18, 5.5, 0);
        guiGraphics.pose().scale(0.75f, 0.75f, 1f);
        guiGraphics.drawString(font, Component.literal(string).withStyle(ChatFormatting.BOLD), 0, 0, 0xd7e3f2, false);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
