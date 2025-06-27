package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import it.hurts.octostudios.octolib.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class RuleWidget extends AbstractWidget {
    public static final ResourceLocation BOTTOM = Clavis.rl("textures/lockpicking/bottom_box.png");
    public static final ResourceLocation FILL = Clavis.rl("textures/lockpicking/woodplate.png");
    public static final ResourceLocation SIDE = Clavis.rl("textures/lockpicking/sidewalls.png");
    public static final ResourceLocation TOP = Clavis.rl("textures/lockpicking/top_banner.png");

    ResourceLocation icon;
    List<FormattedCharSequence> description;

    public RuleWidget(int x, int y, Rule<?> rule) {
        super(x, y, 160, 32, Component.translatable(rule.getId().toLanguageKey("rule")).withStyle(ChatFormatting.BOLD));
        Component description = Component.translatable(rule.getId().toLanguageKey("rule", "description"));
        this.description = Minecraft.getInstance().font.split(description, Math.round(152/0.75f));

        int lines = this.description.size();
        this.setHeight((int) Math.max(32, 22+lines*10*0.75f));

        this.icon = ResourceLocation.fromNamespaceAndPath(rule.getId().getNamespace(), "textures/icon/rule/"+rule.getId().getPath()+".png");
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        if (this.isHovered()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), 0, 0xffd7e3f2);
            guiGraphics.fill(this.getX()-1, this.getY(), this.getX()+this.getWidth()+1, this.getY()+this.getHeight(), 0, 0xffd7e3f2);
            guiGraphics.fill(this.getX(), this.getY()-1, this.getX()+this.getWidth(), this.getY()+this.getHeight()+1, 0, 0xffd7e3f2);
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 1);

        guiGraphics.blit(TOP, 0, 0, 160, 16, 0, 0, 160, 16, 160, 16);
        guiGraphics.blit(icon, 3, 3, 10, 10, 0, 0, 10, 10, 10, 10);

        RenderSystem.setShaderTexture(0, FILL);
        RenderUtils.renderTilingTexture(guiGraphics.pose(), 2, 16, 0, 0, 156, 8, 156, this.height-20, 0, false, true);
        RenderSystem.setShaderTexture(0, SIDE);
        RenderUtils.renderTilingTexture(guiGraphics.pose(), 0, 16, 0, 0, 160, 1, 160, this.height-32, 0, false, true);
        guiGraphics.blit(BOTTOM, 0, this.height-16, 160, 16, 0, 0, 160, 16, 160, 16);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(18, 5.5, 0);
        guiGraphics.pose().scale(0.75f, 0.75f, 1f);
        guiGraphics.drawString(font, this.getMessage(), 0, 0, 0xffd7e3f2, true);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(6, 18, 0);
        guiGraphics.pose().scale(0.75f, 0.75f, 1f);
        int y = 0;
        for (FormattedCharSequence line : this.description) {
            guiGraphics.drawString(font, line, 0, y, 0xeed1ad, false);
            y+=10;
        }
        guiGraphics.pose().popPose();
        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
