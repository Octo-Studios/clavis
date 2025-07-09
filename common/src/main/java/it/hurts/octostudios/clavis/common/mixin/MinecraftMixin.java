package it.hurts.octostudios.clavis.common.mixin;

import it.hurts.octostudios.clavis.common.client.render.LockWorldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "setLevel", at = @At("TAIL"))
    private void injected(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
        LockWorldRenderer.clear();
    }
}
