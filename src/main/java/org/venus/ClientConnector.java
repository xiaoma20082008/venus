package org.venus;

import java.util.concurrent.CompletableFuture;

public interface ClientConnector extends Lifecycle {

    Response invoke(Request request);

    CompletableFuture<Response> invokeAsync(Request request);
}
