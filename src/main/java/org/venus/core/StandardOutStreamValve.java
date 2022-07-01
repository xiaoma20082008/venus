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

    @Override
    public CompletableFuture<Response> invoke(Request req, CompletableFuture<Response> resp) {
        CompletableFuture<Response> res = new CompletableFuture<>();
        return resp.whenComplete(((response, exception) -> {
            if (exception != null) {
                res.completeExceptionally(exception);
            } else {
                res.complete(this.out.filter(response));
            }
        }));
    }

    private static FilterOutbound buildOutboundFilter() {
        return (FilterOutbound) new GZipFilter().next(new MockRespFilter());
    }

}
