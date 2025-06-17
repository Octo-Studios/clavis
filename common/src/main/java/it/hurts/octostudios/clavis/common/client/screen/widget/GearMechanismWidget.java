package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.ClavisClient;
import it.hurts.octostudios.clavis.common.tier.OverworldTier;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.screen.widget.HasRenderMatrix;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.octostudios.octolib.util.VectorUtils;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GearMechanismWidget extends AbstractWidget implements ContainerEventHandler, HasRenderMatrix {
    public static ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/background.png");
    public static ResourceLocation COGWHEEL = ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/gear.png");
    public static ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/arrow.png");

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
    @Setter
    float arrowSpeed;

    public GearMechanismWidget(OverworldTier tier) {
        super(0, 0, 192, 192, Component.empty());
        Random random = new Random();
        int pins = tier.getPinCount(random);
        this.arrowSpeed = tier.getArrowRotationSpeed(random);

        int maxSpots = (int) Math.round(pins * 1.5);

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
        guiGraphics.pose().pushPose();
        int offset = (int) ((192 - 146) / 2f);
        guiGraphics.blit(BACKGROUND, this.getX() + offset, this.getY() + offset, 146, 146, 0, 0, 146, 146, 146, 146);
        guiGraphics.pose().translate(this.getX() + this.width/2f, this.getY() + this.height/2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rot));
        guiGraphics.pose().translate(-this.width/2f - this.getX(), -this.height/2f - this.getY(), 0);
        this.setMatrix(new Matrix4f(guiGraphics.pose().last().pose()));
        children.reversed().forEach(child -> {
            if (child instanceof Renderable renderable) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        });
        RenderSystem.setShaderColor(gearColor.r, gearColor.g, gearColor.b, gearColor.a);
        guiGraphics.blit(COGWHEEL, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor(1f,1f,1f,1f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width/2f, this.getY() + this.height/2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(arrowRot));
        RenderSystem.setShaderColor(arrowColor.r, arrowColor.g, arrowColor.b, arrowColor.a);
        guiGraphics.blit(ARROW, -8, -6, 16, 42, 0, 0, 16, 42, 16, 42);
        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor(1f,1f,1f,1f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width/2f, this.getY() + this.height/2f, 0);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/center.png"), -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
        guiGraphics.pose().popPose();
        arrowRot = (float) normalizeAngle((arrowRot + ClavisClient.getDeltaTime() * this.arrowSpeed));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
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
        Vector2d center = new Vector2d(this.getX() + this.width/2f, this.getY() + this.height/2f);
        double distance = vector2d.distance(center);

        if (distance > this.width/2f - 8) {
            return false;
        }

        if (distance <= this.width/2f - 22) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOOD_HIT, 1f));
            return this.unlockPin(arrowRot);
        }

        this.flipArrowDirection();
        this.rotateGear(90f * -Mth.sign(this.arrowSpeed));

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

        optional.get().children.getFirst().activate();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.5f));
        return true;
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

    private void flipArrowDirection() {
        if (arrowTween != null) {
            arrowTween.kill();
        }

        arrowTween = Tween.create();
        arrowTween.tweenMethod(this::setArrowColor, OctoColor.WHITE.multiply(1.5f), OctoColor.WHITE, 0.5f);
        arrowTween.tweenMethod(this::setArrowSpeed, this.arrowSpeed, -this.arrowSpeed, 2f);
    }

    private void rotateGear(float degrees) {
        if (gearTween != null) {
            gearTween.kill();
        }

        gearTween = Tween.create();
        gearTween.tweenMethod(this::setGearColor, OctoColor.WHITE.multiply(1.5f), OctoColor.WHITE, 0.5f);
        gearTween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.GRINDSTONE_USE, 1.25f)));
        gearTween.tweenMethod(this::setRot, this.rot, this.rot + degrees, 2f)
                .setEaseType(EaseType.EASE_IN_OUT)
                .setTransitionType(TransitionType.CUBIC);
    }

    Tween gearTween;
    Tween arrowTween;
}
