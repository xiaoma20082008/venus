package org.venus;

import java.util.concurrent.CompletableFuture;

public interface Adapter<T, R> {

    CompletableFuture<R> service(T request, Object ext);
}
