package org.venus.core;

import org.venus.*;
import org.venus.protocols.http.HttpProtocol;
import org.venus.valves.ValveBase;

import java.util.concurrent.CompletableFuture;

public class StandardHostStreamValve extends ValveBase {

    private final Chooser chooser;

    public StandardHostStreamValve(Container container) {
        setContainer(container);
        this.chooser = request -> new HttpProtocol();
    }

    @Override
    public CompletableFuture<Response> invoke(Request req, CompletableFuture<Response> resp) {
        Protocol protocol = chooser.choose(req);
        ClientConnector client = null;
        try {
            client = protocol.create();
            CompletableFuture<Response> future = client.invokeAsync(req);
            return getContainer()
                    .getNext() // out
                    .getPipeline()
                    .getFirst()
                    .invoke(req, future);
        } finally {
            protocol.release(client);
        }
    }
}
