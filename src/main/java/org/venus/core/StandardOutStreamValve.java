package org.venus.core;

import org.venus.Container;
import org.venus.FilterOutbound;
import org.venus.Request;
import org.venus.Response;
import org.venus.filters.resp.GZipFilter;
import org.venus.filters.resp.MockRespFilter;
import org.venus.valves.ValveBase;

import java.util.concurrent.CompletableFuture;

public class StandardOutStreamValve extends ValveBase {

    private final FilterOutbound out;

    public StandardOutStreamValve(Container container) {
        setContainer(container);
        this.out = buildOutboundFilter();
    }

    private static FilterOutbound buildOutboundFilter() {
        return (FilterOutbound) new GZipFilter().next(new MockRespFilter());
    }

    @Override
    public CompletableFuture<Response> invokeAsync(Request req, CompletableFuture<Response> resp) {
        CompletableFuture<Response> res = new CompletableFuture<>();
        resp.whenComplete(((response, exception) -> {
            if (exception != null) {
                res.completeExceptionally(exception);
            } else {
                this.out.filterAsync(response).whenComplete((r, e) -> {
                    if (e != null) {
                        res.completeExceptionally(e);
                    } else {
                        res.complete(r);
                    }
                });
            }
        }));
        return res;
    }

}
