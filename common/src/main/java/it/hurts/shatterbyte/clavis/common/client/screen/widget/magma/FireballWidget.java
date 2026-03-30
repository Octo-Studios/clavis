package it.hurts.shatterbyte.clavis.common.client.screen.widget.magma;

import it.hurts.octostudios.octolib.client.screen.widget.Child;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.RotatingParent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class FireballWidget extends AbstractWidget implements Child<RotatingParent> {
    public FireballWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public @Nullable RotatingParent getParent() {
        return null;
    }

    @Override
    public void setParent(@Nullable RotatingParent rotatingParent) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
