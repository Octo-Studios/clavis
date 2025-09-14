package it.hurts.octostudios.clavis.common.client.screen.widget;

import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.Clavis;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2i;

public class MeteorWidget extends AbstractWidget implements Child<MirrorWidget>, Tickable {
    public ResourceLocation METEOR;
    public ResourceLocation METEOR_CRACKED;

    @Getter
    boolean cracked = false;

    float oldRot;
    float rot;
    float rotSpeed;

    float conservedMomentum = 0.975f;

    int size = 0;
    MirrorWidget parent;

    Vector2d oldPos;
    Vector2d precisePosition;

    @Getter
    Vector2d velocity = new Vector2d();

    public MeteorWidget(int x, int y, int size, MirrorWidget parent) {
        super(x, y, 11+size*4, 11+size*4, Component.empty());
        this.size = size;
        this.rotSpeed = parent.random.nextFloat(-0.05f, 0.05f);
        this.setParent(parent);
        this.precisePosition = new Vector2d(x, y);
        this.oldPos = new Vector2d(x, y);

        METEOR = Clavis.path("textures/minigame/mirror/meteor_"+size+".png");
        METEOR_CRACKED = Clavis.path("textures/minigame/mirror/meteor_cracked_"+size+".png");
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Vector2i parentPos = this.getParentPosition();
        float partial = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);

        //guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, 0xffff0000);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(parentPos.x, parentPos.y, 0);
        guiGraphics.pose().translate(
                Mth.lerp(partial, oldPos.x, precisePosition.x),
                Mth.lerp(partial, oldPos.y, precisePosition.y),
                0
        );
        guiGraphics.pose().translate(width/2f, height/2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotation(Mth.lerp(partial, oldRot, rot)));
        guiGraphics.blit(cracked ? METEOR_CRACKED : METEOR, -10, -10, 19, 19, 0, 0, 19, 19, 19, 19);
        guiGraphics.pose().translate(-width/2f, -height/2f, 0);
        //guiGraphics.fill(0, 0, width, height, 0xffff0000);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isCracked()) {
            return false;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.cracked = true;
        this.getParent().getMinigame().processOnClickRules(true);
    }

    public Vector2d getCenterPos(boolean global) {
        Vector2d localCenterPos = new Vector2d(precisePosition).add(this.width/2f, this.height/2f);
        if (!global) {
            return localCenterPos;
        }

        Vector2i parentPos = this.getParentPosition();
        return localCenterPos.add(parentPos.x, parentPos.y);
    }

    private void collide() {
        double objCenterX = this.precisePosition.x + this.getWidth() / 2.0f;
        double objCenterY = this.precisePosition.y + this.getHeight() / 2.0f;

        double dx = objCenterX - this.getParent().getWidth()/2f;
        double dy = objCenterY - this.getParent().getHeight()/2f;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);

        float objRadius = Math.max(this.width, this.height)/2f;
        objRadius += (float)Math.sqrt( (this.width/2.0f)*(this.height/2.0f) * 2 );
        objRadius /= 2f;

        float radius = this.getParent().getWidth()/2f-2;

        if (dist + objRadius > radius) {
            float overlap = (dist + objRadius) - radius + 1f;
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
            velocity.normalize(prevLength*0.875f);

            double tangentX = velocity.x - normX * (velocity.x * normX + velocity.y * normY);
            double tangentY = velocity.y - normY * (velocity.x * normX + velocity.y * normY);

            double tangentialSpeed = Math.sqrt(tangentX * tangentX + tangentY * tangentY);

            // flip spin direction and add tangential influence
            this.rotSpeed = -this.rotSpeed;
            this.rotSpeed += (float)(tangentialSpeed * 0.01);

            // clamp spin so it doesn't get ridiculous
            this.rotSpeed = Mth.clamp(this.rotSpeed, -0.1f, 0.1f);
        }
    }

    @Override
    public void tick() {
        this.oldPos = new Vector2d(this.precisePosition);

        this.oldRot = rot;
        this.rot += rotSpeed;

        if (this.velocity.lengthSquared() > 0.005) {
            this.precisePosition.x += this.velocity.x;
            this.precisePosition.y += this.velocity.y;

            this.collide();
        } else if (this.velocity.lengthSquared() != 0) {
            this.velocity.mul(0);
        }

        this.setX((int) this.precisePosition.x);
        this.setY((int) this.precisePosition.y);

        this.velocity.mul(conservedMomentum);
    }
}
