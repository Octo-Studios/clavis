package it.hurts.octostudios.clavis.common.minigame.rule;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.widget.FakePinWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.LockPinWidget;
import net.minecraft.util.Mth;

import java.util.concurrent.atomic.AtomicInteger;

public class OverworldRules {
    public static final Rule<GearMechanismWidget> MOOD_SWINGS = new Rule<GearMechanismWidget>(Clavis.path("mood_swings"))
            .withOnClick((gear, activated) -> {
                if (activated) {
                    gear.flipArrowDirection();
                }
            });

    public static final Rule<GearMechanismWidget> ROTATE_GEAR = new Rule<GearMechanismWidget>(Clavis.path("nauseous_carousel"))
            .withEveryTick((gear, tickCount) -> {
                if ((tickCount+80) % 180 == 0) {
                    gear.rotateGear(120f * -Mth.sign(gear.getArrowSpeed()));
                }
            });

    public static final Rule<GearMechanismWidget> FAKE_PIN = new Rule<GearMechanismWidget>(Clavis.path("fake_pin"))
            .withOnCreate(gear -> {
                gear.children().add(LockPinWidget.createFake(
                        Math.round(gear.getWidth()/2f),
                        Math.round(gear.getHeight()/2f),
                        gear.getFreeSpots().get(gear.getRandom().nextInt(0, gear.getFreeSpots().size())) * (360f / gear.getMaxSpots()),
                        gear
                ));
            })
            .withEveryTick((gear, tickCount) -> {
                AtomicInteger index = new AtomicInteger(1);
                gear.children().forEach(rotating -> {
                    if (!(rotating.children().getFirst() instanceof FakePinWidget fake)) {
                        return;
                    }

                    if ((tickCount + index.getAndIncrement() * 2L) % 30 == 0) {
                        fake.letThemKnow();
                    }
                });
            });

    public static final Rule<GearMechanismWidget> SELF_DESTRUCTION = new Rule<GearMechanismWidget>(Clavis.path("self_destruction"))
            .withEveryTick((gear, tickCount) -> {
                if (tickCount % 139 == 0 && gear.getRandom().nextFloat() > 0.3) {
                    gear.activateSelfDestruction();
                }
            });
}
