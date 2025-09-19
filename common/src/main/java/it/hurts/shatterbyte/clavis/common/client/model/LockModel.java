package it.hurts.shatterbyte.clavis.common.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.shatterbyte.clavis.common.Clavis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class LockModel extends Model {
    public static ResourceLocation getTexture(ResourceLocation minigameType) {
        return Clavis.path("textures/model/"+minigameType.getPath()+"/lock.png");
    }

    private final ModelPart main;
    private boolean glow;

    public LockModel(boolean glow) {
        super(glow ? RenderType::entityCutout : RenderType::entityCutoutNoCull);
        this.main = createBodyLayer(glow).bakeRoot().getChild("main");
        this.glow = glow;
    }

    public static LayerDefinition createBodyLayer(boolean glow) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 23.4F, -0.5F, 0.0F, 1.5708F, 0.0F));

        if (glow) {
            main.addOrReplaceChild("glow", CubeListBuilder.create().texOffs(0, 20).addBox(-2.5F, 0.0F, -1.0F, 5.0F, 4.0F, 2.0F, new CubeDeformation(-4.5F))
                    .texOffs(0, 0).addBox(-3.5F, -4.0F, -2.0F, 7.0F, 5.0F, 4.0F, new CubeDeformation(-4.5F)), PartPose.offset(-0.5F, -4.0F, 0.0F));
        } else {
            main.addOrReplaceChild("layer", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, -0.25F, -3.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.25F))
                    .texOffs(14, 20).addBox(-0.5F, -3.25F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-0.5F, -3.75F, 0.0F));

            main.addOrReplaceChild("model", CubeListBuilder.create().texOffs(18, 10).addBox(-1.5F, 0.625F, -3.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                    .texOffs(22, 5).addBox(-0.5F, -1.375F, -2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(22, 0).addBox(-0.5F, -2.375F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(24, 20).addBox(-0.5F, -1.375F, 1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -4.625F, 0.0F));
        }

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        poseStack.translate(0, 1, 0);
        poseStack.scale(-1,-1,1);
        main.render(poseStack, buffer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }
}
