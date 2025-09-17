package it.hurts.shatterbyte.clavis.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.PistonEvent;

@EventBusSubscriber
public class InteractionBlockers {
    @SubscribeEvent
    public static void onPiston(PistonEvent.Pre e) {

    }
}
