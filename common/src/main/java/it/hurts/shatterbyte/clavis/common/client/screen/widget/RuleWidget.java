package it.hurts.shatterbyte.clavis.common.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.octostudios.octolib.util.RenderUtils;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.screen.LockpickingScreen;
import it.hurts.shatterbyte.clavis.common.minigame.rule.Rule;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Vector2i;

import java.util.List;

public class RuleWidget extends AbstractWidget {
    public final ResourceLocation BOTTOM;
    public final ResourceLocation FILL;
    public final ResourceLocation SIDE;
    public final ResourceLocation TOP;

    ResourceLocation icon;

    LockpickingScreen screen;

    List<FormattedCharSequence> description;

    public RuleWidget(int x, int y, Rule<?> rule, LockpickingScreen screen) {
        super(x, y, 160, 32, Component.translatable(rule.getLanguageKey(screen.getGame().getMinigameType())).withStyle(ChatFormatting.BOLD));
        ResourceLocation minigameType = screen.getGame().getMinigameType();
        this.screen = screen;

        BOTTOM = Clavis.path(minigameType.getNamespace(), "textures/lockpicking/"+minigameType.getPath()+"/bottom.png");
        FILL = Clavis.path(minigameType.getNamespace(), "textures/lockpicking/"+minigameType.getPath()+"/fill.png");
        SIDE = Clavis.path(minigameType.getNamespace(), "textures/lockpicking/"+minigameType.getPath()+"/side.png");
        TOP = Clavis.path(minigameType.getNamespace(), "textures/lockpicking/"+minigameType.getPath()+"/top.png");

        Component description = Component.translatable(rule.getLanguageKey(minigameType, "description"));
        this.description = Minecraft.getInstance().font.split(description, Math.round(152/0.75f));

        int lines = this.description.size();
        this.setHeight(Math.max(32, 22+lines*8));

        this.icon = ResourceLocation.fromNamespaceAndPath(rule.getId().getNamespace(), "textures/icon/rule/"+minigameType.getPath()+"/"+rule.getId().getPath()+".png");
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        if (this.isHovered()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), 0, screen.getStyleData().getTitleColor());
            guiGraphics.fill(this.getX()-1, this.getY(), this.getX()+this.getWidth()+1, this.getY()+this.getHeight(), 0, screen.getStyleData().getTitleColor());
            guiGraphics.fill(this.getX(), this.getY()-1, this.getX()+this.getWidth(), this.getY()+this.getHeight()+1, 0, screen.getStyleData().getTitleColor());
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 1);

        guiGraphics.blit(TOP, 0, 0, 160, 16, 0, 0, 160, 16, 160, 16);
        guiGraphics.blit(icon, 3, 3, 10, 10, 0, 0, 10, 10, 10, 10);

        Vector2i fill = screen.getStyleData().getFillTextureSize();

        RenderSystem.setShaderTexture(0, FILL);
        RenderUtils.renderTilingTexture(guiGraphics.pose(), 2, 16, 0, 0, fill.x, fill.y, 156, this.height-20, 0, false, true);
        RenderSystem.setShaderTexture(0, SIDE);
        RenderUtils.renderTilingTexture(guiGraphics.pose(), 0, 16, 0, 0, 160, 1, 160, this.height-32, 0, false, true);
        guiGraphics.blit(BOTTOM, 0, this.height-16, 160, 16, 0, 0, 160, 16, 160, 16);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(18, 5.5, 0);
        guiGraphics.pose().scale(0.75f, 0.75f, 1f);
        guiGraphics.drawString(font, this.getMessage(), 0, 0, screen.getStyleData().getTitleColor(), true);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(5.75, 17.75, 0);
        int y = 0;
        for (FormattedCharSequence line : this.description) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, y, 0);
            guiGraphics.pose().scale(0.75f, 0.75f, 1f);
            guiGraphics.drawString(font, line, 0, 0, screen.getStyleData().getDescriptionColor(), false);
            guiGraphics.pose().popPose();
            y+=8;
        }
        guiGraphics.pose().popPose();
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
