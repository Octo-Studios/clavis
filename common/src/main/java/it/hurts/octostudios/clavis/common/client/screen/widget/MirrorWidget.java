package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class MirrorWidget extends AbstractMinigameWidget<GuiEventListener> {
    protected MirrorWidget(LockpickingScreen screen) {
        super(0, 0, 192, 192, screen);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
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

    }
}
