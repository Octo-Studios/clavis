package it.hurts.octostudios.clavis.fabric;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.octostudios.clavis.common.ClavisClient;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;

public final class ClavisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClavisClient.init();
        WorldRenderEvents.AFTER_ENTITIES.register(context -> LockWorldRenderer.render(
                context.camera(),
                context.projectionMatrix(),
                context.matrixStack(),
                context.tickCounter(),
                context.consumers(),
                context.world()
        ));
    }
}
