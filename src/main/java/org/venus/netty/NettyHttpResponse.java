package org.venus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import org.venus.HttpResponseBase;

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

}
