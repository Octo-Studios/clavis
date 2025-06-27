package it.hurts.octostudios.clavis.common.client.screen;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.RuleWidget;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import it.hurts.octostudios.clavis.common.minigame.rule.OverworldRules;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import it.hurts.octostudios.octolib.client.animation.Tween;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class LockpickingScreen extends Screen {
    public static final ResourceLocation EMPTY_HEART = Clavis.rl("textures/lockpicking/empty_heart.png");
    public static final ResourceLocation HEART = Clavis.rl("textures/lockpicking/heart.png");
    public static final ResourceLocation EASY = Clavis.rl("textures/icon/easy.png");
    public static final ResourceLocation MEDIUM = Clavis.rl("textures/icon/medium.png");
    public static final ResourceLocation HARD = Clavis.rl("textures/icon/hard.png");
    public static final ResourceLocation QUALITY = Clavis.rl("textures/icon/quality.png");
    public static final ResourceLocation TIME = Clavis.rl("textures/icon/time.png");

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

        this.gear.setPosition(Math.round(this.width/2f-this.gear.getWidth()+8), Math.round(this.height/2f-this.gear.getHeight()/2f-8));

        int y = Math.round(this.height/2f-gear.getHeight()/2f);
        for (Rule<?> rule : this.game.getRules()) {
            RuleWidget widget = new RuleWidget(Math.round(this.width/2f)+20, y-8, rule);
            y += widget.getHeight()+8;

            this.addRenderableWidget(widget);
        }

        this.addRenderableWidget(this.gear);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.gear.getX()+this.gear.getWidth()/2f, this.gear.getY()+this.gear.getHeight(), 0);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(200, 0, 0);
        guiGraphics.blit(TIME, 0, 0, 16, 16, 0, 0, 16, 16, 16, 16);
        guiGraphics.drawString(Minecraft.getInstance().font, "☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺☺", 20, 4, 0xffffff, true);
        guiGraphics.pose().popPose();

        guiGraphics.pose().translate(-80, 0, 0);
        for (int i = 1; i <= 5; i++) {
            guiGraphics.blit(i <= this.getGame().getHealth() ? HEART : EMPTY_HEART, 0, 0, 32, 32, 0, 0, 32, 32, 32, 32);
            guiGraphics.pose().translate(32, 0, 0);
        }

        guiGraphics.pose().popPose();
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
