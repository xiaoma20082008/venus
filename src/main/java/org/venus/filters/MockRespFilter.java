package org.venus.filters;

import org.venus.FilterOutbound;
import org.venus.Response;

public final class MockRespFilter extends FilterOutbound {

    @Override
    public Response filter(Response response) {
        return response;
    }

}
