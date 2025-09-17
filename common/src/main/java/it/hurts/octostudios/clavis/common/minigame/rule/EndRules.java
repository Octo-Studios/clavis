package it.hurts.octostudios.clavis.common.minigame.rule;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.widget.MirrorWidget;

public class EndRules {
    public static final Rule<MirrorWidget> ROTATE = new Rule<MirrorWidget>(Clavis.path("rotate"))
            .withEveryTick((mirror, tickCount) -> {
                if (mirror.isPlaying() && (tickCount+80) % 200 == 0) {
                    mirror.rotate(135f);
                }
            });

    public static final Rule<MirrorWidget> SWAP = new Rule<MirrorWidget>(Clavis.path("swap"))
            .withOnClick((mirror, activated) -> {
                mirror.swapMousePositions();
            });

    public static final Rule<MirrorWidget> SHOCKWAVE = new Rule<MirrorWidget>(Clavis.path("shockwave"))
            .withOnClick((mirror, activated) -> {
                mirror.doShockwave();
            });
}
