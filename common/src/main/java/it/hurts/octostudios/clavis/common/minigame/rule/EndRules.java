package it.hurts.octostudios.clavis.common.minigame.rule;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.widget.MirrorWidget;

public class EndRules {
    public static final Rule<MirrorWidget> TEST = new Rule<MirrorWidget>(Clavis.path("test"))
            .withOnClick((gear, activated) -> {

            });
}
