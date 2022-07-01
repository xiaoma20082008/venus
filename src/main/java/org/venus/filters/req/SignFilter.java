package org.venus.filters.req;

import org.venus.FilterInbound;
import org.venus.Request;

public final class SignFilter extends FilterInbound {

    @Override
    public Request filter(Request msg) {
        return next().filter(msg);
    }

}
