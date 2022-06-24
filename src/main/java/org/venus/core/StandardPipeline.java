package org.venus.core;

import org.venus.Container;
import org.venus.Pipeline;
import org.venus.Valve;

import java.util.ArrayList;
import java.util.List;

public class StandardPipeline extends LifecycleBase implements Pipeline {

    protected Valve basic = null;
    protected Valve first = null;
    protected Container container = null;

    public StandardPipeline() {
        this(null);
    }

    public StandardPipeline(Container container) {
        setContainer(container);
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Valve getBasic() {
        return this.basic;
    }

    @Override
    public void setBasic(Valve basic) {
        this.basic = basic;
    }

    @Override
    public Valve getFirst() {
        return this.first == null ? this.basic : this.first;
    }

    @Override
    public void addValue(Valve v) {
        if (this.first == null) {
            this.first = v;
            this.first.setNext(this.basic);
        } else {
            Valve c = first;
            while (c != null) {
                if (c.getNext() == this.basic) {
                    // c.next == basic
                    c.setNext(v);
                    v.setNext(this.basic);
                    break;
                }
                c = c.getNext();
            }
        }
    }

    @Override
    public void removeValue(Valve v) {

    }

    @Override
    public Valve[] getValues() {
        List<Valve> valves = new ArrayList<>();
        Valve curr = first;
        while (curr != null) {
            valves.add(curr);
            curr = curr.getNext();
        }
        return valves.toArray(new Valve[0]);
    }

}
