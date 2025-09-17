package it.hurts.shatterbyte.clavis.neoforge.client;

import it.hurts.shatterbyte.clavis.common.client.render.LockWorldRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(Dist.CLIENT)
public class RenderEvent {
    @SubscribeEvent
    public static void render(RenderLevelStageEvent e) {
        if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            LockWorldRenderer.render(e.getCamera(), e.getModelViewMatrix(), e.getPoseStack(), e.getPartialTick(), Minecraft.getInstance().renderBuffers().bufferSource(), Minecraft.getInstance().level, e.getFrustum());
        }
    }
}
