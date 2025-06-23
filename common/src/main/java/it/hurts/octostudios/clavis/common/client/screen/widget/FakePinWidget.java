package it.hurts.octostudios.clavis.common.client.screen.widget;

public class FakePinWidget extends LockPinWidget {
    public FakePinWidget(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean activate() {
        if (this.parent.getParent() instanceof GearMechanismWidget gear) {
            gear.children().forEach(child -> {
                if (!(child.children.getFirst() instanceof FakePinWidget)) {
                    child.children.getFirst().active = false;
                }
            });

            if (this.getParent() != null) {
                this.getParent().setRot(gear.random.nextFloat(0, 360));
            }
        }
        return false;
    }
}
