package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.octolib.client.screen.widget.Child;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class LockPinWidget extends AbstractWidget implements Child<GearMechanismWidget> {
    public LockPinWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public @Nullable GearMechanismWidget getParent() {
        return null;
    }

    @Override
    public void setParent(@Nullable GearMechanismWidget gearMechanismWidget) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
