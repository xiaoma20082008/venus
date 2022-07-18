package org.venus.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <S> The type used by the socket wrapper associated with this endpoint.
 * @param <U> The type of the underlying socket used by this endpoint.
 */
public abstract class EndpointBase<S, U> {

    protected final Acceptor<U> acceptor;
    protected final Map<U, SocketWrapperBase<S>> connections = new ConcurrentHashMap<>();
    protected SocketHandler<S> handler;
    protected volatile boolean paused = false;
    protected volatile boolean running = false;

    public EndpointBase() {
        this.acceptor = new Acceptor<>(this);
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunning() {
        return running;
    }

    public void pause() {
        if (running && !paused) {
            paused = true;
        }
    }

    public void resume() {
        if (running && paused) {
            paused = false;
        }
    }

    public void await() {

    }

    public void release() {

    }

    public void close(U socket) {
        SocketWrapperBase<S> swb = connections.get(socket);
        if (swb != null) {
            swb.close();
        }
    }

    public abstract U accept() throws Exception;

    public abstract void destroy(U socket);

    public abstract boolean configure(U socket);


}
