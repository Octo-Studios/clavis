package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import it.hurts.octostudios.octolib.OctoLibClient;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.particle.ExtendedUIParticle;
import it.hurts.octostudios.octolib.client.particle.UIParticle;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.octostudios.octolib.util.VectorUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GearMechanismWidget extends AbstractMinigameWidget<RotatingParent<LockPinWidget, GearMechanismWidget>> {
    public static final ResourceLocation BACKGROUND = Clavis.path("textures/minigame/background.png");
    public static final ResourceLocation COGWHEEL = Clavis.path("textures/minigame/gear.png");
    public static final ResourceLocation ARROW = Clavis.path("textures/minigame/arrow.png");
    public static final ResourceLocation ARROW_HOT = Clavis.path("textures/minigame/arrow_hot.png");
    public static final ResourceLocation CENTER = Clavis.path("textures/minigame/center.png");
    public static final ResourceLocation CENTER_WARNING_ON = Clavis.path("textures/minigame/center_warning_on.png");
    public static final ResourceLocation CENTER_WARNING_OFF = Clavis.path("textures/minigame/center_warning_off.png");

    @Setter
    OctoColor gameColor = OctoColor.WHITE;

    float rot;
    float arrowRot;
    @Setter
    @Getter
    float arrowSpeed;

    @Getter
    float maxArrowSpeed;
    float maxPins;

    public float arrowSpeedModifier;

    @Getter
    List<Integer> freeSpots = new ArrayList<>();
    @Getter
    int maxSpots;

    @Setter
    float arrowTemperature;
    @Setter
    float warningVisibility = 0f;
    boolean playing = true;

    public boolean selfDestructRule;

    public boolean isArrowHot() {
        return arrowTemperature > 0.5;
    }

    public GearMechanismWidget(LockpickingScreen screen) {
        super(0, 0, 192, 192, screen);
    }

    @Override
    public void processDifficulty(Minigame<? extends AbstractMinigameWidget<?>> game) {
        this.random = new Random(game.getSeed());
        float difficulty = game.getDifficulty();

        int ruleNumber = (int) Math.min(Math.ceil(difficulty / 0.33f), 3);

        float scaled = (2 / 3f + difficulty * (1 / 3f));

        int pins = Mth.ceil(random.nextFloat(6, 10) * scaled);
        this.maxPins = pins;
        this.maxSpots = (int) Math.round(pins * 1.5);

        this.arrowSpeed = random.nextFloat(240, 300) * scaled;
        if (random.nextBoolean()) {
            this.arrowSpeed *= -1;
        }

        this.maxArrowSpeed = Math.abs(arrowSpeed);

        List<Integer> list = IntStream.range(0, maxSpots).boxed().collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(list, random);
        list.stream().limit(pins).forEach(i -> {
            float newI = i * (360f / maxSpots) + random.nextFloat(-5, 5) * (10f / maxSpots);
            this.children.add(LockPinWidget.create(Math.round(this.width / 2f), Math.round(this.height / 2f), newI, this));
        });

        this.freeSpots = new ArrayList<>(list.subList(pins, list.size()));

        if (game.getRules().isEmpty()) {
            List<Rule<GearMechanismWidget>> rules = Lists.newArrayList(Rule.getRegisteredRules(GearMechanismWidget.class));
            Collections.shuffle(rules, this.random);
            ((Minigame<GearMechanismWidget>) game).addRules(rules.stream().limit(ruleNumber).toList());
        }
    }

    public void setRot(float rot) {
        this.rot = (float) normalizeAngle(rot);
    }

    @Override
    public void playHurtAnimation() {
        this.mainTween.kill();
        this.mainTween = Tween.create();
        this.mainTween.tweenMethod(this::setGameColor, new OctoColor(1.25f, 0.4f, 0.4f, 1f), OctoColor.WHITE, 0.5f);
        this.mainTween.start();
    }

    @Override
    public void playWinAnimation() {
        this.screen.win();
    }

    @Override
    public void playLoseAnimation() {
        this.playing = false;

        this.arrowTween.kill();
        this.arrowTween = Tween.create();
        this.arrowTween.tweenMethod(this::setArrowSpeed, this.arrowSpeed, 0f, 0.75f);
        this.arrowTween.start();

        this.mainTween.kill();
        this.mainTween = Tween.create();
        this.mainTween.tweenMethod(this::setGameColor, OctoColor.WHITE, new OctoColor(1f, 0.2f, 0.2f, 1f), 1.5f);
        this.mainTween.tweenInterval(0.5f);
        this.mainTween.tweenRunnable(this.screen::win);
        this.mainTween.start();
    }

    public void activateSelfDestruction() {
        warningVisibility = 0;
        arrowTemperature = 0;

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_ACTIVATE, 1.25f));
        selfDestructionTween.kill();
        selfDestructionTween = Tween.create();
        selfDestructionTween.tweenMethod(this::setWarningVisibility, 0f, 1f, 0.2d);
        selfDestructionTween.tweenInterval(0.3);
        selfDestructionTween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.FIRE_AMBIENT, 0.85f)));
        selfDestructionTween.tweenMethod(this::setArrowTemperature, 0f, 1f, 1d).setEaseType(EaseType.EASE_IN_OUT).setTransitionType(TransitionType.QUAD);
        selfDestructionTween.tweenInterval(1);
        selfDestructionTween.tweenMethod(this::setWarningVisibility, 1f, 0f, 0.2d);
        selfDestructionTween.parallel().tweenMethod(this::setArrowTemperature, 1f, 0f, 0.6d).setEaseType(EaseType.EASE_OUT).setTransitionType(TransitionType.EXPO);
        selfDestructionTween.start();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());
        RenderSystem.enableBlend();
        guiGraphics.pose().pushPose();
        int offset = (int) ((192 - 146) / 2f);
        guiGraphics.blit(BACKGROUND, this.getX() + offset, this.getY() + offset, 146, 146, 0, 0, 146, 146, 146, 146);
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rot));
        guiGraphics.pose().translate(-this.width / 2f - this.getX(), -this.height / 2f - this.getY(), 0);

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(COGWHEEL, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(arrowRot));
        guiGraphics.blit(ARROW, -8, -6, 16, 42, 0, 0, 16, 42, 16, 42);
        if (arrowTemperature > 0) {
            RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), arrowTemperature);
            RenderSystem.enableBlend();
            guiGraphics.blit(ARROW_HOT, -8, -6, 16, 42, 0, 0, 16, 42, 16, 42);
            RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());
            RenderSystem.disableBlend();
        }
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.blit(selfDestructRule ? CENTER_WARNING_OFF : CENTER, -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
        if (warningVisibility > 0) {
            RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), warningVisibility);
            RenderSystem.enableBlend();
            guiGraphics.blit(CENTER_WARNING_ON, -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
            RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());
            RenderSystem.disableBlend();
        }
        guiGraphics.pose().popPose();
        arrowRot = (float) normalizeAngle((arrowRot + OctoLibClient.getDeltaTime() * (this.arrowSpeed + (this.arrowSpeedModifier * (this.arrowSpeed / this.maxArrowSpeed)))));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !playing) {
            return false;
        }

        Vector2d vector2d = new Vector2d(mouseX, mouseY);
        Vector2d center = new Vector2d(this.getX() + this.width / 2f, this.getY() + this.height / 2f);
        double distance = vector2d.distance(center);

        if (distance > this.width / 2f) {
            return false;
        }

        boolean result;
        if (this.isArrowHot()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.FIRE_EXTINGUISH, 1.25f));
            result = false;
        } else {
            result = this.unlockPin(arrowRot);
        }

        if (!result) {
            this.screen.getGame().hurt();
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOOD_HIT, 1f));
        }

        this.screen.getGame().processOnClickRules(result);

        if (this.areAllPinsActive()) {
            this.screen.getGame().win();
        }

        return result;
    }

    public boolean areAllPinsActive() {
        return this.children.stream()
                .filter(rotating -> !(rotating.children.getFirst() instanceof FakePinWidget))
                .allMatch(rotating -> rotating.children.getFirst().active);
    }

    public boolean unlockPin(float arrowAngle) {
        double angle = normalizeAngle(arrowAngle - this.rot);
        Optional<RotatingParent<LockPinWidget, GearMechanismWidget>> optional = this.children.stream()
                .filter(rotatingParent -> roughlyEquals(normalizeAngle(rotatingParent.rot), angle, 10))
                .findFirst();

        if (optional.isEmpty()) {
            return false;
        }

        boolean result = optional.get().children.getFirst().activate();

        if (result) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.5f));
        }

        return result;
    }

    private static boolean roughlyEquals(double first, double second, double margin) {
        double diff = Math.abs(first-second);
        diff = Math.min(diff, 360-diff);
        return diff <= margin;
    }

    private static double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public void flipArrowDirection() {
        if (!playing) {
            return;
        }

        arrowTween.kill();
        arrowTween = Tween.create();
        arrowTween.tweenMethod(this::setArrowSpeed, this.arrowSpeed, this.maxArrowSpeed * -sign(arrowSpeed), 0.5f);
        arrowTween.start();
    }

    public void rotateGear(float degrees) {
        if (!playing) {
            return;
        }

        gearTween.kill();
        gearTween = Tween.create();
        gearTween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.GRINDSTONE_USE, 0.3f))).setDelay(0.5f);
        gearTween.parallel().tweenMethod(this::setRot, this.rot, this.rot + degrees, 4f)
                .setEaseType(EaseType.EASE_IN_OUT)
                .setTransitionType(TransitionType.QUART);
        gearTween.start();
    }

    public void deactivateAllPins() {
        this.children().forEach(child -> {
            if (!(child.children.getFirst() instanceof FakePinWidget)) {
                child.children.getFirst().deactivate();
            }
        });
    }

    Tween gearTween = Tween.create();
    Tween arrowTween = Tween.create();
    Tween mainTween = Tween.create();
    Tween selfDestructionTween = Tween.create();

    public void killAllTweens() {
        gearTween.kill();
        arrowTween.kill();
        mainTween.kill();
        selfDestructionTween.kill();
    }

    public static int sign(double value) {
        return value >= 0 ? 1 : -1;
    }

    @Override
    public void tick() {
        if (!this.isArrowHot()) {
            return;
        }

        float x = this.getX() + this.width/2f + random.nextFloat(-3, 3);
        float y = this.getY() + this.height/2f + random.nextFloat(-3, 3);

        for (int i = 0; i < 3; i++) {
            Vector2f bananarotate = VectorUtils.rotate(new Vector2f(0, random.nextFloat(30,35)), this.arrowRot);
            ExtendedUIParticle particle = new ExtendedUIParticle(new UIParticle.Texture2D(Clavis.path("textures/particle/pixel.png"), 1, 1), 0.4f,
                    random.nextInt(10,30), x + bananarotate.x, y + bananarotate.y, UIParticle.Layer.SCREEN, 10);
            particle.setScreen(this.screen);
            particle.setColors(OctoColor.RED, OctoColor.RED, new OctoColor(1f, 1f, 0f, 1f), OctoColor.WHITE, new OctoColor(0.05f, 0.05f, 0.05f, 1f));
            particle.setDirection(new Vector2f(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f).normalize());
            particle.setGravityDirection(new Vector2f(0, -1));
            particle.setGravity(0.15f);
            float size = random.nextFloat(0.75f, 1.5f);
            particle.getTransform().setSize(new Vector2f(size, size));
            particle.setRollVelocity(random.nextFloat(-10, 10));
            particle.instantiate();
        }
    }
}
