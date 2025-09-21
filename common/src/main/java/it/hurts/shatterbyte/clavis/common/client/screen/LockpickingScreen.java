package it.hurts.shatterbyte.clavis.common.client.screen;

import com.google.common.collect.Lists;
import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.particle.UIParticle;
import it.hurts.octostudios.octolib.util.RenderUtils;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.ClavisClient;
import it.hurts.shatterbyte.clavis.common.client.particle.HalfHeartUIParticle;
import it.hurts.shatterbyte.clavis.common.client.particle.HeartPartUIParticle;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.MinigameInfoWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.RuleWidget;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.shatterbyte.clavis.common.data.MinigameStyleData;
import it.hurts.shatterbyte.clavis.common.data.TooltipInfoData;
import it.hurts.shatterbyte.clavis.common.minigame.Minigame;
import it.hurts.shatterbyte.clavis.common.minigame.rule.Rule;
import it.hurts.shatterbyte.clavis.common.network.packet.FinishLockpickingPacket;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class LockpickingScreen<T extends AbstractMinigameWidget<?>> extends Screen {
    public static final ResourceLocation EMPTY_HEART = Clavis.path("textures/lockpicking/empty_heart.png");
    public static final ResourceLocation HEART = Clavis.path("textures/lockpicking/heart.png");

    private final Supplier<T> widgetFactory;

    @Getter
    Minigame<T> game;
    T minigameWidget;
    BlockPos blockPos;
    @Getter
    Lock lock;
    @Getter
    MinigameStyleData styleData;

    public LockpickingScreen(BlockPos blockPos, Lock lock, Supplier<T> widgetFactory) {
        super(Component.empty());
        this.blockPos = blockPos;
        this.lock = lock;
        this.widgetFactory = widgetFactory;

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
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.position().distanceToSqr(blockPos.getCenter()) > 256) {
            Minecraft.getInstance().setScreen(null);
        }
    }

    public void finish(boolean hasLostTheGame) {
        ClavisClient.SCREEN_CACHE.remove(lock);
        NetworkManager.sendToServer(new FinishLockpickingPacket(blockPos, lock, this.game.getLootQuality(), hasLostTheGame));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    protected void init() {
        this.addOrRepositionWidget(widgetFactory.get());
    }

    @SuppressWarnings("unchecked")
    private void addOrRepositionWidget(T genericWidget) {
        if (this.minigameWidget == null) {
            this.minigameWidget = genericWidget;
            this.game = new Minigame<>(this.minigameWidget);
            this.minigameWidget.setMinigame(this.game);

            game.load(lock, Minecraft.getInstance().level);
            minigameWidget.processDifficulty(game);

            if (game.getRules().isEmpty()) {
                int ruleNumber = (int) Math.min(Math.ceil(game.getDifficulty() / 0.33f), 3);

                List<Rule<T>> rules = Lists.newArrayList(Rule.getRegisteredRules((Class<T>) minigameWidget.getClass()));
                Collections.shuffle(rules, minigameWidget.getRandom());
                rules = rules.stream().limit(ruleNumber).toList();
                game.addRules(rules);
            }

            game.processOnCreateRules();
        }

        this.minigameWidget.setPosition(Math.round(this.width/2f-this.minigameWidget.getWidth()+8), Math.round(this.height/2f-this.minigameWidget.getHeight()/2f-8));

        int x = Math.round(this.width/2f)+20;
        int y = Math.round(this.height/2f- minigameWidget.getHeight()/2f)-8;

        styleData = MinigameStyleData.get(game.getMinigameType());

        for (Rule<?> rule : this.game.getRules()) {
            RuleWidget widget = new RuleWidget(x, y, rule, this);
            this.addRenderableWidget(widget);
            y += widget.getHeight()+2;
        }

        MinigameInfoWidget infoWidget = new MinigameInfoWidget(this.game, this.styleData);
        infoWidget.setPosition(x, y);
        this.addRenderableWidget(infoWidget);
        this.addRenderableWidget(this.minigameWidget);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    public void animateHeart() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_CLUSTER_BREAK, 1.5f, 0.8f));

        float x = this.minigameWidget.getX()+this.minigameWidget.getWidth()/2f - 64;
        float y = this.minigameWidget.getY()+this.minigameWidget.getHeight() + 16;

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
        guiGraphics.pose().translate(this.minigameWidget.getX()+this.minigameWidget.getWidth()/2f, this.minigameWidget.getY()+this.minigameWidget.getHeight(), 0);
        guiGraphics.pose().translate(-80, 8, 10);
        for (int i = 1; i <= 5; i++) {
            guiGraphics.blit(i <= this.getGame().getHealth() ? HEART : EMPTY_HEART, 0, 0, 32, 32, 0, 0, 32, 32, 32, 32);
            guiGraphics.pose().translate(32, 0, 0);
        }

        guiGraphics.pose().popPose();
    }

    public static void renderTooltip(ResourceLocation minigameType, Font font, TooltipInfoData tooltipInfoData, GuiGraphics guiGraphics, double mouseX, double mouseY, float partialTick, boolean isDescription) {
        List<FormattedCharSequence> toRender = isDescription ? tooltipInfoData.getDescription() : tooltipInfoData.getSummary();
        int length = isDescription ? tooltipInfoData.getDescriptionLength() : tooltipInfoData.getSummaryLength();
        length = Math.round(length*0.75f)+2;
        int height = Math.round((toRender.size()-1)*9*0.75f)+3;

        mouseX += 1;
        mouseY -= (22 + height );

        if (mouseX+length+16 > Minecraft.getInstance().screen.width) {
            mouseX -= 18 + length;
        }

        ResourceLocation tooltip = Clavis.path(minigameType.getNamespace(), "textures/lockpicking/"+minigameType.getPath()+"/tooltip.png");

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(mouseX, mouseY, 0);

        guiGraphics.blit(tooltip, 0, 0, 8, 8, 0, 0, 8, 8, 34, 16);
        guiGraphics.blit(tooltip, 8, 0, length, 8, 8, 0, 1, 8, 34, 16);
        guiGraphics.blit(tooltip, 8 + length, 0, 8, 8, 9, 0, 8, 8, 34, 16);
        //guiGraphics.blit(tooltip, 0, 8, 8, height, 0, 8, 8, 8, 17, 24);
        RenderUtils.renderTilingTexture(guiGraphics.pose(), 0, 8, 17, 8, 34, 16, 8, height, 0f, true, true);
        guiGraphics.blit(tooltip, 8, 8, length, height, 8+17, 8, 1, 8, 34, 16);
        //guiGraphics.blit(tooltip, 8 + length, 8, 8, height, 9, 8, 8, 8, 17, 24);
        RenderUtils.renderTilingTexture(guiGraphics.pose(), 8+length, 8, 9+17, 8, 34, 16, 8, height, 0f, true, true);
        guiGraphics.blit(tooltip, 0, 8 + height, 8, 8, 0, 8, 8, 8, 34, 16);
        guiGraphics.blit(tooltip, 8, 8 + height, length, 8, 8, 8, 1, 8, 34, 16);
        guiGraphics.blit(tooltip, 8 + length, 8 + height, 8, 8, 9, 8, 8, 8, 34, 16);

        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(mouseX+9, mouseY+8, 1);
        guiGraphics.pose().scale(0.75f, 0.75f, 1f);
        for (FormattedCharSequence sequence : toRender) {
            guiGraphics.drawString(font, sequence, 0, 0, 0xff732f20, false);
            guiGraphics.pose().translate(0, 9, 0);
        }
        guiGraphics.pose().popPose();

        //guiGraphics.renderTooltip(font, toRender, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            this.minigameWidget.killTweens();
            this.minigameWidget = null;
            rebuildWidgets();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
