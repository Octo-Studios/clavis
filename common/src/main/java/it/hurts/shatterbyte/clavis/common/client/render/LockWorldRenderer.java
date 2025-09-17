package it.hurts.shatterbyte.clavis.common.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import it.hurts.shatterbyte.clavis.common.client.model.LockModel;
import it.hurts.shatterbyte.clavis.common.data.Box;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class LockWorldRenderer {
    public static final RenderType LOCK_TYPE = RenderType.entityCutoutNoCull(LockModel.TEXTURE);
    public static final RenderType GLOW_TYPE = RenderType.create("clavis_outline",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
            1536, true, false,
            RenderType.CompositeState.builder()
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .setColorLogicState(RenderStateShard.NO_COLOR_LOGIC)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setCullState(RenderStateShard.CULL)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, true))
                    .setTextureState(new RenderStateShard.TextureStateShard(LockModel.TEXTURE, false, false))
                    .createCompositeState(false));

    private static final ResourceLocation CHAIN_TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/chain.png");
    public static final RenderType CHAIN_TYPE = RenderType.entityCutoutNoCull(CHAIN_TEXTURE);

    public static final Set<Lock> FOR_RENDERING = new HashSet<>();
    public static final LockModel LOCK = new LockModel(false);
    public static final LockModel GLOW = new LockModel(true);

    public static final float f = 1 / 16f;
    public static final float d = 0.13258f / 2f;
    public static final float THREE_SIXTEENTH = 3f / 16f;

    private static class LockRenderData {
        final Vec3 center;
        final int light;
        final float ticks;
        final Lock lock;
        final AABB renderBox;
        final boolean shouldRenderLock;

        LockRenderData(Lock lock, AABB renderBox, Vec3 center, int light, float ticks, boolean shouldRenderLock) {
            this.lock = lock;
            this.center = center;
            this.light = light;
            this.ticks = ticks;
            this.renderBox = renderBox;
            this.shouldRenderLock = shouldRenderLock;
        }
    }

    public static void render(Camera camera, Matrix4f modelViewMatrix, PoseStack poseStack, DeltaTracker partialTick, MultiBufferSource multiBufferSource, ClientLevel level, Frustum frustum) {
        if (FOR_RENDERING.isEmpty()) return;

        List<LockRenderData> dataList = FOR_RENDERING.stream().filter(lock -> {
            Box b = lock.getBox();
            Vec3 min = new Vec3(b.minX, b.minY, b.minZ);
            Vec3 max = new Vec3(b.maxX + 1, b.maxY + 1, b.maxZ + 1);
            return frustum.isVisible(new AABB(min, max.add(0, 1, 0)));
        }).map(lock -> {
            Box b = lock.getBox();
            Vec3 min = new Vec3(b.minX, b.minY, b.minZ);
            Vec3 max = new Vec3(b.maxX + 1, b.maxY + 1, b.maxZ + 1);

            if (max.subtract(min).equals(new Vec3(1,1,1))) {
                BlockPos blockPos = new BlockPos((int) min.x, (int) min.y, (int) min.z);
                VoxelShape shape = level.getBlockState(blockPos).getShape(level, blockPos);
                if (!shape.isEmpty()) {
                    AABB bounds = shape.bounds();
                    min = bounds.getMinPosition().add(Vec3.atLowerCornerOf(blockPos));
                    max = bounds.getMaxPosition().add(Vec3.atLowerCornerOf(blockPos));
                }
            }

            AABB renderBox = new AABB(min, max);
            Vec3 center = new Vec3(Mth.lerp(0.5, min.x, max.x), max.y+0.5f, Mth.lerp(0.5, min.z, max.z));
            BlockPos pos = new BlockPos(Mth.floor(center.x), Mth.floor(center.y), Mth.floor(center.z));
            int sky = level.getBrightness(LightLayer.SKY, pos);
            int block = level.getBrightness(LightLayer.BLOCK, pos);
            int light = LightTexture.pack(block, sky);
            float hash = (new Random(lock.getSeed()).nextFloat() * (float) Math.PI);
            float ticks = (level.getGameTime() + partialTick.getGameTimeDeltaPartialTick(true)) / 10f + hash;

            VoxelShape atLockPos = level.getBlockState(pos).getShape(level, pos);
            AABB lockAABB = new AABB(center, center).inflate(0.25,0.5,0.25);
            return new LockRenderData(lock, renderBox, center, light, ticks, atLockPos.isEmpty() || !atLockPos.bounds().move(pos).intersects(lockAABB));
        }).toList();

        Vec3 camPos = camera.getPosition();
        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumer lockBuf = multiBufferSource.getBuffer(LOCK_TYPE);
        for (LockRenderData data : dataList) {
            if (!data.shouldRenderLock) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(data.center.x, data.center.y + 0.25f + (float) Math.sin(data.ticks) * 0.075f, data.center.z);
            poseStack.mulPose(Axis.YP.rotation((data.ticks / 2f) % ((float) Math.PI * 2)));
            LOCK.renderToBuffer(poseStack, lockBuf, data.light, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        VertexConsumer glowBuf = multiBufferSource.getBuffer(GLOW_TYPE);
        for (LockRenderData data : dataList) {
            if (!data.shouldRenderLock) {
                continue;
            }

            int color = data.lock.getDifficulty() < 0.33f ? 0xff33ff22 : data.lock.getDifficulty() < 0.66f ? 0xffffcc00 : 0xffff0011;
            poseStack.pushPose();
            poseStack.translate(data.center.x, data.center.y + 0.25f + (float) Math.sin(data.ticks) * 0.075f, data.center.z);
            poseStack.mulPose(Axis.YP.rotation((data.ticks / 2f) % ((float) Math.PI * 2)));
            GLOW.renderToBuffer(poseStack, glowBuf, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
            poseStack.popPose();
        }

        // 3) render chains
        Quaternionf xRot = Axis.XP.rotationDegrees(90f);
        Quaternionf zRot = Axis.ZN.rotationDegrees(90f);
        VertexConsumer chainBuf = multiBufferSource.getBuffer(CHAIN_TYPE);
        for (LockRenderData data : dataList) {
            Vec3 min = data.renderBox.getMinPosition();
            Vec3 max = data.renderBox.getMaxPosition();
            float yLen = (float) (max.y - min.y) + f * 2f;
            float xLen = (float) (max.x - min.x) + f * 2f;
            float zLen = (float) (max.z - min.z) + f * 2f;
            int light = data.light;

            // four vertical chains
            renderChainAt(chainBuf, poseStack, data.center.x, min.y - f, min.z - d, null, light, yLen);
            renderChainAt(chainBuf, poseStack, max.x + d, min.y - f, data.center.z, null, light, yLen);
            renderChainAt(chainBuf, poseStack, min.x - d, min.y - f, data.center.z, null, light, yLen);
            renderChainAt(chainBuf, poseStack, data.center.x, min.y - f, max.z + d, null, light, yLen);

            // top horizontal
            renderChainAt(chainBuf, poseStack, data.center.x, max.y + d, min.z - f, xRot, light, zLen);
            renderChainAt(chainBuf, poseStack, min.x - f, max.y + d, data.center.z, zRot, light, xLen);
            renderChainAt(chainBuf, poseStack, data.center.x, min.y - d, min.z - f, xRot, light, zLen);
            renderChainAt(chainBuf, poseStack, min.x - f, min.y - d, data.center.z, zRot, light, xLen);
        }

        poseStack.popPose();
        //Minecraft.getInstance().player.displayClientMessage(Component.literal("Rendered locks: " + dataList.size() + ", fps: " + Minecraft.getInstance().getFps()), true);
    }

    private static void renderChainAt(VertexConsumer buf, PoseStack ps, double x, double y, double z, Quaternionf rotation, int light, float length) {
        ps.pushPose();
        ps.translate(x, y, z);
        if (rotation != null) {
            ps.mulPose(rotation);
        }
        renderChain(buf, ps, light, length);
        ps.popPose();
    }

    private static void renderChain(VertexConsumer consumer, PoseStack poseStack, int light, float length) {
        float width = THREE_SIXTEENTH;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(45f));

        poseStack.pushPose();
        poseStack.translate(-width / 2f, 0, 0);
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();
        Vector3f nZ = new Vector3f(0, 0, 1);
        normalMatrix.transform(nZ);

        consumer.addVertex(matrix, 0, 0, 0).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nZ.x, nZ.y, nZ.z);
        consumer.addVertex(matrix, 0, length, 0).setColor(1f, 1f, 1f, 1f).setUv(0, length).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nZ.x, nZ.y, nZ.z);
        consumer.addVertex(matrix, width, length, 0).setColor(1f, 1f, 1f, 1f).setUv(width, length).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nZ.x, nZ.y, nZ.z);
        consumer.addVertex(matrix, width, 0, 0).setColor(1f, 1f, 1f, 1f).setUv(width, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nZ.x, nZ.y, nZ.z);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 0, -width / 2f);
        matrix = poseStack.last().pose();
        normalMatrix = poseStack.last().normal();
        Vector3f nX = new Vector3f(1, 0, 0);
        normalMatrix.transform(nX);

        consumer.addVertex(matrix, 0, 0, 0).setColor(1f, 1f, 1f, 1f).setUv(width, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nX.x, nX.y, nX.z);
        consumer.addVertex(matrix, 0, length, 0).setColor(1f, 1f, 1f, 1f).setUv(width, length).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nX.x, nX.y, nX.z);
        consumer.addVertex(matrix, 0, length, width).setColor(1f, 1f, 1f, 1f).setUv(width * 2, length).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nX.x, nX.y, nX.z);
        consumer.addVertex(matrix, 0, 0, width).setColor(1f, 1f, 1f, 1f).setUv(width * 2, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nX.x, nX.y, nX.z);
        poseStack.popPose();

        poseStack.popPose();
    }

    public static void clear() {
        FOR_RENDERING.clear();
    }
}
