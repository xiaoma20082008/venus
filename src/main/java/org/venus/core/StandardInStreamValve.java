package org.venus.core;

import org.venus.Container;
import org.venus.FilterInbound;
import org.venus.Request;
import org.venus.Response;
import org.venus.valves.ValveBase;

import java.util.concurrent.CompletableFuture;

public class StandardInStreamValve extends ValveBase {

    private FilterInbound inbound;

    public StandardInStreamValve(Container container) {
        setContainer(container);
    }

    @Override
    public CompletableFuture<Response> invoke(Request req, CompletableFuture<Response> resp) {
        return getContainer()
                .getNext() // == Host
                .getPipeline()
                .getFirst()
                .invoke(req, resp);
    }

}
