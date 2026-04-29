package it.hurts.shatterbyte.clavis.common.client.screen.widget;

import com.mojang.math.Axis;
import it.hurts.octostudios.octolib.client.screen.widget.Child;
import it.hurts.octostudios.octolib.client.screen.widget.HasRenderMatrix;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RotatingParent<T extends Child<RotatingParent>, C extends LayoutElement> extends AbstractWidget implements HasRenderMatrix, ContainerEventHandler, Child<C> {
    C parent;
    Matrix4f matrix;
    public List<T> children = new ArrayList<>();
    GuiEventListener focused;
    boolean dragging;
    @Getter
    @Setter
    float rot;
    float previousRot;

    @SafeVarargs
    public RotatingParent(int x, int y, float degrees, T... children) {
        super(x, y, 0, 0, Component.empty());
        this.previousRot = degrees;
        this.setRot(degrees);
        this.children.addAll(Arrays.stream(children).peek(child -> child.setParent(this)).toList());
    }

    public void capturePreviousRot() {
        this.previousRot = this.rot;
    }

    public float getVisualRot(float partialTick) {
        return Mth.lerp(partialTick, this.previousRot, this.rot);
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
        return ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(this.getVisualRot(partialTick)));
        guiGraphics.pose().translate(-this.getX(), -this.getY(), 0);
        this.setMatrix(new Matrix4f(guiGraphics.pose().last().pose()));
        children.forEach(child -> {
            if (child instanceof Renderable renderable) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        });
        guiGraphics.pose().popPose();
    }

    @Override
    public @Nullable C getParent() {
        return this.parent;
    }

    @Override
    public void setParent(@Nullable C c) {
        this.parent = c;
    }
}
