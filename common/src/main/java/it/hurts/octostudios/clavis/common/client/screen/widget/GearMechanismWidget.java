package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.clavis.common.Clavis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GearMechanismWidget extends AbstractWidget implements ContainerEventHandler {
    public static ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/background.png");

    List<GuiEventListener> children = new ArrayList<>();
    GuiEventListener focused;
    boolean dragging;

    public GearMechanismWidget(int x, int y) {
        super(x, y, 192, 192, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, 0xffffffff);
        guiGraphics.blit(BACKGROUND, (int) (this.width/2f - 72), (int) (this.height/2f - 72), 0, 0, 144, 144);

        children.forEach(child -> {
            if (child instanceof Renderable renderable) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        });
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
}
