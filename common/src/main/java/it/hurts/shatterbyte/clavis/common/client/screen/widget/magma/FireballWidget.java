package it.hurts.shatterbyte.clavis.common.client.screen.widget.magma;

import com.mojang.math.Axis;
import it.hurts.octostudios.octolib.client.screen.widget.Child;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.RotatingParent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class FireballWidget extends AbstractWidget implements Child<RotatingParent> {
    @Override
    public @Nullable RotatingParent getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable RotatingParent rotatingParent) {
        this.parent = rotatingParent;
    }

    RotatingParent parent;

    public float angle;
    public float radius;

    public FireballWidget(int x, int y) {
        super(x, y, 10, 10, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        double rad = Math.toRadians(angle);

//        float x = (float) (Math.cos(rad) * radius);
//        float y = (float) (Math.sin(rad) * radius);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(parent.getX(), parent.getY(), 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotation((float) rad));
        guiGraphics.pose().translate(0, -radius, 0);

        guiGraphics.blit(MagmaWheelWidget.FIREBALL, -5, -5, 10, 10, 0, 0, 10, 10, 10, 10);

        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
