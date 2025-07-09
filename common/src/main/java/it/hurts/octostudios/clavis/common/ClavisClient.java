package it.hurts.octostudios.clavis.common;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;

public class ClavisClient {
    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            LockWorldRenderer.clear();
        });
    }
}