package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.octolib.client.screen.widget.Child;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class MeteorWidget extends AbstractWidget implements Child<RotatingParent> {
    int size = 1;
    RotatingParent parent;

    public MeteorWidget(int x, int y) {
        super(x, y, 19, 19, Component.empty());
    }

    public static RotatingParent<MeteorWidget, MirrorWidget> create(int x, int y, float degrees, MirrorWidget parent) {
        RotatingParent<MeteorWidget, MirrorWidget> rotatingParent = new RotatingParent<>(x, y, degrees, new MeteorWidget(-10, -10));
        rotatingParent.setParent(parent);
        return rotatingParent;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX()+width, this.getY()+height, 0xffff0000);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.ANVIL_PLACE, 1f, 1f));
    }

    @Override
    public @Nullable RotatingParent getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable RotatingParent parent) {
        this.parent = parent;
    }
}
