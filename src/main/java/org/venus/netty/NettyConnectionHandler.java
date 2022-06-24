package org.venus.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class NettyConnectionHandler extends ChannelInboundHandlerAdapter {

    private final NettyConnectionManager manager;

    public NettyConnectionHandler(NettyConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 可能channel已经关了
        if (!ctx.channel().isActive()) {
            return;
        }
        // connected
        if (manager.onConnected(ctx)) {
            super.channelActive(ctx);
        } else {
            ctx.writeAndFlush(createErrorResponse());
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // disconnected
        manager.onClosed(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // error
        manager.onFailed(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    private FullHttpResponse createErrorResponse() {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(("Too many connections.Max=" + manager.getMaxConnections()).getBytes(StandardCharsets.UTF_8))
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
        return response;
    }
}
