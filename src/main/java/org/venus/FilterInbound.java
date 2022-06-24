package org.venus;

public abstract non-sealed class FilterInbound extends FilterBase<Request> {

    public FilterInbound() {
        super(FilterType.IN);
    }

    public FilterInbound(String name) {
        super(name, FilterType.IN);
    }

}
