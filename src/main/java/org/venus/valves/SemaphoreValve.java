package org.venus.valves;

import org.venus.Request;
import org.venus.Response;

import java.util.concurrent.CompletableFuture;

public class SemaphoreValve extends ValveBase {

    @Override
    public CompletableFuture<Response> invoke(Request req, CompletableFuture<Response> resp) {
        return resp;
    }

}
