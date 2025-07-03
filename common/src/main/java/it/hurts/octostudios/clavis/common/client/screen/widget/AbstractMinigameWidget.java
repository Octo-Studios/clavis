package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.minigame.Minigame;
import it.hurts.octostudios.octolib.client.screen.widget.HasRenderMatrix;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractMinigameWidget<T extends GuiEventListener> extends AbstractWidget implements ContainerEventHandler, HasRenderMatrix, Tickable {
    @Getter Random random = new Random();
    @Getter LockpickingScreen screen;

    Matrix4f matrix;
    List<T> children = new ArrayList<>();
    GuiEventListener focused;
    boolean dragging;

    protected AbstractMinigameWidget(int x, int y, int width, int height, LockpickingScreen screen) {
        super(x, y, width, height, Component.empty());
        this.screen = screen;
    }

    public abstract void playHurtAnimation();
    public abstract void playWinAnimation();
    public abstract void playLoseAnimation();

    public abstract void processDifficulty(Minigame<? extends AbstractMinigameWidget<?>> game);

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.setMatrix(new Matrix4f(guiGraphics.pose().last().pose()));
        children.reversed().forEach(child -> {
            if (child instanceof Renderable renderable) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        });
    }

    @Override
    public @NotNull List<T> children() {
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
}
