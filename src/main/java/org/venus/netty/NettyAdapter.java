package org.venus.netty;

import io.netty.handler.codec.http.FullHttpResponse;
import org.venus.Adapter;
import org.venus.Request;
import org.venus.Response;
import org.venus.ServerConnector;

import java.util.concurrent.CompletableFuture;

public class NettyAdapter implements
        Adapter<io.netty.handler.codec.http.FullHttpRequest, io.netty.handler.codec.http.FullHttpResponse> {

    private final NettyFrontendAdapter fa;
    private final NettyBackendAdapter ba;
    private final ServerConnector connector;

    public NettyAdapter(ServerConnector connector) {
        this.fa = new NettyFrontendAdapter();
        this.ba = new NettyBackendAdapter();
        this.connector = connector;
    }

    @Override
    public CompletableFuture<io.netty.handler.codec.http.FullHttpResponse> serviceAsync(io.netty.handler.codec.http.FullHttpRequest request, Object ext) {
        CompletableFuture<io.netty.handler.codec.http.FullHttpResponse> res = new CompletableFuture<>();
        Request req = this.fa.service(request, ext);
        Response resp = this.connector.getContainer()
                .getPipeline()
                .getFirst()
                .invoke(req);
        FullHttpResponse response = this.ba.service(resp, ext);
        res.complete(response);
        return res;
    }

}
