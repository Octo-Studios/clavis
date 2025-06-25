package it.hurts.octostudios.clavis.common.minigame.rule;

import it.hurts.octostudios.clavis.common.client.screen.widget.FakePinWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.octostudios.clavis.common.client.screen.widget.LockPinWidget;
import lombok.Getter;
import net.minecraft.util.Mth;

@Getter
public class OverworldRules {
    public static final Rule<GearMechanismWidget> FLIP_ARROW = new Rule<GearMechanismWidget>()
            .withOnClick((gear, activated) -> {
                if (activated) {
                    gear.flipArrowDirection();
                }
            });


    public static final Rule<GearMechanismWidget> ROTATE_GEAR = new Rule<GearMechanismWidget>()
            .withEveryTick((gear, tickCount) -> {
                if ((tickCount+80) % 180 == 0) {
                    gear.rotateGear(90f * -Mth.sign(gear.getArrowSpeed()));
                }
            });

    public static final Rule<GearMechanismWidget> FAKE_PIN = new Rule<GearMechanismWidget>()
            .withOnCreate(gear -> {
                gear.children().add(LockPinWidget.createFake(Math.round(gear.getWidth()/2f), Math.round(gear.getHeight()/2f), gear.getRandom().nextFloat(0, 359), gear));
            })
            .withEveryTick((gear, tickCount) -> {
                gear.children().stream().filter(child -> child.children().getFirst() instanceof FakePinWidget).forEach(rotating -> {
                    rotating.setRot(rotating.getRot() + 0.05f);
                });
            });
}
