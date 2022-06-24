package org.venus.protocols;

import org.venus.ClientConnector;
import org.venus.Protocol;
import org.venus.config.FixedPoolConfig;
import org.venus.utils.AsyncObjectFactory;
import org.venus.utils.FixedAsyncPool;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PooledProtocol extends ProtocolBase {
    private final FixedAsyncPool<ClientConnector> pool;

    public PooledProtocol(Protocol protocol, FixedPoolConfig config) {
        super(getName(protocol));
        this.pool = new FixedAsyncPool<>(new ClientConnectorFactory(protocol), Objects.requireNonNull(config, "PoolConfig is null"));
    }

    private static String getName(Protocol protocol) {
        Objects.requireNonNull(protocol, "protocol is null");
        Objects.requireNonNull(protocol.name(), "protocol's name is null");
        return protocol.name();
    }

    @Override
    protected ClientConnector doAcquire() throws Exception {
        return this.pool.acquire();
    }

    @Override
    protected void doRelease(ClientConnector client) throws Exception {
        this.pool.release(client);
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
}
