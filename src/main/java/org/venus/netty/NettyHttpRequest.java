package org.venus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import org.venus.Connection;
import org.venus.HttpRequestBase;

import java.util.Map;
import java.util.stream.Collectors;

public final class NettyHttpRequest extends HttpRequestBase {

    private transient final FullHttpRequest request;

    private transient Map<String, String> headers;

    public NettyHttpRequest(FullHttpRequest request, Connection connection) {
        super(connection);
        this.request = request;
    }

    public static NettyRequestBuilder builder() {
        return new NettyRequestBuilder();
    }

    @Override
    public String method() {
        return this.request.method().name();
    }

    @Override
    public String uri() {
        return this.request.uri();
    }

    @Override
    public String version() {
        return this.request.protocolVersion().text();
    }

    @Override
    public Map<String, String> headers() {
        if (this.headers == null) {
            this.headers = this.request.headers()
                    .entries()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return this.headers;
    }

    @Override
    public ByteBuf body() {
        return this.request.content();
    }

    public FullHttpRequest getSourceRequest() {
        return request;
    }

    public static class NettyRequestBuilder {
    }
}
