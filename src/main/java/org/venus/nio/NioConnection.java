package org.venus.nio;

import org.venus.ConnectionBase;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

public final class NioConnection extends ConnectionBase {

    private final SocketChannel channel;

    public NioConnection(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public InetSocketAddress localAddress() {
        return null;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        return null;
    }

}
