package it.hurts.octostudios.clavis.common.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.Lock;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Set;

public class LockWorldRenderer {
    public static final Set<Lock> FOR_RENDERING = new HashSet<>();

    public static void render(Camera camera, Matrix4f modelViewMatrix, PoseStack poseStack, DeltaTracker partialTick, MultiBufferSource multiBufferSource, ClientLevel level) {
        Vec3 cp = camera.getPosition();
        for (Lock lock : FOR_RENDERING) {
            Box box = lock.getBox();
            Vec3 minPos = new Vec3(box.minX, box.minY, box.minZ).subtract(cp);
            Vec3 maxPos = new Vec3(box.maxX+1, box.maxY+1, box.maxZ+1).subtract(cp);

            AABB aabb = new AABB(minPos, maxPos);
            Vec3 center = new Vec3(Mth.lerp(0.5, minPos.x, maxPos.x), maxPos.y, Mth.lerp(0.5, minPos.z, maxPos.z));
            //level.addParticle(ParticleTypes.END_ROD, center.x+cp.x, center.y+cp.y, center.z+cp.z, 0, 0.1, 0);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            poseStack.pushPose();
            poseStack.translate(center.x, center.y, center.z);
            itemRenderer.renderStatic(Items.DIAMOND.getDefaultInstance(), ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, poseStack,
                    multiBufferSource, level, 0);
            poseStack.popPose();
        }
    }
}
