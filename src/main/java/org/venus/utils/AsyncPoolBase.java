package org.venus.utils;

public abstract class AsyncPoolBase<T extends AutoCloseable> implements AsyncPool<T> {

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
