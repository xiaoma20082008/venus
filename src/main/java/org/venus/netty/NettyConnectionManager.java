package org.venus.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.SessionContext;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class NettyConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConnectionManager.class);

    private final Map<String, Channel> channelMap;
    private final int maxConnections;
    private final Semaphore semaphore;
    private final AtomicLong sequencer;

    public NettyConnectionManager(int maxConnections) {
        this.channelMap = new HashMap<>();
        this.maxConnections = maxConnections;
        this.semaphore = new Semaphore(maxConnections);
        this.sequencer = new AtomicLong(1);
    }

    public boolean onConnected(ChannelHandlerContext ctx) {
        SessionContext sessionContext = new SessionContext()
                .incoming(ctx.channel())
                .id(this.sequencer.getAndIncrement());
        ctx.channel().attr(SessionContext.ATTR_SESSION_KEY).set(sessionContext);
        LOGGER.info("## connected channel: {}", ctx.channel());
        return true;
    }

    public void onClosed(ChannelHandlerContext ctx) {
        LOGGER.info("## closed channel: {}", ctx.channel());
        SessionContext sc = ctx.channel().attr(SessionContext.ATTR_SESSION_KEY).getAndSet(null);
        sc.reset();
        ctx.channel().close();
    }

    public void closeAll() {
        Iterator<Map.Entry<String, Channel>> it = channelMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().close();
            it.remove();
        }
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public int getAvailableCount() {
        return semaphore.availablePermits();
    }

    public Channel getConnection(String address) {
        return this.channelMap.get(address);
    }

    private String toKey(Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        return remoteAddress.getHostName() + ":" + remoteAddress.getPort();
    }

    public void onFailed(ChannelHandlerContext ctx, Throwable t) {
        ctx.channel().close();
    }
}
