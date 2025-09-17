package it.hurts.shatterbyte.clavis.common.minigame.rule;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.FakeMeteorWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.MeteorWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.MirrorWidget;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            .withOnCreate(mirrorWidget -> {
                FakeMeteorWidget meteor = new FakeMeteorWidget(0, 0, mirrorWidget.getRandomMeteorSize(), mirrorWidget);
                Vector2d randomPos = mirrorWidget.getRandomPos().sub(meteor.getWidth()/2f, meteor.getHeight()/2f);
                meteor.setPosition((int) randomPos.x, (int) randomPos.y);
                meteor.precisePosition = randomPos;
                meteor.oldPos = new Vector2d(randomPos);
                mirrorWidget.children().add(meteor);
            })
            .withOnClick((mirrorWidget, aBoolean) -> {
                mirrorWidget.children().forEach(meteor -> {
                    if (!(meteor instanceof FakeMeteorWidget)) {
                        return;
                    }

                    Vector2d randomPos = mirrorWidget.getRandomPos().sub(meteor.getWidth()/2f, meteor.getHeight()/2f);;

                    meteor.precisePosition = randomPos;
                    meteor.oldPos = new Vector2d(randomPos);
                });
            });

    public static final Rule<MirrorWidget> HOT_METEOR = new Rule<MirrorWidget>(Clavis.path("hot_meteor"))
            .withEveryTick((mirror, tickCount) -> {
                if (mirror.isPlaying() && (tickCount+70) % 119 == 0 && mirror.getRandom().nextFloat() > 0.5f) {
                    List<MeteorWidget> meteors = new ArrayList<>(mirror.children().stream().filter(meteorWidget -> !(meteorWidget instanceof FakeMeteorWidget) && !meteorWidget.isCracked()).toList());
                    if (meteors.isEmpty()) {
                        return;
                    }
                    Collections.shuffle(meteors);
                    meteors.getFirst().makeItHot();
                }
            });
}
