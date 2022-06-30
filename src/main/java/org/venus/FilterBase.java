package org.venus;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public abstract sealed class FilterBase<M> implements Filter<M> permits FilterInbound, FilterOutbound {

    protected final String name;
    protected final FilterType type;
    protected final int order;

    protected Filter<M> next;

    public FilterBase(FilterType type) {
        this.name = getClass().getSimpleName();
        this.type = requireNonNull(type, "type is null");
        this.order = NORM_ORDER;
    }

    public FilterBase(String name, FilterType type) {
        this(name, type, NORM_ORDER);
    }

    public FilterBase(String name, FilterType type, int order) {
        this.name = requireNonNull(name, "name is null");
        this.type = requireNonNull(type, "type is null");
        this.order = order;
    }

    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final FilterType type() {
        return this.type;
    }

    @Override
    public final int order() {
        return this.order;
    }

    @Override
    public final Filter<M> next() {
        return this.next;
    }

    @Override
    public final Filter<M> next(Filter<M> next) {
        this.next = Objects.requireNonNull(next, "next is null");
        return this;
    }

    @Override
    public boolean shouldFilter(M msg) {
        return true;
    }

    @Override
    public CompletableFuture<M> filterAsync(CompletableFuture<M> future) {
        CompletableFuture<M> f = new CompletableFuture<>();
        future.whenComplete((msg, err) -> {
            if (err != null) {
                f.completeExceptionally(err);
            } else {
                f.complete(filter(msg));
            }
        });
        return f;
    }

    @Override
    public int compareTo(Filter<M> o) {
        return Integer.compare(order(), o.order());
    }

    @Override
    public String toString() {
        if (next != null) {
            return name + "(" + type + ")" + "->" + next;
        }
        return name + "(" + type + ")";
    }

}
