package org.venus.core;

import org.venus.Lifecycle;
import org.venus.LifecycleState;

public abstract class LifecycleBase implements Lifecycle {

    protected volatile LifecycleState state;

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public LifecycleState getState() {
        return this.state;
    }

}
