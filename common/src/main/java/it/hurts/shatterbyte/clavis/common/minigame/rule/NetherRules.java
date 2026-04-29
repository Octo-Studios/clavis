package it.hurts.shatterbyte.clavis.common.minigame.rule;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.magma.MagmaWheelWidget;

public class NetherRules {
    public static final Rule<MagmaWheelWidget> TEST = new Rule<MagmaWheelWidget>(Clavis.path("test"))
            .withEveryTick((mirror, tickCount) -> {

            });
}
