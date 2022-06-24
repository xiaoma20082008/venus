package org.venus.core;

import org.venus.Container;
import org.venus.Pipeline;

public abstract class ContainerBase extends LifecycleBase implements Container {

    private final Pipeline pipeline;
    private String name;
    private Container next;

    public ContainerBase() {
        this.pipeline = new StandardPipeline(this);
    }

    @Override
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Container getNext() {
        return next;
    }

    @Override
    public void setNext(Container next) {
        this.next = next;
    }
}
