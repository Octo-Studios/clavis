package it.hurts.octostudios.clavis.common.minigame.rule;

import it.hurts.octostudios.clavis.common.Clavis;
import it.hurts.octostudios.clavis.common.client.screen.widget.FakePinWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.LockPinWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.RotatingParent;

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
                    gear.rotateGear(90f * -GearMechanismWidget.sign(gear.getRandom().nextInt(2)-1));
                }
            });

    public static final Rule<GearMechanismWidget> FAKE_PIN = new Rule<GearMechanismWidget>(Clavis.path("fake_pin"))
            .withOnCreate(gear -> {
                RotatingParent<LockPinWidget, GearMechanismWidget> parent = LockPinWidget.createFake(
                        Math.round(gear.getWidth()/2f),
                        Math.round(gear.getHeight()/2f),
                        gear
                );
                gear.children().add(parent);
                ((FakePinWidget) parent.children().getFirst()).moveToRandomPosition(gear);
            })
            .withOnClick((gear, activated) -> {
                gear.children().forEach(rotating -> {
                    if (!(rotating.children().getFirst() instanceof FakePinWidget fake)) {
                        return;
                    }

                    fake.moveToRandomPosition(gear);
                });
            })
            .withEveryTick((gear, tickCount) -> {
                AtomicInteger index = new AtomicInteger(0);
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
            .withOnCreate(gear -> {
                gear.selfDestructRule = true;
            })
            .withEveryTick((gear, tickCount) -> {
                if ((tickCount+80) % 139 == 0 && gear.getRandom().nextFloat() > 0.3) {
                    gear.activateSelfDestruction();
                }
            });

    public static final Rule<GearMechanismWidget> FULL_THROTTLE = new Rule<GearMechanismWidget>(Clavis.path("full_throttle"))
            .withEveryTick((gear, tickCount) -> {
                gear.arrowSpeedModifier = (float) Math.max(gear.arrowSpeedModifier - gear.getMaxArrowSpeed()*0.15/20f, 0);
            })
            .withOnClick((gear, activated) -> {
                gear.arrowSpeedModifier += gear.getMaxArrowSpeed()*0.2f;
            });
}
