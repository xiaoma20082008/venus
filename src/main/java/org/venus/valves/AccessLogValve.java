package org.venus.valves;

import org.venus.AccessLog;
import org.venus.Request;
import org.venus.Response;

import java.util.concurrent.CompletableFuture;

public class AccessLogValve extends ValveBase {

    private AccessLog log;

    @Override
    public CompletableFuture<Response> invokeAsync(Request req, CompletableFuture<Response> resp) {
        return getNext().invokeAsync(req, resp);
    }

}
