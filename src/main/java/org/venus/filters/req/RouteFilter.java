package org.venus.filters.req;

import org.venus.FilterInbound;
import org.venus.Request;
import org.venus.SessionContext;
import org.venus.protocols.http.HttpProtocol;

public final class RouteFilter extends FilterInbound {

    @Override
    public Request filter(Request msg) {
        msg.context().put(SessionContext.PROTOCOL_KEY, HttpProtocol.NAME);
        return msg;
    }

}
