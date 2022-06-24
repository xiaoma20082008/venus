package org.venus.netty;

import io.netty.channel.*;

public class NettyBackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel inbound;

    public NettyBackendHandler(Channel inbound) {
        this.inbound = inbound;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        inbound.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            }
        });
    }

}
