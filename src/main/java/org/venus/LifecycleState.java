package org.venus;

public enum LifecycleState {
    // new->init->start->shutdown->close
    NEW,

    STARTED,
    CLOSED,
    ;

    public final boolean available;

    LifecycleState() {
        this(false);
    }

    LifecycleState(boolean available) {
        this.available = available;
    }
}
