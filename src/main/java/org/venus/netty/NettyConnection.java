package org.venus.netty;

import io.netty.channel.Channel;
import org.venus.ConnectionBase;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class NettyConnection extends ConnectionBase {

    private final Channel channel;
    private final CompletableFuture<Void> closeFuture;

    public NettyConnection(Channel channel) {
        this.channel = Objects.requireNonNull(channel, "channel is null");
        this.closeFuture = new CompletableFuture<>();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) this.channel.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        this.channel.close().addListener(future -> {
            if (future.isSuccess()) {
                closeFuture.complete(null);
            } else {
                closeFuture.completeExceptionally(future.cause());
            }
        });
        return closeFuture;
    }

}
