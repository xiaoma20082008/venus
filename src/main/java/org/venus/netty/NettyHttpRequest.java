package org.venus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import org.venus.HttpRequestBase;
import org.venus.SessionContext;

import java.util.Map;
import java.util.stream.Collectors;

public final class NettyHttpRequest extends HttpRequestBase {

    private final FullHttpRequest request;

    private transient Map<String, String> headers;

    public NettyHttpRequest(FullHttpRequest request, SessionContext context) {
        super(context);
        this.request = request;
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

    public static NettyRequestBuilder builder() {
        return new NettyRequestBuilder();
    }

    public static class NettyRequestBuilder {
    }
}
