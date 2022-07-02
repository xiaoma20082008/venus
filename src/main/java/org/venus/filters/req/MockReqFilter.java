package org.venus.filters.req;

import org.venus.FilterInbound;
import org.venus.Request;

import java.util.concurrent.CompletableFuture;

public final class MockReqFilter extends FilterInbound {

    @Override
    public CompletableFuture<Request> filterAsync(CompletableFuture<Request> future) {
        if (next() != null) {
            return next().filterAsync(future);
        }
        return future;
    }

}
