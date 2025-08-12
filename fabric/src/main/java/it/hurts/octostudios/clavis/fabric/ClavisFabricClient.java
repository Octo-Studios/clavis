package it.hurts.octostudios.clavis.fabric;

import it.hurts.octostudios.clavis.common.ClavisClient;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class ClavisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClavisClient.init();

        ClientChunkEvents.CHUNK_LOAD.register(ClavisClient::onLoadChunk);
        ClientChunkEvents.CHUNK_UNLOAD.register(ClavisClient::onUnloadChunk);
        WorldRenderEvents.AFTER_ENTITIES.register(context -> LockWorldRenderer.render(
                context.camera(),
                context.projectionMatrix(),
                context.matrixStack(),
                context.tickCounter(),
                context.consumers(),
                context.world(),
                context.frustum()
        ));
    }
}
