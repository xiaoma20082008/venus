package org.venus.filters.req;

import org.venus.FilterInbound;
import org.venus.Request;
import org.venus.SessionContext;
import org.venus.protocols.http.HttpProtocol;

import java.util.concurrent.CompletableFuture;

public final class RouteFilter extends FilterInbound {

    @Override
    public CompletableFuture<Request> filterAsync(CompletableFuture<Request> future) {
        if (next() != null) {
            return next().filterAsync(mockRouteRequest(future));
        }
        return future;
    }

    private CompletableFuture<Request> mockRouteRequest(CompletableFuture<Request> future) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignore) {
        }
        CompletableFuture<Request> res = new CompletableFuture<>();
        future.whenComplete((req, err) -> {
            if (err != null) {
                res.completeExceptionally(err);
            } else {
                req.context().put(SessionContext.PROTOCOL_KEY, HttpProtocol.NAME);
                res.complete(req);
            }
        });
        return res;
    }
}
