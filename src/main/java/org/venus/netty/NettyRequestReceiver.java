package org.venus.netty;

import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.Connection;
import org.venus.SessionContext;

public class NettyRequestReceiver extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRequestReceiver.class);

    private final NettyAdapter adapter;

    public NettyRequestReceiver(NettyAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null || msg == Unpooled.EMPTY_BUFFER || msg instanceof EmptyByteBuf) {
            ctx.close();
        } else if (msg instanceof FullHttpRequest req) {
            doService(ctx, req);
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    private void doService(ChannelHandlerContext ctx, FullHttpRequest request) {
        long start = System.currentTimeMillis();
        try {
            Connection connection = ctx.channel().attr(SessionContext.ATTR_CONNECTION_KEY).get();
            FullHttpResponse response = this.adapter.service(request, connection);
            long end = System.currentTimeMillis();
            ctx.writeAndFlush(response).addListener(future -> onResponseComplete(ctx, request, response, future.cause(), start, end));
        } catch (Throwable e) {
            FullHttpResponse response = NettyHttpResponse.error(e).getTargetResponse();
            long end = System.currentTimeMillis();
            ctx.writeAndFlush(response).addListener(future -> onResponseComplete(ctx, request, response, e, start, end));
        }
    }

    private void onResponseComplete(ChannelHandlerContext ctx,
                                    FullHttpRequest request, FullHttpResponse response,
                                    Throwable error, long start, long end) {
        int rc = request.refCnt();
        int sc = response.refCnt();
        assert rc == 1;
        assert sc == 1;

        long u = PlatformDependent.usedDirectMemory();
        try {
            if (error != null) {
                ctx.fireExceptionCaught(error);
            }
        } finally {

            ReferenceCountUtil.release(request);    // ref--
            ReferenceCountUtil.release(response);   // ref--
        }

        long n = PlatformDependent.usedDirectMemory();
        long m = PlatformDependent.maxDirectMemory();

        if (error == null) {
            LOGGER.info("##Total {}ms, process {}ms, write {}ms. Direct memory: use/now/max = {}/{}/{} bytes. rc={},sc={}",
                    System.currentTimeMillis() - start, (end - start), (System.currentTimeMillis() - end), u, n, m, rc, sc);
        } else {
            LOGGER.info("##Total {}ms, process {}ms, write {}ms. Direct memory: use/now/max = {}/{}/{} bytes. rc={},sc={} caused by:{}",
                    System.currentTimeMillis() - start, (end - start), (System.currentTimeMillis() - end), u, n, m, rc, sc, error);
        }
    }

}
