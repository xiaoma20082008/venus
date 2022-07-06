package org.venus.nio;

import org.venus.ServerConnectorBase;
import org.venus.config.ServerConfig;

import java.net.InetSocketAddress;

public class NioServerConnector extends ServerConnectorBase {

    public NioServerConnector(ServerConfig config) {
        super(config);
    }

    @Override
    public InetSocketAddress localAddress() {
        return null;
    }
}
