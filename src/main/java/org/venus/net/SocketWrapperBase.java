package org.venus.net;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class SocketWrapperBase<S> implements Closeable {

    private final S socket;
    private final EndpointBase<S, ?> endpoint;
    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean closed = new AtomicBoolean();

    private volatile long readTimeout = -1;
    private volatile long writeTimeout = -1;

    public SocketWrapperBase(S socket, EndpointBase<S, ?> endpoint) {
        this.socket = socket;
        this.endpoint = endpoint;
    }

    @Override
    public void close() {

    }
}
