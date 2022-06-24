package org.venus.utils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Pools {

    private final Map<String /* protocol */, FixedChannelPool> pools = new HashMap<>();

    public void connect(long timeout, TimeUnit unit) throws Exception {
        try (FixedChannelPool pool = new FixedChannelPool(
                new Bootstrap(),
                new AbstractChannelPoolHandler() {
                    @Override
                    public void channelCreated(Channel ch) throws Exception {
                        ch.pipeline().addLast();
                    }
                },
                ChannelHealthChecker.ACTIVE,
                FixedChannelPool.AcquireTimeoutAction.FAIL,
                10L, 10, 10, true);) {
            Future<Channel> future = pool.acquire();
            pool.release(future.get(timeout, unit));
        }
    }

}
