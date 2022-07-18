package org.venus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.*;
import org.venus.HttpResponseBase;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public final class NettyHttpResponse extends HttpResponseBase {

    private final FullHttpResponse response;

    public NettyHttpResponse(FullHttpResponse response) {
        this.response = response;
    }

    @Override
    public String protocol() {
        return this.response.protocolVersion().text();
    }

    @Override
    public int code() {
        return this.response.status().code();
    }

    @Override
    public String reason() {
        return this.response.status().reasonPhrase();
    }

    @Override
    public Map<String, String> headers() {
        return this.response.headers()
                .entries()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public ByteBuf body() {
        return this.response.content();
    }

    public FullHttpResponse getTargetResponse() {
        return this.response;
    }

    public static NettyHttpResponse error(Throwable t) {
        var body = """
                {
                    "code":500,
                    "error":"%s"
                }
                """;
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
        buf.writeCharSequence(String.format(body, t.getMessage()), StandardCharsets.UTF_8);
        DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        resp.headers().set(HttpHeaderNames.CONTENT_LENGTH, resp.content().readableBytes());
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
        return new NettyHttpResponse(resp);
    }
}
