package org.venus.protocols.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.venus.Request;
import org.venus.Response;
import org.venus.netty.NettyResponse;
import org.venus.protocols.ClientConnectorBase;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class HttpClientConnector extends ClientConnectorBase {

    public HttpClientConnector() {
    }

    @Override
    public CompletableFuture<Response> invokeAsync(Request request) {
        CompletableFuture<Response> res = new CompletableFuture<>();
        Response response = mockResponse(request);
        res.complete(response);
        return res;
    }

    private Response mockResponse(Request request) {
        // 模拟后端请求
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        byte[] body = gen1_5kBytes();
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(body.length);
        buf.writeBytes(body);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                buf
        );
        buf.retain(); // ref++
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
        return new NettyResponse(response);
    }

    private byte[] gen1_5kBytes() {
        int len = 1536;
        byte[] bytes = new byte[len];
        Arrays.fill(bytes, (byte) 'a');
        return bytes;
    }
}
