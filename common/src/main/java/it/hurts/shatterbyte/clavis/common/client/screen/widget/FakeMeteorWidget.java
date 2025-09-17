package it.hurts.shatterbyte.clavis.common.client.screen.widget;

import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;
import org.joml.Vector2f;

public class FakeMeteorWidget extends MeteorWidget {
    public FakeMeteorWidget(int x, int y, int size, MirrorWidget parent) {
        super(x, y, size, parent);
        this.conservedMomentum = 0.85f;
    }

    Tween susTween = Tween.create();

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.getParent().regenerateAll();
        this.getParent().getMinigame().processOnClickRules(false);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.AXE_STRIP, 1.4f, 0.33f));
    }

    @Override
    public void tick() {
        if (this.parent.minigame.getTickCount() % 40 == 0) {
            susTween.kill();
            susTween = Tween.create();
            susTween.setTransitionType(TransitionType.ELASTIC);
            susTween.setEase(EaseType.EASE_OUT);
            susTween.tweenMethod(this::setVisualSize, new Vector2f(0.9f, 0.9f), new Vector2f(1,1), 0.66f);
            susTween.start();
        } else if (this.getParent().random.nextFloat() < 0.06f) {
            this.velocity.add(this.getParent().random.nextFloat(-0.33f, 0.33f), this.getParent().random.nextFloat(-0.33f, 0.33f));
        }

        super.tick();
    }
}
