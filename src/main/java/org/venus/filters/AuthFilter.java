package org.venus.filters;

import org.venus.FilterInbound;
import org.venus.Request;

public final class AuthFilter extends FilterInbound {

    private boolean isAuthGW;

    @Override
    public Request filter(Request msg) {
        return null;
    }
}
