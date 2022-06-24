package org.venus;

public interface Lifecycle extends AutoCloseable {

    void init() throws Exception;

    void shutdown() throws Exception;

    void start() throws Exception;

    LifecycleState getState();
}
