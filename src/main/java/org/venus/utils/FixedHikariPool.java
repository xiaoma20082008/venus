package org.venus.utils;

import org.venus.config.FixedPoolConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FixedHikariPool<T extends AutoCloseable> extends AsyncPoolBase<T> {

    private static final AtomicIntegerFieldUpdater<FixedHikariPool> POOL_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(FixedHikariPool.class, "state");

    private static final int ST_ACTIVE = 1;
    private static final int ST_CLOSING = 2;
    private static final int ST_CLOSED = 2;

    private final CompletableFuture<Void> closeFuture;

    private volatile int state;

    public FixedHikariPool(AsyncObjectFactory<T> factory, FixedPoolConfig config) {
        super(factory, config);
        this.closeFuture = new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<T> acquireAsync() {
        CompletableFuture<T> res = new CompletableFuture<>();
        return res;
    }

    @Override
    public CompletableFuture<Void> releaseAsync(T obj) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        if (!isActive()) {
            return this.closeFuture;
        }
        this.state = ST_CLOSING;
        CompletableFuture<Void> f = clearAsync();
        this.state = ST_CLOSED;
        f.whenComplete((v, t) -> {
            if (t != null) {
                this.closeFuture.completeExceptionally(t);
            } else {
                this.closeFuture.complete(v);
            }
        });
        return this.closeFuture;
    }

    @Override
    public CompletableFuture<Void> clearAsync() {
        return null;
    }

    private boolean casState(int expected, int state) {
        return POOL_STATE_UPDATER.compareAndSet(this, expected, state);
    }

    private boolean isActive() {
        return this.state == ST_ACTIVE;
    }

    private boolean testOnCreate() {
        return this.config.isTestOnCreate();
    }

    private boolean testOnAcquire() {
        return this.config.isTestOnAcquire();
    }

    private boolean testOnRelease() {
        return this.config.isTestOnRelease();
    }
}
