package org.venus;

public sealed interface Filter<M> extends Comparable<Filter<M>> permits FilterBase {

    int MIN_ORDER = 1;
    int NORM_ORDER = 5;
    int MAX_ORDER = 10;

    M filter(M msg);

    Filter<M> getNext();

    void setNext(Filter<M> next);

    boolean shouldFilter(M msg);

    String name();

    FilterType type();

    int order();

    enum FilterType {
        IN,
        OUT,
    }
}
