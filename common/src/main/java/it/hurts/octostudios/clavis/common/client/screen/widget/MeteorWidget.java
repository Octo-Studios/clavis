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

public class MeteorWidget extends AbstractWidget implements Child<MirrorWidget> {
    int size = 1;
    MirrorWidget parent;

    public MeteorWidget(int x, int y, MirrorWidget parent) {
        super(x, y, 19, 19, Component.empty());
        this.setParent(parent);
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
    public @Nullable MirrorWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable MirrorWidget parent) {
        this.parent = parent;
    }
}
