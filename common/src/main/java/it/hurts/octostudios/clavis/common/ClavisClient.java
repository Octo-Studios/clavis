package it.hurts.octostudios.clavis.common;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import it.hurts.octostudios.clavis.common.client.screen.LockpickingScreen;
import it.hurts.octostudios.clavis.common.data.Lock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ClavisClient {
    public static final Map<Lock, LockpickingScreen> SCREEN_CACHE = new HashMap<>();

    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            LockWorldRenderer.clear();
        });
    }

    public static void openScreen(Lock lock, BlockPos pos) {
        Minecraft.getInstance().setScreen(SCREEN_CACHE.computeIfAbsent(lock, l -> new LockpickingScreen(pos, l)));
    }
}