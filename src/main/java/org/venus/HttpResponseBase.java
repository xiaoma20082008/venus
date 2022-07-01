package org.venus;

public abstract class HttpResponseBase implements Response {

    protected transient String content;// for cache

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
    public final String toString() {
        StringBuilder resp = new StringBuilder();
        resp.append(protocol()).append(' ').append(code()).append(' ').append(reason()).append("\r\n");
        if (headers() != null) {
            headers().forEach((k, v) -> resp.append(k).append(" : ").append(v).append("\r\n"));
        }
        resp.append("\r\n");
        resp.append(content());
        return resp.toString();
    }
}
