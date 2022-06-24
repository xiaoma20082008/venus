package org.venus.netty;

import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;

public class NettyResponseSender extends ChannelOutboundHandlerAdapter {

    private final EventLoopGroup executor;

    public NettyResponseSender(EventLoopGroup executor) {
        this.executor = executor;
    }

}
