package org.venus.protocols.dubbo;

import org.venus.ClientConnectorBase;
import org.venus.Request;
import org.venus.Response;

import java.util.concurrent.CompletableFuture;

public class DubboClientConnector extends ClientConnectorBase {

    public DubboClientConnector() {
    }

    @Override
    public CompletableFuture<Response> invokeAsync(Request request) {
        return null;
    }
}
