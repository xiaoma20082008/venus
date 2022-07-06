package org.venus.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.venus.Connection;
import org.venus.ConnectionManager;
import org.venus.SessionContext;

import java.nio.charset.StandardCharsets;

public class NettyConnectionHandler extends ChannelInboundHandlerAdapter {

    private final ConnectionManager manager;

    public NettyConnectionHandler(ConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 可能channel已经关了
        if (!ctx.channel().isActive()) {
            return;
        }
        // connected
        if (this.manager.canAcquire()) {
            NettyConnection connection = new NettyConnection(ctx.channel());
            ctx.channel().attr(SessionContext.ATTR_CONNECTION_KEY).set(connection);
            this.manager.onConnected(connection);
            super.channelActive(ctx);
        } else {
            ctx.writeAndFlush(createErrorResponse()).addListener(f -> ctx.channel().close());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // disconnected
        Connection connection = ctx.channel().attr(SessionContext.ATTR_CONNECTION_KEY).getAndSet(null);
        this.manager.onClosed(connection);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // error
        this.manager.onFailed(ctx, cause);
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
