package org.venus.utils;

import java.util.concurrent.CompletableFuture;

public interface AsyncObjectFactory<T> {

    CompletableFuture<T> create();

    CompletableFuture<Void> destroy(T object);

    CompletableFuture<Boolean> validate(T object);

}