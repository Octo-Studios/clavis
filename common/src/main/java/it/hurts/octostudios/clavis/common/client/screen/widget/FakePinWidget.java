package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
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

    public FakePinWidget(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean activate() {
        if (this.parent.getParent() instanceof GearMechanismWidget gear) {
            gear.children().forEach(child -> {
                if (!(child.children.getFirst() instanceof FakePinWidget)) {
                    child.children.getFirst().active = false;
                }
            });

            if (this.getParent() != null) {
                this.getParent().setRot(gear.random.nextFloat(0, 360));
            }
        }
        return false;
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
