package org.venus.filters.resp;

import org.venus.FilterOutbound;
import org.venus.Response;

import java.util.concurrent.CompletableFuture;

public final class GZipFilter extends FilterOutbound {

    @Override
    public CompletableFuture<Response> filterAsync(CompletableFuture<Response> future) {
        if (next() != null) {
            return next().filterAsync(future);
        }
        return future;
    }
}
