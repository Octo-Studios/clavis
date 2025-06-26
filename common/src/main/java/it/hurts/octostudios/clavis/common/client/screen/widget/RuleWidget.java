package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class RuleWidget extends AbstractWidget {
    Component description = Component.empty();

    public RuleWidget(int x, int y, Rule<?> rule) {
        super(x, y, 156, 32, Component.translatable(rule.getId().toLanguageKey("rule")));
        this.description = Component.translatable(rule.getId().toLanguageKey("rule", "description"));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
