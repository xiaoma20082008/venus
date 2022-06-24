package org.venus.utils;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface AsyncPool<T extends AutoCloseable> extends Closeable {

    T acquire();

    void release(T obj);

    void clear();

    @Override
    void close();

    CompletableFuture<T> acquireAsync();

    CompletableFuture<Void> releaseAsync(T obj);

    CompletableFuture<Void> clearAsync();

    CompletableFuture<Void> closeAsync();
}
