package it.hurts.shatterbyte.clavis.common.client.screen.widget;

import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.util.OctoColor;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;

public class FakePinWidget extends LockPinWidget {
    Tween fakeTween = Tween.create();
    @Setter
    OctoColor pinColor = OctoColor.WHITE;
    public int value = Integer.MIN_VALUE;

    public FakePinWidget(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean activate() {
        if (this.parent.getParent() instanceof GearMechanismWidget gear) {
            gear.deactivateAllPins();
        }
        return true;
    }

    public void moveToRandomPosition(GearMechanismWidget gear) {
        int newValue = gear.getFreeSpots().get(gear.getRandom().nextInt(0, gear.getFreeSpots().size()));
        if (this.getParent() != null) {
            this.getParent().setRot(newValue * (360f / gear.getMaxSpots()));
        }

        if (value >= 0) {
            gear.getFreeSpots().add(value);
        }

        this.value = newValue;
        gear.getFreeSpots().remove(Integer.valueOf(newValue));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void letThemKnow() {
        fakeTween.kill();
        fakeTween = Tween.create();
        fakeTween.tweenMethod(this::setYScale, 0.93f, 1f, 0.75d).setTransitionType(TransitionType.ELASTIC).setEaseType(EaseType.EASE_OUT);
        fakeTween.start();
    }
}
