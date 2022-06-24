package org.venus.netty;

import org.venus.Server;
import org.venus.ServerConnector;
import org.venus.core.LifecycleBase;

import java.net.SocketAddress;

public class NettyServer extends LifecycleBase implements Server {

    private final ServerConnector connector;

    public NettyServer() {
        this.connector = new NettyServerConnector();
    }

    @Override
    public SocketAddress getLocalAddress() {
        return this.connector.localAddress();
    }

    @Override
    public void start() throws Exception {
        this.connector.start();
    }

    @Override
    public void close() throws Exception {
        this.connector.close();
    }

}
