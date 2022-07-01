package org.venus.core;

import org.venus.*;
import org.venus.choosers.ProtocolChooser;
import org.venus.utils.Futures;
import org.venus.valves.ValveBase;

import java.util.concurrent.CompletableFuture;

public class StandardHostStreamValve extends ValveBase {

    private final Chooser chooser;

    public StandardHostStreamValve(Container container) {
        setContainer(container);
        this.chooser = new ProtocolChooser();
    }

    @Override
    public CompletableFuture<Response> invoke(Request req, CompletableFuture<Response> ignore) {
        CompletableFuture<Response> res = new CompletableFuture<>();
        try (ClientConnector client = chooser.choose(req).create()) {
            client.invokeAsync(req)
                    .whenComplete((resp, err) -> {
                        if (err != null) {
                            res.completeExceptionally(err);
                        } else {
                            getContainer()
                                    .getNext() // out
                                    .getPipeline()
                                    .getFirst()
                                    .invoke(req, CompletableFuture.completedFuture(resp))
                                    .whenComplete((r, e) -> {
                                        if (e != null) {
                                            res.completeExceptionally(e);
                                        } else {
                                            res.complete(r);
                                        }
                                    });
                        }
                    });
        } catch (Exception err) {
            res.completeExceptionally(err);
        }
        return res;
    }
}
