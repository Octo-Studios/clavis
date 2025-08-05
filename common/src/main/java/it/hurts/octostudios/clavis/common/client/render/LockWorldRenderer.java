package it.hurts.octostudios.clavis.common.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.client.model.LockModel;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.Lock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class LockWorldRenderer {
    public static RenderType getOutline() {
        return RenderType.create("clavis_outline",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536, false, true,
                RenderType.CompositeState.builder()
                        .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                        .setOutputState(RenderStateShard.MAIN_TARGET)
                        //.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
                        .setColorLogicState(RenderStateShard.NO_COLOR_LOGIC)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .setCullState(RenderStateShard.CULL)
                        .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                        .setTextureState(new RenderStateShard.TextureStateShard(LockModel.TEXTURE,false,false))
                        .createCompositeState(false)
        );
    }

    public static final Set<Lock> FOR_RENDERING = new HashSet<>();
    public static final LockModel LOCK = new LockModel(false);
    public static final LockModel GLOW = new LockModel(true);

    public static void render(Camera camera, Matrix4f modelViewMatrix, PoseStack poseStack, DeltaTracker partialTick, MultiBufferSource multiBufferSource, ClientLevel level) {
        Vec3 cp = camera.getPosition();
        for (Lock lock : FOR_RENDERING) {
            Box box = lock.getBox();
            Vec3 minPos = new Vec3(box.minX, box.minY, box.minZ).subtract(cp);
            Vec3 maxPos = new Vec3(box.maxX+1, box.maxY+1, box.maxZ+1).subtract(cp);

            AABB aabb = new AABB(minPos, maxPos);
            Vec3 center = new Vec3(Mth.lerp(0.5, minPos.x, maxPos.x), maxPos.y, Mth.lerp(0.5, minPos.z, maxPos.z));
            poseStack.pushPose();
            poseStack.translate(center.x, center.y+0.66f, center.z);

            BlockPos pos = new BlockPos(Mth.floor(center.x+cp.x), Mth.floor(center.y+0.66f+cp.y), Mth.floor(center.z+cp.z));

            int color = lock.getDifficulty() < 0.33f ? 0xff33ff22 : lock.getDifficulty() < 0.66f ? 0xffffcc00 : 0xffff0011;
            int sky = level.getBrightness(LightLayer.SKY, pos);
            int block = level.getBrightness(LightLayer.BLOCK, pos);
            float hash = (float) (new Random(lock.getSeed()).nextFloat()*Math.PI);
            float ticks = (level.getGameTime() + partialTick.getGameTimeDeltaPartialTick(false))/10f + hash;


            poseStack.translate(0, Math.sin(ticks)*0.1f, 0);
            poseStack.mulPose(Axis.YP.rotation((float) (ticks/2f % (Math.PI*2f))));

            LOCK.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(LockModel.TEXTURE)), LightTexture.pack(block, sky), OverlayTexture.NO_OVERLAY);
            GLOW.renderToBuffer(poseStack, multiBufferSource.getBuffer(getOutline()), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);

            poseStack.popPose();
        }
    }

    public static void clear() {
        FOR_RENDERING.clear();
    }
}
