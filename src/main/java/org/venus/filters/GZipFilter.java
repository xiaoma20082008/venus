package org.venus.filters;

import org.venus.FilterOutbound;
import org.venus.Response;

public final class GZipFilter extends FilterOutbound {

    @Override
    public Response filter(Response msg) {
        return next().filter(msg);
    }
}
