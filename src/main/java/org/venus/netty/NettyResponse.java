package org.venus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import org.venus.Response;

import java.util.Map;
import java.util.stream.Collectors;

public class NettyResponse implements Response {

    private final FullHttpResponse response;

    public NettyResponse(FullHttpResponse response) {
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

    @Override
    public String content() {
        return null;
    }
}
