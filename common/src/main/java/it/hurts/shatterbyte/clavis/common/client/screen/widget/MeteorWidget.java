package it.hurts.shatterbyte.clavis.common.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.particle.UIParticle;
import it.hurts.octostudios.octolib.client.screen.widget.Child;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.particle.MeteorPartUIParticle;
import it.hurts.shatterbyte.clavis.common.registry.SoundEventRegistry;
import lombok.Getter;
import lombok.Setter;
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
import org.joml.Vector2f;
import org.joml.Vector2i;

public class MeteorWidget extends AbstractWidget implements Child<MirrorWidget>, Tickable {
    public ResourceLocation METEOR;
    public ResourceLocation METEOR_CRACKED;

    Tween scaleTween = Tween.create();
    Tween hotTween = Tween.create();
    @Setter
    Vector2f visualSize = new Vector2f(1, 1);

    @Getter
    boolean cracked = false;

    @Setter
    float heatProgress;

    float oldRot;
    float rot;
    float rotSpeed;

    float conservedMomentum = 0.975f;

    int size = 0;
    MirrorWidget parent;

    public Vector2d oldPos;
    public Vector2d precisePosition;

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

        RenderSystem.setShaderColor(1f + heatProgress/8f, 1f - heatProgress*0.75f, 1f - heatProgress, 1f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(parentPos.x, parentPos.y, 0);
        guiGraphics.pose().translate(
                Mth.lerp(partial, oldPos.x, precisePosition.x),
                Mth.lerp(partial, oldPos.y, precisePosition.y),
                0
        );
        guiGraphics.pose().translate(width/2f, height/2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotation(Mth.lerp(partial, oldRot, rot)));
        guiGraphics.pose().scale(visualSize.x, visualSize.y, 1);
        guiGraphics.blit(cracked ? METEOR_CRACKED : METEOR, -10, -10, 19, 19, 0, 0, 19, 19, 19, 19);
        guiGraphics.pose().translate(-width/2f, -height/2f, 0);
        //guiGraphics.fill(0, 0, width, height, 0xffff0000);
        guiGraphics.pose().popPose();

        RenderSystem.setShaderColor(parent.gameColor.r(), parent.gameColor.g(), parent.gameColor.b(), parent.gameColor.a());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.GENERIC_EXPLODE.value(), 1.33f, 0.25f));
        handler.play(SimpleSoundInstance.forUI(SoundEventRegistry.METEOR_SMASH.get(), 0.66f, 1.25f));
    }

    public boolean isHot() {
        return heatProgress >= 0.6f;
    }

    public void makeItHot() {
        heatProgress = 0f;

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_ACTIVATE, 1.25f));
        hotTween.kill();
        hotTween = Tween.create();
        hotTween.tweenInterval(0.4);
        hotTween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.FIRE_AMBIENT, 0.85f)));
        hotTween.tweenMethod(this::setHeatProgress, 0f, 1f, 1.25d).setEaseType(EaseType.EASE_IN_OUT).setTransitionType(TransitionType.QUAD);
        hotTween.tweenInterval(1.5);
        hotTween.tweenMethod(this::setHeatProgress, 1f, 0f, 0.8d).setEaseType(EaseType.EASE_OUT).setTransitionType(TransitionType.EXPO);
        hotTween.start();
    }

    public void regenerate() {
        if (!this.isCracked()) {
            return;
        }

        scaleTween.kill();
        scaleTween = Tween.create();
        scaleTween.setTransitionType(TransitionType.QUART);
        scaleTween.setEase(EaseType.EASE_IN);
        scaleTween.tweenMethod(this::setVisualSize, new Vector2f(0.95f, 0.95f), new Vector2f(1.25f, 1.25f), 0.58f);
        scaleTween.tweenRunnable(() -> {
            this.cracked = false;
            Vector2d center = this.getCenterPos(true);

            for (int i = 0; i < 4 + size; i++) {
                MeteorPartUIParticle particle = new MeteorPartUIParticle(
                        MeteorPartUIParticle.getRandomPart(parent.random),
                        16,
                        (float) center.x,
                        (float) center.y,
                        this.width*1.75f,
                        UIParticle.Layer.SCREEN,
                        1
                );
                particle.setSpeed(0.75f);
                particle.getDirection().mul(-1);
                particle.setScreen(parent.getScreen());
                particle.instantiate();
            }
        });
        scaleTween.tweenMethod(this::setVisualSize, new Vector2f(1.25f, 1.25f), new Vector2f(1f, 1f), 0.25f).setEaseType(EaseType.EASE_OUT);
        scaleTween.start();
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
        if (this.isHot()) {
            this.parent.minigame.hurt();
            this.getParent().getMinigame().processOnClickRules(false);
            return;
        }

        this.cracked = true;
        this.hotTween.kill();
        this.heatProgress = 0f;

        if (this.getParent().children.stream().filter(meteor -> !(meteor instanceof FakeMeteorWidget)).allMatch(MeteorWidget::isCracked)) {
            this.getParent().playWinAnimation();
            return;
        }

        this.playCrackAnimation();
        this.getParent().getMinigame().processOnClickRules(true);
    }

    public void playCrackAnimation() {
        scaleTween.kill();
        scaleTween = Tween.create();
        scaleTween.setTransitionType(TransitionType.QUART);
        scaleTween.setEase(EaseType.EASE_OUT);
        scaleTween.tweenMethod(this::setVisualSize, new Vector2f(1.25f, 1.25f), new Vector2f(1f, 1f), 1f);
        scaleTween.start();

        Vector2d center = this.getCenterPos(true);

        for (int i = 0; i < 4 + size; i++) {
            MeteorPartUIParticle particle = new MeteorPartUIParticle(
                    MeteorPartUIParticle.getRandomPart(parent.random),
                    30,
                    (float) center.x,
                    (float) center.y,
                    this.width,
                    UIParticle.Layer.SCREEN,
                    1
            );
            particle.setScreen(parent.getScreen());
            particle.instantiate();
        }
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

        float radius = this.getParent().getWidth()/2f-6;

        if (dist + objRadius > radius) {
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
            velocity.normalize(prevLength*0.925f);

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

        for (MeteorWidget child : this.getParent().children()) {
            if (child == this) {
                continue;
            }

            Vector2d thisPos = this.getCenterPos(false);
            Vector2d otherPos = child.getCenterPos(false);

            if (thisPos.distance(otherPos) < this.width*0.9f) {
                this.velocity.add(thisPos.sub(otherPos).normalize(0.75f));
                child.velocity.sub(thisPos);
            }
        }

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
