package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.TestLockpickingScreen;
import it.hurts.octostudios.clavis.common.minigame.rule.OverworldRules;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import it.hurts.octostudios.clavis.common.minigame.tier.OverworldTier;
import it.hurts.octostudios.octolib.OctoLibClient;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.screen.widget.HasRenderMatrix;
import it.hurts.octostudios.octolib.util.OctoColor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2d;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GearMechanismWidget extends AbstractWidget implements ContainerEventHandler, HasRenderMatrix {
    public static ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/background.png");
    public static ResourceLocation COGWHEEL = ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/gear.png");
    public static ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/arrow.png");

    public Random random = new Random();

    @Setter
    OctoColor gameColor = OctoColor.WHITE;
    @Setter
    OctoColor gearColor = OctoColor.WHITE;
    @Setter
    OctoColor arrowColor = OctoColor.WHITE;
    Matrix4f matrix;
    List<RotatingParent<LockPinWidget, GearMechanismWidget>> children = new ArrayList<>();
    GuiEventListener focused;
    boolean dragging;
    float rot;
    float arrowRot;
    @Setter @Getter
    float arrowSpeed;

    float maxArrowSpeed;
    float maxPins;

    public TestLockpickingScreen screen;

    public GearMechanismWidget() {
        super(0, 0, 192, 192, Component.empty());
    }

    public void processDifficulty(float difficulty) {
        int pins = Mth.ceil(random.nextInt(6, 11)*difficulty);
        this.maxPins = pins;
        int maxSpots = (int) Math.round(pins * 1.5);

        this.arrowSpeed = random.nextFloat(240, 300)*difficulty;
        this.maxArrowSpeed = Math.abs(arrowSpeed);

        List<Integer> list = IntStream.range(0, maxSpots).boxed().collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(list);
        list.stream().limit(pins).forEach(i -> {
            float newI = i * (360f / maxSpots) + random.nextFloat(-5, 5);
            this.children.add(LockPinWidget.create(Math.round(this.width / 2f), Math.round(this.height / 2f), newI, this));
        });
    }

    public void setRot(float rot) {
        this.rot = (float) normalizeAngle(rot);
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
        this.setMatrix(new Matrix4f(guiGraphics.pose().last().pose()));
        children.reversed().forEach(child -> {
            if (child instanceof Renderable renderable) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        });
        RenderSystem.setShaderColor(gearColor.r(), gearColor.g(), gearColor.b(), gearColor.a());
        guiGraphics.blit(COGWHEEL, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());


        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(arrowRot));
        RenderSystem.setShaderColor(arrowColor.r(), arrowColor.g(), arrowColor.b(), arrowColor.a());
        guiGraphics.blit(ARROW, -8, -6, 16, 42, 0, 0, 16, 42, 16, 42);
        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor(gameColor.r(), gameColor.g(), gameColor.b(), gameColor.a());

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 0);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/center.png"), -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
        guiGraphics.pose().popPose();
        arrowRot = (float) normalizeAngle((arrowRot + OctoLibClient.getDeltaTime() * this.arrowSpeed));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public @NotNull List<RotatingParent<LockPinWidget, GearMechanismWidget>> children() {
        return this.children;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        this.dragging = isDragging;
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.focused = focused;
    }

    @Override
    public Matrix4f getMatrix() {
        return this.matrix;
    }

    @Override
    public void setMatrix(Matrix4f matrix) {
        this.matrix = matrix;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        Vector2d vector2d = new Vector2d(mouseX, mouseY);
        Vector2d center = new Vector2d(this.getX() + this.width / 2f, this.getY() + this.height / 2f);
        double distance = vector2d.distance(center);

        if (distance > this.width / 2f - 8) {
            return false;
        }

        if (distance <= this.width / 2f - 22) {
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
        arrowTween.kill();
        arrowTween = Tween.create();
        arrowTween.tweenMethod(this::setArrowSpeed, this.arrowSpeed, this.maxArrowSpeed * -Mth.sign(this.arrowSpeed), 0.5f);
        arrowTween.start();
    }

    public void rotateGear(float degrees) {
        gearTween.kill();
        gearTween = Tween.create();
        gearTween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.GRINDSTONE_USE, 0.3f))).setDelay(0.5f);
        gearTween.parallel().tweenMethod(this::setRot, this.rot, this.rot + degrees, 4f)
                .setEaseType(EaseType.EASE_IN_OUT)
                .setTransitionType(TransitionType.QUART);
        gearTween.start();
    }

    Tween gearTween = Tween.create();
    Tween arrowTween = Tween.create();
    Tween mainTween = Tween.create();
}
