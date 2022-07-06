package org.venus.netty;

import org.venus.Adapter;
import org.venus.Connection;
import org.venus.Request;
import org.venus.SessionContext;

import java.util.concurrent.CompletableFuture;

public class NettyFrontendAdapter implements Adapter<io.netty.handler.codec.http.FullHttpRequest, Request> {

    @Override
    public CompletableFuture<Request> service(io.netty.handler.codec.http.FullHttpRequest request, Object ext) {
        assert ext instanceof SessionContext;
        return CompletableFuture.completedFuture(new NettyHttpRequest(request, (Connection) ext));
    }

}
