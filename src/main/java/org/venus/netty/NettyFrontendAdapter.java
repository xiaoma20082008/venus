package org.venus.netty;

import org.venus.Adapter;
import org.venus.Connection;
import org.venus.Request;

import java.util.concurrent.CompletableFuture;

public class NettyFrontendAdapter implements Adapter<io.netty.handler.codec.http.FullHttpRequest, Request> {

    @Override
    public CompletableFuture<Request> serviceAsync(io.netty.handler.codec.http.FullHttpRequest request, Object ext) {
        assert ext instanceof Connection;
        return CompletableFuture.completedFuture(new NettyHttpRequest(request, (Connection) ext));
    }

}
