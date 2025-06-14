package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.google.common.collect.Lists;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.ClavisClient;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.screen.widget.HasRenderMatrix;
import it.hurts.octostudios.octolib.util.VectorUtils;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    Matrix4f matrix;
    List<GuiEventListener> children = new ArrayList<>();
    GuiEventListener focused;
    boolean dragging;
    @Setter
    float rot;

    float arrowRot;

    public GearMechanismWidget(int x, int y) {
        super(x, y, 192, 192, Component.empty());
        Random random = new Random();
        Vector2f position = new Vector2f(0, this.height/2f-22);
        int toGenerate = 4;

        List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(list);
        list.stream().limit(toGenerate).forEach(integer -> {
            float newI = integer * 36 + random.nextFloat(-5, 5);

            Vector2f rotated = VectorUtils.rotate(position, newI);
            this.children.add(LockPinWidget.create(Math.round(this.width / 2f + rotated.x), Math.round(this.height / 2f + rotated.y), newI, this));

        });
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
        guiGraphics.blit(COGWHEEL, this.getX(), this.getY(), 192, 192, 0, 0, 192, 192, 192, 192);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width/2f, this.getY() + this.height/2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(arrowRot));
        guiGraphics.blit(ARROW, -8, -6, 16, 42, 0, 0, 16, 42, 16, 42);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width/2f, this.getY() + this.height/2f, 0);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Clavis.MODID, "textures/minigame/center.png"), -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
        guiGraphics.pose().popPose();
        arrowRot = (float) ((arrowRot - ClavisClient.getDeltaTime() * 200) % 360);
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
        if (ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button)) {
            return false;
        }

        Vector2d vector2d = new Vector2d(mouseX, mouseY);
        Vector2d center = new Vector2d(this.getX() + this.width/2f, this.getY() + this.height/2f);
        double distance = vector2d.distance(center);

        if (distance > this.width/2f - 8 || distance < this.width/2f - 22) {
            return false;
        }

        if (super.mouseClicked(mouseX,mouseY, button)) {
            Tween tween = Tween.create();
            tween.tweenMethod(this::setRot, this.rot, this.rot + 100f, 0.75f).setEaseType(EaseType.EASE_OUT).setTransitionType(TransitionType.QUART);
            return true;
        }

        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {

    }
}
