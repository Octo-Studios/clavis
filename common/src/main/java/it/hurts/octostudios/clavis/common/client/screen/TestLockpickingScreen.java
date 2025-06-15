package it.hurts.octostudios.clavis.common.client.screen;

import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestLockpickingScreen extends Screen {
    GearMechanismWidget gear;

    public TestLockpickingScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        this.addOrRepositionGear();
    }

    private void addOrRepositionGear() {
        if (this.gear == null) {
            this.gear = new GearMechanismWidget(0, 0);
        }
        this.gear.setPosition(Math.round(this.width/2f-this.gear.getWidth()/2f-72), Math.round(this.height/2f-96));
        this.addRenderableWidget(this.gear);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            this.gear = null;
            rebuildWidgets();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
