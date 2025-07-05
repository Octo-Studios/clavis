package it.hurts.octostudios.clavis.common.mixin;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.network.packet.OpenLockpickingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    private void injected(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
        List<Lock> locks = ClavisSavedData.get(player.serverLevel()).getLocksAt(packet.getHitResult().getBlockPos());
        if (locks.isEmpty()) {
            return;
        }

        NetworkManager.sendToPlayer(player, new OpenLockpickingPacket(packet.getHitResult().getBlockPos(), locks.getFirst()));
        ci.cancel();
    }
}
