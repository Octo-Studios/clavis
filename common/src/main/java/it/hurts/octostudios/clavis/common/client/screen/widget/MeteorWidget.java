package it.hurts.octostudios.clavis.common.client.screen.widget;

import it.hurts.octostudios.octolib.client.screen.widget.Child;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2i;

public class MeteorWidget extends AbstractWidget implements Child<MirrorWidget>, Tickable {
    int size = 1;
    MirrorWidget parent;

    double gravityAccel;

    Vector2d oldPos;
    Vector2d precisePosition;

    @Getter
    Vector2d velocity = new Vector2d();

    public MeteorWidget(int x, int y, MirrorWidget parent) {
        super(x, y, 19, 19, Component.empty());
        this.setParent(parent);
        this.precisePosition = new Vector2d(x, y);
        this.oldPos = new Vector2d(x, y);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Vector2i parentPos = this.getParentPosition();
        float partial = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(parentPos.x, parentPos.y, 0);
        guiGraphics.pose().translate(
                Mth.lerp(partial, oldPos.x, precisePosition.x),
                Mth.lerp(partial, oldPos.y, precisePosition.y),
                0
        );
        guiGraphics.fill(0, 0, width, height, 0xffff0000);
        guiGraphics.pose().popPose();
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

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.velocity.set(7, 4);
    }

    private void collide() {
        double objCenterX = this.precisePosition.x + this.getWidth() / 2.0f;
        double objCenterY = this.precisePosition.y + this.getHeight() / 2.0f;

        double dx = objCenterX - this.getParent().getWidth()/2f;
        double dy = objCenterY - this.getParent().getHeight()/2f;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);

        //float objRadius = Math.max(this.width, this.height)/2f;
        float objRadius = (float)Math.sqrt( (this.width/2.0f)*(this.height/2.0f) * 2 );

        float radius = this.getParent().getWidth()/2f-2;

        if (dist + objRadius > radius) {
            gravityAccel = 0;

            float overlap = (dist + objRadius) - radius;
            double normX = dx / dist;
            double normY = dy / dist;

            objCenterX -= normX * overlap + this.getWidth() / 2f;
            objCenterY -= normY * overlap + this.getHeight() / 2f;

            this.precisePosition.x = objCenterX;
            this.precisePosition.y = objCenterY;

            double prevLength = velocity.length();

            double dot = velocity.x * normX + velocity.y * normY;
            velocity.x -= 2 * dot * normX;
            velocity.y -= 2 * dot * normY;

            //normalize it back to what it was bc wtf
            velocity.normalize(prevLength*0.85f);
        }
    }

    @Override
    public void tick() {
        this.oldPos = new Vector2d(this.precisePosition);
        gravityAccel += 0.1d;
        this.velocity.y += gravityAccel;

        if (this.velocity.lengthSquared() > 1) {
            this.collide();

            this.precisePosition.x += this.velocity.x;
            this.precisePosition.y += this.velocity.y;
        }

        this.setX((int) this.precisePosition.x);
        this.setY((int) this.precisePosition.y);
    }
}
