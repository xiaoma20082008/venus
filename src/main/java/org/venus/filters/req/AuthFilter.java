package org.venus.filters.req;

import org.venus.FilterInbound;
import org.venus.Request;

public final class AuthFilter extends FilterInbound {

    private boolean isAuthGW;

    @Override
    public Request filter(Request msg) {
        return next().filter(msg);
    }
}
