package it.hurts.shatterbyte.clavis.common;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import it.hurts.shatterbyte.clavis.common.client.ClientMinigameTypeRegistry;
import it.hurts.shatterbyte.clavis.common.client.render.LockWorldRenderer;
import it.hurts.shatterbyte.clavis.common.client.screen.LockpickingScreen;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.shatterbyte.clavis.common.minigame.rule.Rule;
import it.hurts.shatterbyte.clavis.common.network.packet.LockRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClavisClient {
    public static final Map<Lock, LockpickingScreen> SCREEN_CACHE = new HashMap<>();

    public static void init() {
        ClientMinigameTypeRegistry.init();
        Rule.registerAll();

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            LockWorldRenderer.clear();
        });
    }

    public static void openScreen(Lock lock, BlockPos pos) {
        Minecraft.getInstance().setScreen(SCREEN_CACHE.computeIfAbsent(lock, l -> new LockpickingScreen<>(pos, l, ClientMinigameTypeRegistry.getFactory(l.getType(Minecraft.getInstance().level)))));
    }

    public static void onUnloadChunk(ClientLevel level, LevelChunk levelChunk) {
        ChunkPos chunkPos = levelChunk.getPos();
        List<Lock> toRemove = LockWorldRenderer.FOR_RENDERING.stream()
                .filter(lock -> lock.getBox().intersectsChunk(chunkPos))
                .toList();

        toRemove.forEach(LockWorldRenderer.FOR_RENDERING::remove);
    }

    public static void onLoadChunk(ClientLevel level, LevelChunk levelChunk) {
        NetworkManager.sendToServer(new LockRequestPacket(levelChunk.getPos()));
    }
}