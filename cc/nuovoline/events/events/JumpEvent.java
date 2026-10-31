package cc.nuovoline.events.events;

import cc.nuovoline.events.events.callables.CancellableEvent;

public class JumpEvent extends CancellableEvent {

    private double height;

    public JumpEvent(double height) {
        this.height = height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return height;
    }
}
