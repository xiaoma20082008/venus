package org.venus.utils;

import java.util.concurrent.CompletableFuture;

public class Futures {

    public static <T> CompletableFuture<T> complete(T value, Throwable e) {
        CompletableFuture<T> res = new CompletableFuture<>();
        if (e != null) {
            res.completeExceptionally(e);
        } else {
            res.complete(value);
        }
        return res;
    }

}
