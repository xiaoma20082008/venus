package org.venus.protocols.http;

import org.venus.ClientConnector;
import org.venus.protocols.ProtocolBase;

public class HttpProtocol extends ProtocolBase {

    public static final String NAME = "HTTP";

    public HttpProtocol() {
        super(NAME);
    }

    @Override
    protected ClientConnector doAcquire() throws Exception {
        return new HttpClientConnector();
    }

}
