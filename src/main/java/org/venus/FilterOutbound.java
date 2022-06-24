package org.venus;

public abstract non-sealed class FilterOutbound extends FilterBase<Response> {

    public FilterOutbound() {
        super(FilterType.OUT);
    }

    public FilterOutbound(String name) {
        super(name, FilterType.OUT);
    }

}
