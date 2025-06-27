package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.octolib.OctoLibClient;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.util.AnimationUtils;
import it.hurts.octostudios.octolib.util.OctoColor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector2d;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GearMechanismWidget extends AbstractMinigameWidget<RotatingParent<LockPinWidget, GearMechanismWidget>> {
    public static final ResourceLocation BACKGROUND = Clavis.path("textures/minigame/background.png");
    public static final ResourceLocation COGWHEEL = Clavis.path("textures/minigame/gear.png");
    public static final ResourceLocation ARROW = Clavis.path("textures/minigame/arrow.png");
    public static final ResourceLocation CENTER = Clavis.path("textures/minigame/center.png");
    public static final ResourceLocation CENTER_WARNING = Clavis.path("textures/minigame/center_warning.png");

    @Setter
    OctoColor gameColor = OctoColor.WHITE;

    float rot;
    float arrowRot;
    @Setter
    @Getter
    float arrowSpeed;

    float maxArrowSpeed;
    float maxPins;

    @Getter
    List<Integer> freeSpots;
    @Getter
    int maxSpots;

    @Getter
    boolean arrowHot;

    @Setter
    float arrowTemperature;
    @Setter
    float warningVisibiity = 0f;
    boolean playing = true;

    public GearMechanismWidget(LockpickingScreen screen) {
        super(0, 0, 192, 192, screen);
    }

    public void processDifficulty(float difficulty) {
        int pins = Mth.ceil(random.nextInt(6, 11) * difficulty);
        this.maxPins = pins;
        this.maxSpots = (int) Math.round(pins * 1.5);

        this.arrowSpeed = random.nextFloat(240, 300) * difficulty;
        this.maxArrowSpeed = Math.abs(arrowSpeed);

        List<Integer> list = IntStream.range(0, maxSpots).boxed().collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(list);
        list.stream().limit(pins).forEach(i -> {
            float newI = i * (360f / maxSpots) + random.nextFloat(-5, 5);
            this.children.add(LockPinWidget.create(Math.round(this.width / 2f), Math.round(this.height / 2f), newI, this));
        });

        this.freeSpots = list.subList(pins, list.size());
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

    }

    @Override
    public void playLoseAnimation() {
        this.arrowTween.kill();
        this.arrowTween = Tween.create();
        this.arrowTween.tweenMethod(this::setArrowSpeed, this.arrowSpeed, 0f, 0.75f);
        this.arrowTween.start();

        this.mainTween.kill();
        this.mainTween = Tween.create();
        this.mainTween.tweenMethod(this::setGameColor, OctoColor.WHITE, new OctoColor(1f, 0.2f, 0.2f, 1f), 1.5f);
        this.mainTween.tweenInterval(0.5f);
        this.mainTween.tweenRunnable(() -> Minecraft.getInstance().setScreen(null));
        this.mainTween.start();
    }

    public void activateSelfDestruction() {
        warningVisibiity = 0;
        arrowTemperature = 0;
        arrowHot = false;

        selfDestructionTween.kill();
        selfDestructionTween = Tween.create();
        selfDestructionTween.tweenMethod(this::setWarningVisibiity, 0f, 1f, 0.2d);
        selfDestructionTween.tweenInterval(0.5);
        selfDestructionTween.tweenRunnable(() -> this.arrowHot = true);
        selfDestructionTween.tweenMethod(this::setArrowTemperature, 0f, 1f, 1d).setEaseType(EaseType.EASE_IN_OUT).setTransitionType(TransitionType.QUAD);
        selfDestructionTween.tweenInterval(1);
        selfDestructionTween.tweenRunnable(() -> this.arrowHot = false);
        selfDestructionTween.tweenMethod(this::setWarningVisibiity, 1f, 0f, 0.2d);
        selfDestructionTween.parallel().tweenMethod(this::setArrowTemperature, 1f, 0f, 0.6d).setEaseType(EaseType.EASE_OUT).setTransitionType(TransitionType.EXPO);
        selfDestructionTween.start();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());
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
        RenderSystem.setShaderColor(
                gameColor.r()+arrowTemperature*4,
                (float) (gameColor.g()+TransitionType.EXPO.apply(EaseType.EASE_OUT, arrowTemperature)/4f),
                gameColor.b(),
                gameColor.a()
        );
        guiGraphics.blit(ARROW, -8, -6, 16, 42, 0, 0, 16, 42, 16, 42);
        RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());

        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.blit(CENTER, -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(warningVisibiity, warningVisibiity, warningVisibiity, warningVisibiity);
        guiGraphics.blit(CENTER_WARNING, -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());
        guiGraphics.pose().popPose();
        arrowRot = (float) normalizeAngle((arrowRot + OctoLibClient.getDeltaTime() * this.arrowSpeed));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !playing) {
            return false;
        }

        Vector2d vector2d = new Vector2d(mouseX, mouseY);
        Vector2d center = new Vector2d(this.getX() + this.width / 2f, this.getY() + this.height / 2f);
        double distance = vector2d.distance(center);

        if (distance > this.width / 2f - 8) {
            return false;
        }

        if (distance <= this.width / 2f - 22) {
            if (this.isArrowHot()) {
                this.screen.getGame().hurt();
                this.deactivateAllPins();
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.FIRE_EXTINGUISH, 1.25f));
                return false;
            }

            boolean result = this.unlockPin(arrowRot);
            if (result) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOOD_HIT, 1f));
            } else {
                this.screen.getGame().hurt();
            }

            this.screen.getGame().processOnClickRules(result);

            return result;
        }

        return true;
    }

    public boolean unlockPin(float arrowAngle) {
        double angle = normalizeAngle(arrowAngle - this.rot);
        Optional<RotatingParent<LockPinWidget, GearMechanismWidget>> optional = this.children.stream()
                .filter(rotatingParent -> roughlyEquals(normalizeAngle(rotatingParent.rot), angle, 8))
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

    private static boolean roughlyEquals(double first, double second, double accuracy) {
        return first >= second - accuracy && first < second + accuracy;
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
        arrowTween.tweenMethod(this::setArrowSpeed, this.arrowSpeed, this.maxArrowSpeed * -Mth.sign(this.arrowSpeed), 0.5f);
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
}
