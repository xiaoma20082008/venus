package org.venus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import org.venus.Response;

import java.util.Map;

public class NettyResponse implements Response {

    private final FullHttpResponse response;

    public NettyResponse(FullHttpResponse response) {
        this.response = response;
    }

    @Override
    public String protocol() {
        return null;
    }

    @Override
    public int code() {
        return 0;
    }

    @Override
    public String reason() {
        return null;
    }

    @Override
    public Map<String, String> headers() {
        return null;
    }

    @Override
    public ByteBuf body() {
        return null;
    }

    @Override
    public String content() {
        return null;
    }
}
