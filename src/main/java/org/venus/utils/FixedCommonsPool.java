package org.venus.utils;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.venus.config.FixedPoolConfig;

import java.util.concurrent.CompletableFuture;

public class FixedCommonsPool<T extends AutoCloseable> extends AsyncPoolBase<T> {

    private final GenericObjectPool<T> pool;

    public FixedCommonsPool(AsyncObjectFactory<T> factory, FixedPoolConfig config) {
        super(factory, config);
        this.pool = new GenericObjectPool<>(new ClientConnectorPooledFactory(), convert());
    }

    @Override
    public CompletableFuture<T> acquireAsync() {
        CompletableFuture<T> res = new CompletableFuture<>();
        try {
            T obj = this.pool.borrowObject();
            res.complete(obj);
        } catch (Exception e) {
            res.completeExceptionally(e);
        }
        return res;
    }

    @Override
    public CompletableFuture<Void> releaseAsync(T obj) {
        this.pool.returnObject(obj);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        this.pool.close();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> clearAsync() {
        this.pool.clear();
        return CompletableFuture.completedFuture(null);
    }

    private GenericObjectPoolConfig<T> convert() {
        GenericObjectPoolConfig<T> cfg = new GenericObjectPoolConfig<>();
        cfg.setMaxIdle(config.getMaxIdle());
        cfg.setMinIdle(config.getMinIdle());
        cfg.setMaxTotal(cfg.getMaxTotal());
        return cfg;
    }

    private class ClientConnectorPooledFactory extends BasePooledObjectFactory<T> {

        @Override
        public T create() throws Exception {
            return factory.create().join();
        }

        @Override
        public PooledObject<T> wrap(T t) {
            return new DefaultPooledObject<>(t);
        }

        @Override
        public void destroyObject(PooledObject<T> p) throws Exception {
            factory.destroy(p.getObject()).join();
        }

        @Override
        public boolean validateObject(PooledObject<T> p) {
            return factory.validate(p.getObject()).join();
        }
    }
}
