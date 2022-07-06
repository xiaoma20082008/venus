package org.venus;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;

public final class ConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private final Map<String, Connection> connectionMap;
    private final int maxConnections;
    private final Semaphore semaphore;

    public ConnectionManager(int maxConnections) {
        this.connectionMap = new HashMap<>();
        this.maxConnections = maxConnections;
        this.semaphore = new Semaphore(maxConnections);
    }

    public boolean canAcquire() {
        return this.semaphore.tryAcquire();
    }

    public void onConnected(Connection connection) {
        LOGGER.info("## connected connection: {}", connection);
    }

    public void onClosed(Connection connection) {
        if (connection == null) {
            return;
        }
        LOGGER.info("## closed channel: {}", connection);
        connection.closeAsync();
        this.semaphore.release();
    }

    public void closeAll() {
        Iterator<Map.Entry<String, Connection>> it = connectionMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().closeAsync();
            it.remove();
        }
        this.semaphore.release(this.maxConnections);
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public int getAvailableCount() {
        return semaphore.availablePermits();
    }

    public Connection getConnection(String address) {
        return this.connectionMap.get(address);
    }

    private String toKey(Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        return remoteAddress.getHostName() + ":" + remoteAddress.getPort();
    }

    public void onFailed(ChannelHandlerContext ctx, Throwable t) {
        ctx.channel().close();
        ctx.close();
    }
}
