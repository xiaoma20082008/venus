package org.venus.netty;

import org.venus.Adapter;
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
    public CompletableFuture<io.netty.handler.codec.http.FullHttpResponse> service(io.netty.handler.codec.http.FullHttpRequest request, Object ext) {
        CompletableFuture<io.netty.handler.codec.http.FullHttpResponse> res = new CompletableFuture<>();
        this.fa.service(request, ext)
                .whenComplete((req, reqError) -> {
                    if (reqError != null) {
                        res.completeExceptionally(reqError);
                    } else {
                        connector.getContainer()
                                .getPipeline()
                                .getFirst()
                                .invoke(req)
                                .whenComplete((resp, err) -> {
                                    if (err != null) {
                                        res.completeExceptionally(err);
                                    } else {
                                        this.ba.service(resp, ext)
                                                .whenComplete(((response, exception) -> {
                                                    if (exception != null) {
                                                        res.completeExceptionally(exception);
                                                    } else {
                                                        res.complete(response);
                                                    }
                                                }));
                                    }
                                });
                    }
                });
        return res;
    }

}
