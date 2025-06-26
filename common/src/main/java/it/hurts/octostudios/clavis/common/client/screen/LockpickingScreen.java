package it.hurts.octostudios.clavis.common.client.screen;

import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import it.hurts.octostudios.clavis.common.minigame.rule.OverworldRules;
import it.hurts.octostudios.octolib.client.animation.Tween;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class LockpickingScreen extends Screen {
    @Getter
    Minigame<GearMechanismWidget> game;
    GearMechanismWidget gear;
    long tickCount;

    public LockpickingScreen() {
        super(Component.empty());
        Tween tween = Tween.create().setLoops(-1);
        tween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_BUTTON_CLICK_ON, 2F)));
        tween.tweenInterval(0.5);
        tween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_BUTTON_CLICK_OFF, 1.6F)));
        tween.tweenInterval(0.5);
        tween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_BUTTON_CLICK_OFF, 1.6F)));
        tween.tweenInterval(0.5);
        tween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_BUTTON_CLICK_OFF, 1.6F)));
        tween.tweenInterval(0.5);
        tween.tweenRunnable(() -> {
            if (Minecraft.getInstance().screen != this) {
                tween.kill();
            }
        });
        tween.start();
    }

    @Override
    public void tick() {
        this.children().forEach(child -> {
            if (child instanceof Tickable tickable) {
                tickable.tick();
            }
        });

        game.processOnTickRules(tickCount);
        tickCount++;
    }

    @Override
    protected void init() {
        this.addOrRepositionGear();
    }

    private void addOrRepositionGear() {
        if (this.gear == null) {
            this.gear = new GearMechanismWidget(this);
            this.game = new Minigame<>(this.gear);

            game.addRules(OverworldRules.ROTATE_GEAR, OverworldRules.MOOD_SWINGS, OverworldRules.FAKE_PIN);
            gear.processDifficulty(game.getDifficulty());

            game.processOnCreateRules();
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
