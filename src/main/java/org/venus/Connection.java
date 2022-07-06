package org.venus;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public sealed interface Connection extends Closeable permits ConnectionBase {

    long id();

    SessionContext context();

    InetSocketAddress localAddress();

    InetSocketAddress remoteAddress();

    boolean isActive();

    @Override
    void close();

    CompletableFuture<Void> closeAsync();
}
