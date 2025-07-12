package it.hurts.octostudios.clavis.common.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.clavis.common.client.model.LockModel;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.Lock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
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
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            poseStack.pushPose();
            poseStack.translate(center.x, center.y+0.66f, center.z);
            //itemRenderer.renderStatic(Items.DIAMOND.getDefaultInstance(), ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, level, 0);

            LockModel model = new LockModel(false);
            LockModel glow = new LockModel(true);

            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(LockModel.TEXTURE)), LightTexture.pack(0, 15), OverlayTexture.NO_OVERLAY);
            glow.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entityCutout(LockModel.TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }
    }

    public static void clear() {
        FOR_RENDERING.clear();
    }
}
