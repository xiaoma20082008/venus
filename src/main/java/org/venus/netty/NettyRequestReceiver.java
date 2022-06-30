package org.venus.netty;

import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.SessionContext;

public class NettyRequestReceiver extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRequestReceiver.class);

    private final NettyAdapter adapter;
    private final EventLoopGroup executor;

    public NettyRequestReceiver(NettyAdapter adapter, EventLoopGroup executor) {
        this.adapter = adapter;
        this.executor = executor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null || msg == Unpooled.EMPTY_BUFFER || msg instanceof EmptyByteBuf) {
            return;
        } else if (msg instanceof FullHttpRequest req) {
            // 请求行+请求头
            if (req.content().readableBytes() > 0) {
                doService(ctx, req);
            } else {
                ReferenceCountUtil.release(msg);
            }
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    private void doService(ChannelHandlerContext ctx, FullHttpRequest request) {
        long start = System.currentTimeMillis();
        try {
            SessionContext sc = ctx.channel().attr(SessionContext.ATTR_SESSION_KEY).get();

            this.adapter.service(request, sc)
                    .whenCompleteAsync(
                            (response, err) -> {
                                if (err != null) {
                                    ctx.close();
                                    return;
                                }
                                long end = System.currentTimeMillis();
                                ctx.writeAndFlush(response)
                                        .addListener(future ->
                                                onResponseComplete(ctx, request, response, future.cause(), start, end));
                            },
                            this.executor);
        } catch (Throwable e) {
            ctx.fireExceptionCaught(e);
        }
    }

    private void onResponseComplete(ChannelHandlerContext ctx,
                                    FullHttpRequest request, FullHttpResponse response,
                                    Throwable error, long start, long end) {
        long u = PlatformDependent.usedDirectMemory();
        try {
            if (error != null) {
                ctx.fireExceptionCaught(error);
            }
        } finally {
            int rc = request.refCnt();
            int sc = response.refCnt();
            ReferenceCountUtil.release(request);    // ref--
            ReferenceCountUtil.release(response);   // ref--

            long n = PlatformDependent.usedDirectMemory();
            long m = PlatformDependent.maxDirectMemory();

            if (error == null) {
                LOGGER.info("## write response success. total cost {}ms, process cost {}ms, write response cost {}ms. Direct memory: use/now/max = {}/{}/{} bytes. rc={},sc={}",
                        System.currentTimeMillis() - start, (end - start), (System.currentTimeMillis() - end), u, n, m, rc, sc);
            } else {
                LOGGER.info("## write response failed. total cost {}ms, process cost {}ms, write response cost {}ms. Direct memory: use/now/max = {}/{}/{} bytes. rc={},sc={} caused by:{}",
                        System.currentTimeMillis() - start, (end - start), (System.currentTimeMillis() - end), u, n, m, rc, sc, error);
            }
        }
    }

}
