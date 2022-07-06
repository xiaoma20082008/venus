package org.venus.nio;

import org.venus.Server;
import org.venus.core.LifecycleBase;

import java.net.SocketAddress;

public class NioServer extends LifecycleBase implements Server {

    private NioServerConnector connector;

    @Override
    public SocketAddress getLocalAddress() {
        return this.connector.localAddress();
    }

}
