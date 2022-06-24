package org.venus.netty;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.venus.Adapter;
import org.venus.Response;

import java.util.concurrent.CompletableFuture;

public class NettyBackendAdapter implements Adapter<Response, io.netty.handler.codec.http.FullHttpResponse> {

    @Override
    public CompletableFuture<io.netty.handler.codec.http.FullHttpResponse> service(Response resp, Object ext) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.valueOf(resp.protocol()),
                HttpResponseStatus.valueOf(resp.code()),
                resp.body()
        );
        resp.body().retain(); // ref++
        resp.headers().forEach((k, v) -> response.headers().set(k, v));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
        return CompletableFuture.completedFuture(response);
    }

}
