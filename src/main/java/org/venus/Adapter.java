package org.venus;

import java.util.concurrent.CompletableFuture;

public interface Adapter<T, R> {

    default R service(T request, Object ext) {
        return serviceAsync(request, ext).join();
    }

    CompletableFuture<R> serviceAsync(T request, Object ext);
}
