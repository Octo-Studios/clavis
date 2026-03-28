package it.hurts.shatterbyte.clavis.common.client.screen.widget.magma;

import it.hurts.shatterbyte.clavis.common.client.screen.LockpickingScreen;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.RotatingParent;
import it.hurts.shatterbyte.clavis.common.minigame.Minigame;
import net.minecraft.client.Minecraft;

public class MagmaWheelWidget extends AbstractMinigameWidget<RotatingParent<FireballWidget, MagmaWheelWidget>> {
    public MagmaWheelWidget() {
        super(0, 0, 192, 192, (LockpickingScreen) Minecraft.getInstance().screen);
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
