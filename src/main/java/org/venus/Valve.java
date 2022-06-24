package org.venus;

import java.util.concurrent.CompletableFuture;

public interface Valve {

    Valve getNext();

    void setNext(Valve next);

    default CompletableFuture<Response> invoke(Request request) {
        return invoke(request, new CompletableFuture<>());
    }

    CompletableFuture<Response> invoke(Request req, CompletableFuture<Response> resp);
}
