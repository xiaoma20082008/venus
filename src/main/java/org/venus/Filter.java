package org.venus;

import java.util.concurrent.CompletableFuture;

public sealed interface Filter<M> extends Comparable<Filter<M>> permits FilterBase {

    int MIN_ORDER = 1;
    int NORM_ORDER = 5;
    int MAX_ORDER = 10;

    M filter(M msg);

    CompletableFuture<M> filterAsync(M msg);

    CompletableFuture<M> filterAsync(CompletableFuture<M> future);

    Filter<M> next();

    Filter<M> next(Filter<M> next);

    boolean shouldFilter(M msg);

    String name();

    FilterType type();

    int order();

    enum FilterType {
        IN,
        OUT,
    }
}
