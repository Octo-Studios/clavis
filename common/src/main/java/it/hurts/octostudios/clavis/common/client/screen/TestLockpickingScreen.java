package it.hurts.octostudios.clavis.common.client.screen;

import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestLockpickingScreen extends Screen {
    public TestLockpickingScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new GearMechanismWidget((int) (this.width/2f-96), (int) (this.height/2f-96)));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
