package org.venus;

import java.util.concurrent.CompletableFuture;

public interface Valve {

    Valve getNext();

    void setNext(Valve next);

    default Response invoke(Request request) {
        return invokeAsync(request).join();
    }

    default Response invoke(Request request, Response response) {
        return invokeAsync(request, CompletableFuture.completedFuture(response)).join();
    }

    default CompletableFuture<Response> invokeAsync(Request request) {
        return invokeAsync(request, new CompletableFuture<>());
    }

    CompletableFuture<Response> invokeAsync(Request req, CompletableFuture<Response> resp);
}
