package it.hurts.octostudios.clavis.neoforge;

import it.hurts.octostudios.clavis.common.network.LockInteractionBlockers;
import net.minecraft.core.Vec3i;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.PistonEvent;

@EventBusSubscriber
public class InteractionBlockers {
    @SubscribeEvent
    public static void onPiston(PistonEvent.Pre e) {
        if (!LockInteractionBlockers.onPiston(e.getLevel(), e.getFaceOffsetPos().offset(e.getPistonMoveType() == PistonEvent.PistonMoveType.RETRACT ? e.getDirection().getNormal() : Vec3i.ZERO))) {
            //e.setCanceled(true);
        }
    }
}
