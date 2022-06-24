package org.venus.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

public class NettyFrontendHandler extends ChannelInboundHandlerAdapter {

    private Channel outbound;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inbound = ctx.channel();
        ChannelFuture f = new Bootstrap().group(inbound.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new NettyBackendHandler(inbound))
                .option(ChannelOption.AUTO_READ, false)
                .connect("", 1111);
        this.outbound = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    inbound.read();
                } else {
                    inbound.close();
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.outbound.isActive()) {
            outbound.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        }
    }

}
