package org.venus.core;

import org.venus.Container;
import org.venus.FilterOutbound;
import org.venus.Request;
import org.venus.Response;
import org.venus.valves.ValveBase;

import java.util.concurrent.CompletableFuture;

public class StandardOutStreamValve extends ValveBase {

    private FilterOutbound out;

    public StandardOutStreamValve(Container container) {
        setContainer(container);
    }

    @Override
    public CompletableFuture<Response> invoke(Request req, CompletableFuture<Response> resp) {
        return resp;
    }
}
