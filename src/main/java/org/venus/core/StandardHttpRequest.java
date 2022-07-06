package org.venus.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.venus.Connection;
import org.venus.HttpRequestBase;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class StandardHttpRequest extends HttpRequestBase {

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers;
    private final ByteBuf body;

    private StandardHttpRequest(String m, String uri, String v, Map<String, String> h, ByteBuf buf, Connection c) {
        super(c);
        this.method = m;
        this.uri = uri;
        this.version = v;
        this.headers = h;
        this.body = buf;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardHttpRequest that = (StandardHttpRequest) o;
        return Objects.equals(method, that.method) && Objects.equals(uri, that.uri) && Objects.equals(version, that.version) && Objects.equals(headers, that.headers) && ByteBufUtil.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method, uri, version, headers);
        result = 31 * result + ByteBufUtil.hashCode(body);
        return result;
    }

    public static class HttpRequestBuilder {

        private String method = "GET";
        private String uri;
        private String version = "HTTP/1.1";
        private Map<String, String> headers = new HashMap<>();
        private ByteBuf body;
        private Connection connection;

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

        public HttpRequestBuilder connection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public StandardHttpRequest build() {
            return new StandardHttpRequest(
                    requireNonNull(method, "method is null"),
                    requireNonNull(uri, "uri is null"),
                    requireNonNull(version, "version is null"),
                    requireNonNull(headers, "headers is null"),
                    body,
                    requireNonNull(connection, "connection is null")
            );
        }
    }

}
