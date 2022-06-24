package org.venus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import org.venus.Request;
import org.venus.SessionContext;

import java.util.List;
import java.util.Map;

public class NettyRequest implements Request {

    private final FullHttpRequest request;
    private final SessionContext sessionContext;

    public NettyRequest(FullHttpRequest request, SessionContext sessionContext) {
        this.request = request;
        this.sessionContext = sessionContext;
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
    public String path() {
        return null;
    }

    @Override
    public Map<String, List<String>> parameters() {
        return null;
    }

    @Override
    public String version() {
        return this.request.protocolVersion().text();
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
    public SessionContext context() {
        return null;
    }

    @Override
    public void putAttr(String key, Object attr) {

    }

    @Override
    public Object getAttr(String key) {
        return null;
    }

    @Override
    public String content() {
        return null;
    }
}
