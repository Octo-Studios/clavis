package it.hurts.octostudios.clavis.common.client.screen;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.ClavisClient;
import it.hurts.octostudios.clavis.common.client.particle.HalfHeartUIParticle;
import it.hurts.octostudios.clavis.common.client.particle.HeartPartUIParticle;
import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.MinigameInfoWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.RuleWidget;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import it.hurts.octostudios.clavis.common.minigame.rule.OverworldRules;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import it.hurts.octostudios.clavis.common.network.packet.FinishLockpickingPacket;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.particle.ParticleSystem;
import it.hurts.octostudios.octolib.client.particle.UIParticle;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class LockpickingScreen extends Screen {
    public static final ResourceLocation EMPTY_HEART = Clavis.path("textures/lockpicking/empty_heart.png");
    public static final ResourceLocation HEART = Clavis.path("textures/lockpicking/heart.png");
    public static final ResourceLocation EASY = Clavis.path("textures/icon/easy.png");
    public static final ResourceLocation MEDIUM = Clavis.path("textures/icon/medium.png");
    public static final ResourceLocation HARD = Clavis.path("textures/icon/hard.png");
    public static final ResourceLocation QUALITY = Clavis.path("textures/icon/quality.png");
    public static final ResourceLocation TIME = Clavis.path("textures/icon/time.png");

    @Getter
    Minigame<GearMechanismWidget> game;
    GearMechanismWidget gear;
    BlockPos blockPos;
    Lock lock;

    public LockpickingScreen(BlockPos blockPos, Lock lock) {
        super(Component.empty());
        this.blockPos = blockPos;
        this.lock = lock;

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
    }

    @Override
    public void tick() {
        this.children().forEach(child -> {
            if (child instanceof Tickable tickable) {
                tickable.tick();
            }
        });

        game.processOnTickRules();
    }

    public void win() {
        ClavisClient.SCREEN_CACHE.remove(lock);
        NetworkManager.sendToServer(new FinishLockpickingPacket(blockPos, lock, this.game.getLootQuality()));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    protected void init() {
        this.addOrRepositionGear();
    }

    private void addOrRepositionGear() {
        if (this.gear == null) {
            this.gear = new GearMechanismWidget(this);
            this.game = new Minigame<>(this.gear);

            game.load(lock);
            gear.processDifficulty(game);
            game.processOnCreateRules();
        }

        this.gear.setPosition(Math.round(this.width/2f-this.gear.getWidth()+8), Math.round(this.height/2f-this.gear.getHeight()/2f-8));

        int x = Math.round(this.width/2f)+20;
        int y = Math.round(this.height/2f-gear.getHeight()/2f)-8;
        for (Rule<?> rule : this.game.getRules()) {
            RuleWidget widget = new RuleWidget(x, y, rule);
            this.addRenderableWidget(widget);
            y += widget.getHeight()+2;
        }

        MinigameInfoWidget infoWidget = new MinigameInfoWidget(this.game);
        infoWidget.setPosition(x, y);
        this.addRenderableWidget(infoWidget);
        this.addRenderableWidget(this.gear);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    public void animateHeart() {
        float x = this.gear.getX()+this.gear.getWidth()/2f - 64;
        float y = this.gear.getY()+this.gear.getHeight() + 16;

        int health = this.game.getHealth();
        x += health * 32;

        HalfHeartUIParticle left = new HalfHeartUIParticle(false, x, y, UIParticle.Layer.SCREEN, 1);
        HalfHeartUIParticle right = new HalfHeartUIParticle(true, x, y, UIParticle.Layer.SCREEN, 1);
        left.setScreen(this);
        right.setScreen(this);
        left.instantiate();
        right.instantiate();
        for (int i = 0; i < 3; i++) {
            HeartPartUIParticle particle = new HeartPartUIParticle(x, y, UIParticle.Layer.SCREEN, 0.5f);
            particle.setScreen(this);
            particle.instantiate();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.gear.getX()+this.gear.getWidth()/2f, this.gear.getY()+this.gear.getHeight(), 0);
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
            this.gear.killAllTweens();
            this.gear = null;
            rebuildWidgets();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
