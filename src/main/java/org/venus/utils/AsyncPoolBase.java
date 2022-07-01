package org.venus.utils;

import org.venus.config.FixedPoolConfig;

public abstract class AsyncPoolBase<T extends AutoCloseable> implements AsyncPool<T> {

    protected final AsyncObjectFactory<T> factory;
    protected final FixedPoolConfig config;

    public AsyncPoolBase(AsyncObjectFactory<T> factory, FixedPoolConfig config) {
        this.factory = factory;
        this.config = config;
    }

    @Override
    public T acquire() {
        return acquireAsync().join();
    }

    @Override
    public void release(T obj) {
        releaseAsync(obj).join();
    }

    @Override
    public void close() {
        closeAsync().join();
    }

    @Override
    public void clear() {
        clearAsync().join();
    }
}
