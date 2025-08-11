package it.hurts.octostudios.clavis.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.hurts.octostudios.clavis.common.LockManager;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.data.LootUtils;
import it.hurts.octostudios.octolib.OctoLib;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BlockEntityTicker.class)
public class BlockEntityTickerMixin {
    @Inject(require = 0, method = "onServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;setLootTable(Lnet/minecraft/resources/ResourceKey;J)V", shift = At.Shift.AFTER))
    private static void injected(CallbackInfo ci, @Local BlockEntityTicker.Entry entry, @Local(name = "rbe") RandomizableContainerBlockEntity rbe, @Local ServerLevel level) {
        long startTimestamp = System.nanoTime();
        float difficulty = (float) LootUtils.calculateDifficulty(level, entry.getPosition(), rbe, 20);
        OctoLib.LOGGER.info("Elapsed time: {}", String.format("%.3f", (System.nanoTime() - startTimestamp) / 1000000d));

        if (difficulty < 0.05f) {
            return;
        }

        LockManager.addLock(level, new Lock(UUID.randomUUID(), new Box(entry.getPosition()), difficulty, rbe.getLootTableSeed(), true));
    }
}
