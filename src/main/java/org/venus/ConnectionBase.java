package org.venus;

import java.util.concurrent.atomic.AtomicLong;

public non-sealed abstract class ConnectionBase implements Connection {
    private static final AtomicLong idx = new AtomicLong();

    private final long id;
    private final SessionContext ctx;

    public ConnectionBase() {
        this.id = idx.incrementAndGet();
        this.ctx = new SessionContext();
    }

    @Override
    public final long id() {
        return this.id;
    }

    @Override
    public final SessionContext context() {
        return this.ctx;
    }

    @Override
    public final void close() {
        closeAsync().join();
    }

    @Override
    public final String toString() {
        // id: 0x8df3229f, L:/[0:0:0:0:0:0:0:1]:4321 - R:/[0:0:0:0:0:0:0:1]:62985
        return String.format("[id: %d, L:%s %s R:%s]", id(), localAddress(), (isActive() ? "-" : "!"), remoteAddress());
    }

}
