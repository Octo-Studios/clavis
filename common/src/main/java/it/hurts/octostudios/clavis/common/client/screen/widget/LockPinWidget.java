package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.screen.widget.Child;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class LockPinWidget extends AbstractWidget implements Child<RotatingParent> {
    public static final ResourceLocation ACTIVE = ResourceLocation.fromNamespaceAndPath(Clavis.MOD_ID, "textures/minigame/pin_active.png");
    public static final ResourceLocation INACTIVE = ResourceLocation.fromNamespaceAndPath(Clavis.MOD_ID, "textures/minigame/pin_inactive.png");

    RotatingParent parent;
    public boolean active;
    @Setter
    private float yScale = 1f;

    Tween tween = Tween.create();

    public LockPinWidget(int x, int y) {
        super(x, y, 12, 36, Component.empty());
    }

    public static RotatingParent<LockPinWidget, GearMechanismWidget> create(int x, int y, float degrees, GearMechanismWidget parent) {
        RotatingParent<LockPinWidget, GearMechanismWidget> rotatingParent = new RotatingParent<>(x, y, degrees, new LockPinWidget(-6, -36+74));
        rotatingParent.setParent(parent);
        return rotatingParent;
    }

    public static RotatingParent<LockPinWidget, GearMechanismWidget> createFake(int x, int y, GearMechanismWidget parent) {
        RotatingParent<LockPinWidget, GearMechanismWidget> rotatingParent = new RotatingParent<>(x, y, 0, new FakePinWidget(-6, -36+74));
        rotatingParent.setParent(parent);
        return rotatingParent;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY()+this.height, 0);
        guiGraphics.pose().scale(1f, yScale, 1f);

        guiGraphics.blit(active ? getActiveTexture() : getInactiveTexture(), 0, -this.height, 12, 36, 0, 0, 12, 36, 12, 36);

        guiGraphics.pose().popPose();
    }

    public ResourceLocation getActiveTexture() {
        return ACTIVE;
    }

    public ResourceLocation getInactiveTexture() {
        return INACTIVE;
    }

    @Override
    public @Nullable RotatingParent getParent() {
        return this.parent;
    }

    @Override
    public void setParent(@Nullable RotatingParent parent) {
        this.parent = parent;
    }

    public boolean activate() {
        if (this.active) {
            return false;
        }

        this.active = true;
        this.animateActivation();
        return true;
    }

    public void deactivate() {
        if (!this.active) {
            return;
        }

        this.active = false;
        this.animateDectivation();
    }

    private void animateActivation() {
        tween.kill();
        tween = Tween.create();
        tween.tweenMethod(this::setYScale, 1.384f, 1f, 0.5f).setTransitionType(TransitionType.ELASTIC).setEaseType(EaseType.EASE_OUT);
        tween.start();
    }

    private void animateDectivation() {
        tween.kill();
        tween = Tween.create();
        tween.tweenMethod(this::setYScale, 0.722f, 1f, 0.5f).setTransitionType(TransitionType.ELASTIC).setEaseType(EaseType.EASE_OUT);
        tween.start();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active) {
            return false;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        //this.activate();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
