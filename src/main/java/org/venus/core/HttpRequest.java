package org.venus.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.venus.Request;
import org.venus.SessionContext;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class HttpRequest implements Request, Serializable {

    private static final long serialVersionUID = -8645635495081191668L;

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers;
    private final ByteBuf body;
    private final SessionContext context;

    private transient String path;// for cache
    private transient String content;// for cache
    private transient Map<String, List<String>> parameters;// for cache

    private HttpRequest(String m, String uri, String v, Map<String, String> headers, ByteBuf buf, SessionContext ctx) {
        this.method = m;
        this.uri = uri;
        this.version = v;
        this.headers = headers;
        this.body = buf;
        this.context = ctx;
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public String path() {
        if (this.path == null) {
            QueryStringDecoder decoder = new QueryStringDecoder(this.uri);
            this.path = decoder.path();
            this.parameters = decoder.parameters();
        }
        return this.path;
    }

    @Override
    public Map<String, List<String>> parameters() {
        if (this.parameters == null) {
            QueryStringDecoder decoder = new QueryStringDecoder(this.uri);
            this.path = decoder.path();
            this.parameters = decoder.parameters();
        }
        return this.parameters;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public ByteBuf body() {
        return body;
    }

    @Override
    public SessionContext context() {
        return this.context;
    }

    @Override
    public String content() {
        if (this.content == null) {
            byte[] bytes = new byte[this.body.readableBytes()];
            this.body.getBytes(this.body.readerIndex(), bytes);
            this.content = new String(bytes);
        }
        return this.content;
    }

    @Override
    public Object getAttr(String key) {
        return this.context.get(key);
    }

    @Override
    public void putAttr(String key, Object attr) {
        this.context.put(key, attr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(method, that.method) && Objects.equals(uri, that.uri) && Objects.equals(version, that.version) && Objects.equals(headers, that.headers) && ByteBufUtil.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method, uri, version, headers);
        result = 31 * result + ByteBufUtil.hashCode(body);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder req = new StringBuilder();
        req.append(method).append(' ');
        req.append(uri).append(' ');
        req.append(version).append("\r\n");
        if (headers != null) {
            headers.forEach((k, v) -> req.append(k).append(" : ").append(v).append("\r\n"));
        }
        return req.toString();
    }

    public static class HttpRequestBuilder {

        private String method = "GET";
        private String uri;
        private String version = "HTTP/1.1";
        private Map<String, String> headers = new HashMap<>();
        private ByteBuf body;
        private SessionContext context = new SessionContext();

        public HttpRequestBuilder method(String method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public HttpRequestBuilder version(String version) {
            this.version = version;
            return this;
        }

        public HttpRequestBuilder headers(Map<String, String> headers) {
            this.headers = new HashMap<>(requireNonNull(headers, "headers is null"));
            return this;
        }

        public HttpRequestBuilder addHeader(String key, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(key, value);
            return this;
        }

        public HttpRequestBuilder addHeaders(Map<String, String> map) {
            map.forEach(this::addHeader);
            return this;
        }

        public HttpRequestBuilder addHeaders(List<Map.Entry<String, String>> entries) {
            entries.forEach(e -> addHeader(e.getKey(), e.getValue()));
            return this;
        }

        public HttpRequestBuilder utf8Body(String body) {
            return this.body(body.getBytes(StandardCharsets.UTF_8));
        }

        public HttpRequestBuilder body(String body, Charset charset) {
            this.body = Unpooled.wrappedBuffer(body.getBytes(charset));
            return this;
        }

        public HttpRequestBuilder body(byte[] body) {
            this.body = Unpooled.wrappedBuffer(body);
            return this;
        }

        public HttpRequestBuilder body(ByteBuf body) {
            this.body = body;
            body.retain(); // ref++
            return this;
        }

        public HttpRequestBuilder context(SessionContext context) {
            this.context = context;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(
                    requireNonNull(method, "method is null"),
                    requireNonNull(uri, "uri is null"),
                    requireNonNull(version, "version is null"),
                    requireNonNull(headers, "headers is null"),
                    body,
                    requireNonNull(context, "context is null")
            );
        }
    }

}
