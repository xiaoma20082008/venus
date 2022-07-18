package org.venus.core;

import org.venus.*;
import org.venus.choosers.ProtocolChooser;
import org.venus.valves.ValveBase;

import java.util.concurrent.CompletableFuture;

public class StandardHostStreamValve extends ValveBase {

    private final Chooser chooser;

    public StandardHostStreamValve(Container container) {
        setContainer(container);
        this.chooser = new ProtocolChooser();
    }

    @Override
    public CompletableFuture<Response> invokeAsync(Request req, CompletableFuture<Response> ignore) {
        CompletableFuture<Response> res = new CompletableFuture<>();
        try (ClientConnector client = chooser.choose(req).create()) {
            getContainer()
                    .getNext() // out
                    .getPipeline()
                    .getFirst()
                    .invokeAsync(req, client.invokeAsync(req))
                    .whenComplete((r, e) -> {
                        if (e != null) {
                            res.completeExceptionally(e);
                        } else {
                            res.complete(r);
                        }
                    });
        } catch (Exception err) {
            res.completeExceptionally(err);
        }
        return res;
    }
}
