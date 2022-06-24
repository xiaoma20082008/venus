package org.venus.protocols.dubbo;

import org.venus.ClientConnector;
import org.venus.protocols.ProtocolBase;

public class DubboProtocol extends ProtocolBase {

    public static final String NAME = "DUBBO";

    public DubboProtocol() {
        super(NAME);
    }

    @Override
    protected ClientConnector doAcquire() throws Exception {
        return new DubboClientConnector();
    }

}
