package org.venus.core;

import io.netty.buffer.*;
import org.venus.Response;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse implements Response, Serializable {

    private static final long serialVersionUID = -8746059598052924249L;

    private final String protocol;
    private final int code;
    private final String reason;
    private final Map<String, String> headers;
    private final ByteBuf body;

    private transient String content;// for cache

    private HttpResponse(String protocol, int code, String reason, Map<String, String> headers, ByteBuf body) {
        this.protocol = protocol;
        this.code = code;
        this.reason = reason;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    @Override
    public String protocol() {
        return this.protocol;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String reason() {
        return this.reason;
    }

    @Override
    public Map<String, String> headers() {
        return this.headers;
    }

    @Override
    public ByteBuf body() {
        return this.body;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResponse that = (HttpResponse) o;
        return code == that.code && Objects.equals(protocol, that.protocol) && Objects.equals(reason, that.reason) && Objects.equals(headers, that.headers) && ByteBufUtil.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(protocol, code, reason, headers);
        result = 31 * result + ByteBufUtil.hashCode(body);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder resp = new StringBuilder();
        resp.append(protocol).append(' ').append(code).append(' ').append(reason).append("\r\n");
        if (headers != null) {
            headers.forEach((k, v) -> resp.append(k).append(" : ").append(v).append("\r\n"));
        }
        return resp.toString();
    }

    public static class HttpResponseBuilder {
        private String protocol = "HTTP/1.1";
        private int code = 200;
        private String reason = "OK";
        private Map<String, String> headers = new HashMap<>();
        private ByteBuf body;

        public HttpResponseBuilder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public HttpResponseBuilder code(int code) {
            this.code = code;
            return this;
        }

        public HttpResponseBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public HttpResponseBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public HttpResponseBuilder addHeader(String key, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(key, value);
            return this;
        }

        public HttpResponseBuilder utf8Body(String body) {
            return this.body(body, StandardCharsets.UTF_8);
        }

        public HttpResponseBuilder body(String body, String charset) {
            return this.body(body, Charset.forName(charset));
        }

        public HttpResponseBuilder body(String body, Charset charset) {
            return this.body(body.getBytes(charset));
        }

        public HttpResponseBuilder body(ByteBuf body) {
            this.body = body;
            return this;
        }

        public HttpResponseBuilder body(byte[] body) {
            return this.body(Unpooled.wrappedBuffer(body));
        }

        public HttpResponse build() {
            this.body = this.body == null ? new EmptyByteBuf(PooledByteBufAllocator.DEFAULT) : this.body;
            return new HttpResponse(protocol, code, reason, headers, body);
        }
    }
}
