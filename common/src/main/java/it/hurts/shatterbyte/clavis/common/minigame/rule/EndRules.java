package it.hurts.shatterbyte.clavis.common.minigame.rule;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.MirrorWidget;

public class EndRules {
    public static final Rule<MirrorWidget> ROTATE = new Rule<MirrorWidget>(Clavis.path("rotate_mirror"))
            .withEveryTick((mirror, tickCount) -> {
                if (mirror.isPlaying() && (tickCount+80) % 200 == 0) {
                    mirror.rotate(135f);
                }
            });

    public static final Rule<MirrorWidget> SWAP = new Rule<MirrorWidget>(Clavis.path("swap_mousepos"))
            .withOnClick((mirror, activated) -> {
                mirror.swapMousePositions();
            });

    public static final Rule<MirrorWidget> SHOCKWAVE = new Rule<MirrorWidget>(Clavis.path("mirror_shockwave"))
            .withOnClick((mirror, activated) -> {
                mirror.doShockwave();
            });

    public static final Rule<MirrorWidget> FAKE = new Rule<MirrorWidget>(Clavis.path("fake_meteor"))
            .withOnClick((mirror, activated) -> {
                mirror.doShockwave();
            });
}
