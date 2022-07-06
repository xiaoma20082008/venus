package org.venus.core;

import org.venus.Container;
import org.venus.FilterInbound;
import org.venus.Request;
import org.venus.Response;
import org.venus.filters.req.AuthFilter;
import org.venus.filters.req.MockReqFilter;
import org.venus.filters.req.RouteFilter;
import org.venus.filters.req.SignFilter;
import org.venus.valves.ValveBase;

import java.util.concurrent.CompletableFuture;

public class StandardInStreamValve extends ValveBase {

    private final FilterInbound inbound;

    public StandardInStreamValve(Container container) {
        setContainer(container);
        this.inbound = buildInboundFilter();
    }

    private static FilterInbound buildInboundFilter() {
        return (FilterInbound) new AuthFilter()
                .next(new SignFilter())
                .next(new RouteFilter())
                .next(new MockReqFilter());
    }

    @Override
    public CompletableFuture<Response> invoke(Request req, CompletableFuture<Response> ignore) {
        req = this.inbound.filter(req);
        return getContainer()
                .getNext() // == Host
                .getPipeline()
                .getFirst()
                .invoke(req);
    }
}
