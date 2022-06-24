package org.venus.protocols.grpc;

import org.venus.ClientConnector;
import org.venus.protocols.ProtocolBase;

public class GrpcProtocol extends ProtocolBase {

    public static final String NAME = "GRPC";

    public GrpcProtocol() {
        super(NAME);
    }

    @Override
    protected ClientConnector doAcquire() throws Exception {
        return new GrpcClientConnector();
    }

}
