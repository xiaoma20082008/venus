package org.venus;

import com.google.inject.AbstractModule;
import org.venus.netty.NettyServer;
import org.venus.netty.NettyServerConnector;

public class VenusModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Server.class).to(NettyServer.class).asEagerSingleton();
        bind(ServerConnector.class).to(NettyServerConnector.class).asEagerSingleton();
    }

}
