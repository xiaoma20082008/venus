package org.venus.filters;

import org.venus.FilterInbound;
import org.venus.Request;

public final class MockReqFilter extends FilterInbound {

    @Override
    public Request filter(Request request) {
        return request;
    }

}
