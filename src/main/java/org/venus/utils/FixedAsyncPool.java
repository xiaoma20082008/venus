package org.venus.utils;

import org.venus.config.FixedPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FixedAsyncPool<T extends AutoCloseable> extends AsyncPoolBase<T> {

    private static final AtomicIntegerFieldUpdater<FixedAsyncPool> POOL_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(FixedAsyncPool.class, "state");

    private static final int ST_ACTIVE = 1;
    private static final int ST_CLOSING = 2;
    private static final int ST_CLOSED = 2;

    private final AsyncObjectFactory<T> factory;
    private final FixedPoolConfig config;
    private final Queue<T> cache;
    private final Queue<T> all;
    private final CompletableFuture<Void> closeFuture;

    private volatile int state;

    public FixedAsyncPool(AsyncObjectFactory<T> factory, FixedPoolConfig config) {
        this.factory = factory;
        this.config = new FixedPoolConfig();
        this.cache = new ConcurrentLinkedQueue<>();
        this.all = new ConcurrentLinkedQueue<>();
        this.closeFuture = new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<T> acquireAsync() {
        CompletableFuture<T> res = new CompletableFuture<>();
        T obj = cache.poll();
        doAcquire(obj, res);
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
        List<CompletableFuture<Void>> futures = new ArrayList<>(this.all.size());
        T obj = null;
        while ((obj = this.cache.poll()) != null) {
            this.all.remove(obj);
            futures.add(this.factory.destroy(obj));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void doAcquire(T object, CompletableFuture<T> res) {
        this.factory.create().whenComplete((o, t) -> {
            if (t != null) {
                res.completeExceptionally(t);
            } else {
                res.complete(o);
            }
        });
    }

    private void doCreate(CompletableFuture<T> res) {
        this.factory.create().whenComplete((obj, err) -> onCreateComplete(res, obj, err));
    }

    private void onCreateComplete(CompletableFuture<T> res, T obj, Throwable err) {
        if (err != null) {
            res.completeExceptionally(new IllegalStateException("Create object failed", err));
        } else {
            if (testOnCreate()) {
                this.factory.validate(obj).whenComplete((state, t) -> onValidateComplete(res, obj, state, t));
            }
        }
    }

    private void onValidateComplete(CompletableFuture<T> res, T obj, boolean state, Throwable err) {
        if (err != null || !state) {
            res.completeExceptionally(new IllegalStateException("Validate object failed", err));
        }
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
