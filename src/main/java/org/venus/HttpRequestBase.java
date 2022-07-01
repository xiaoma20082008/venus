package org.venus;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class HttpRequestBase implements Request {

    protected final SessionContext context;

    private transient String path;// for cache
    private transient String content;// for cache
    private transient Map<String, List<String>> parameters;// for cache

    protected HttpRequestBase(SessionContext context) {
        this.context = Objects.requireNonNull(context, "context is null");
    }

    @Override
    public final Object getAttr(String key) {
        return this.context.get(key);
    }

    @Override
    public final void putAttr(String key, Object attr) {
        this.context.put(key, attr);
    }

    @Override
    public final String path() {
        if (this.path == null) {
            QueryStringDecoder decoder = new QueryStringDecoder(this.uri());
            this.path = decoder.path();
            this.parameters = decoder.parameters();
        }
        return this.path;
    }

    @Override
    public final Map<String, List<String>> parameters() {
        if (this.parameters == null) {
            QueryStringDecoder decoder = new QueryStringDecoder(this.uri());
            this.path = decoder.path();
            this.parameters = decoder.parameters();
        }
        return this.parameters;
    }

    @Override
    public final String content() {
        if (this.content == null) {
            byte[] bytes = new byte[this.body().readableBytes()];
            this.body().getBytes(this.body().readerIndex(), bytes);
            this.content = new String(bytes);
        }
        return this.content;
    }

    @Override
    public final SessionContext context() {
        return this.context;
    }

    @Override
    public final String toString() {
        StringBuilder req = new StringBuilder();
        req.append(method()).append(' ');
        req.append(uri()).append(' ');
        req.append(version()).append("\r\n");
        if (headers() != null) {
            headers().forEach((k, v) -> req.append(k).append(" : ").append(v).append("\r\n"));
        }
        req.append("\r\n");
        req.append(content());
        return req.toString();
    }
}
