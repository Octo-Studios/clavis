package it.hurts.octostudios.clavis.common.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import it.hurts.octostudios.clavis.common.client.model.LockModel;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.Lock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
                1536, true, false,
                RenderType.CompositeState.builder()
                        .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                        .setOutputState(RenderStateShard.MAIN_TARGET)
                        //.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
                        .setColorLogicState(RenderStateShard.NO_COLOR_LOGIC)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .setCullState(RenderStateShard.CULL)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                        .setTextureState(new RenderStateShard.TextureStateShard(LockModel.TEXTURE,false,false))
                        .createCompositeState(false)
        );
    }

    private static final ResourceLocation CHAIN_TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/chain.png");
    public static final Set<Lock> FOR_RENDERING = new HashSet<>();
    public static final LockModel LOCK = new LockModel(false);
    public static final LockModel GLOW = new LockModel(true);

    public static void render(Camera camera, Matrix4f modelViewMatrix, PoseStack poseStack, DeltaTracker partialTick, MultiBufferSource multiBufferSource, ClientLevel level) {
        Vec3 cp = camera.getPosition();
        for (Lock lock : FOR_RENDERING) {
            Box box = lock.getBox();
            Vec3 minPos = new Vec3(box.minX, box.minY, box.minZ);
            Vec3 maxPos = new Vec3(box.maxX+1, box.maxY+1, box.maxZ+1);

            AABB aabb = new AABB(minPos, maxPos);
            Vec3 center = new Vec3(Mth.lerp(0.5, minPos.x, maxPos.x), maxPos.y, Mth.lerp(0.5, minPos.z, maxPos.z));
            poseStack.pushPose();
            poseStack.translate(-cp.x, -cp.y, -cp.z);
            poseStack.translate(center.x, center.y+0.66f, center.z);

            BlockPos pos = new BlockPos(Mth.floor(center.x), Mth.floor(center.y+0.66f), Mth.floor(center.z));

            int color = lock.getDifficulty() < 0.33f ? 0xff33ff22 : lock.getDifficulty() < 0.66f ? 0xffffcc00 : 0xffff0011;
            int sky = level.getBrightness(LightLayer.SKY, pos);
            int block = level.getBrightness(LightLayer.BLOCK, pos);
            float hash = (float) (new Random(lock.getSeed()).nextFloat()*Math.PI);
            float ticks = (level.getGameTime() + partialTick.getGameTimeDeltaPartialTick(true))/10f + hash;

            poseStack.translate(0, Math.sin(ticks)*0.1f, 0);
            poseStack.mulPose(Axis.YP.rotation((float) (ticks/2f % (Math.PI*2f))));

            LOCK.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(LockModel.TEXTURE)), LightTexture.pack(block, sky), OverlayTexture.NO_OVERLAY);
            GLOW.renderToBuffer(poseStack, multiBufferSource.getBuffer(getOutline()), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);

            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(-cp.x, -cp.y, -cp.z);

            BufferBuilder builder = new BufferBuilder(new ByteBufferBuilder(1536), VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, CHAIN_TEXTURE);
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();

            float f = 1/16f;
            float d = 0.13258f/2f;
            float xLength = (float) (maxPos.x-minPos.x)+f*2f;
            float yLength = (float) (maxPos.y-minPos.y)+f*2f;
            float zLength = (float) (maxPos.z-minPos.z)+f*2f;


            poseStack.pushPose();
            poseStack.translate(center.x, minPos.y-f, minPos.z-d);
            renderChain(builder, poseStack, yLength);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(maxPos.x+d, minPos.y-f, center.z);
            renderChain(builder, poseStack, yLength);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(minPos.x-d, minPos.y-f, center.z);
            renderChain(builder, poseStack, yLength);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(center.x, minPos.y-f, maxPos.z+d);
            renderChain(builder, poseStack, yLength);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(center.x, maxPos.y+d, minPos.z-f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            renderChain(builder, poseStack, zLength);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(minPos.x-f, maxPos.y+d, center.z);
            poseStack.mulPose(Axis.ZN.rotationDegrees(90f));
            renderChain(builder, poseStack, xLength);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(center.x, minPos.y-d, minPos.z-f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            renderChain(builder, poseStack, zLength);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(minPos.x-f, minPos.y-d, center.z);
            poseStack.mulPose(Axis.ZN.rotationDegrees(90f));
            renderChain(builder, poseStack, xLength);
            poseStack.popPose();

            BufferUploader.drawWithShader(builder.build());

            RenderSystem.enableCull();
            poseStack.popPose();
        }
    }

    private static void renderChain(VertexConsumer consumer, PoseStack poseStack, float length) {
        float threeSixteenth = 3/16f;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(45f));
        poseStack.pushPose();

        poseStack.translate(-threeSixteenth/2f, 0, 0);
        Matrix4f matrix4f = poseStack.last().pose();
        consumer.addVertex(matrix4f, 0, 0, 0).setUv(0,0).setColor(1f,1f,1f,1f);
        consumer.addVertex(matrix4f, 0, length, 0).setUv(0,length).setColor(1f,1f,1f,1f);
        consumer.addVertex(matrix4f, threeSixteenth, length, 0).setUv(threeSixteenth,length).setColor(1f,1f,1f,1f);
        consumer.addVertex(matrix4f, threeSixteenth, 0, 0).setUv(threeSixteenth,0).setColor(1f,1f,1f,1f);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 0, -threeSixteenth/2f);
        matrix4f = poseStack.last().pose();
        consumer.addVertex(matrix4f, 0, 0, 0).setUv(threeSixteenth,0).setColor(1f,1f,1f,1f);
        consumer.addVertex(matrix4f, 0, length, 0).setUv(threeSixteenth,length).setColor(1f,1f,1f,1f);
        consumer.addVertex(matrix4f, 0, length, threeSixteenth).setUv(threeSixteenth*2,length).setColor(1f,1f,1f,1f);
        consumer.addVertex(matrix4f, 0, 0, threeSixteenth).setUv(threeSixteenth*2,0).setColor(1f,1f,1f,1f);
        poseStack.popPose();
        poseStack.popPose();
    }

    public static void clear() {
        FOR_RENDERING.clear();
    }
}
