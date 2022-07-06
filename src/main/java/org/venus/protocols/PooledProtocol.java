package org.venus.protocols;

import org.venus.*;
import org.venus.config.FixedPoolConfig;
import org.venus.utils.AsyncObjectFactory;
import org.venus.utils.AsyncPool;
import org.venus.utils.FixedCommonsPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PooledProtocol extends ProtocolBase {
    private final AsyncPool<ClientConnector> pool;

    public PooledProtocol(Protocol protocol) {
        this(protocol, new FixedPoolConfig());
    }

    public PooledProtocol(Protocol protocol, FixedPoolConfig config) {
        super(getName(protocol));
        this.pool = new FixedCommonsPool<>(new ClientConnectorFactory(protocol), Objects.requireNonNull(config, "PoolConfig is null"));
    }

    private static String getName(Protocol protocol) {
        Objects.requireNonNull(protocol, "protocol is null");
        Objects.requireNonNull(protocol.name(), "protocol's name is null");
        return protocol.name();
    }

    @Override
    protected ClientConnector doAcquire() throws Exception {
        ClientConnector client = new ClientConnectorProxy(this.pool.acquire());
        client.start();
        return client;
    }

    private record ClientConnectorFactory(Protocol protocol) implements AsyncObjectFactory<ClientConnector> {

        @Override
        public CompletableFuture<ClientConnector> create() {
            CompletableFuture<ClientConnector> res = new CompletableFuture<>();
            res.complete(protocol.create());
            return res;
        }

        @Override
        public CompletableFuture<Void> destroy(ClientConnector client) {
            protocol.release(client);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<Boolean> validate(ClientConnector client) {
            return CompletableFuture.completedFuture(protocol.check(client));
        }

    }

    private class ClientConnectorProxy extends ClientConnectorBase implements InvocationHandler {

        private volatile ClientConnector client;

        public ClientConnectorProxy(ClientConnector client) {
            this.client = client;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(this.client, args);
        }

        @Override
        public CompletableFuture<Response> invokeAsync(Request request) {
            return this.client.invokeAsync(request);
        }

        @Override
        public void start() throws Exception {
            this.client.start();
        }

        @Override
        public void close() throws Exception {
            PooledProtocol.this.pool.release(this.client);
            this.client = null;
        }

    }
}
