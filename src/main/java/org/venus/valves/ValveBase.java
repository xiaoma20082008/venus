package org.venus.valves;

import org.venus.Contained;
import org.venus.Container;
import org.venus.Valve;
import org.venus.core.LifecycleBase;

public abstract class ValveBase extends LifecycleBase implements Valve, Contained {

    protected Container container = null;
    protected Valve next = null;

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Valve getNext() {
        return this.next;
    }

    @Override
    public void setNext(Valve next) {
        this.next = next;
    }

}
