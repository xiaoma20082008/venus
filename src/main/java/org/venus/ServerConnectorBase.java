package org.venus;

import org.venus.config.ServerConfig;
import org.venus.core.LifecycleBase;

public abstract class ServerConnectorBase extends LifecycleBase implements ServerConnector {

    protected final ServerConfig config;

    protected Container container;

    public ServerConnectorBase(ServerConfig config) {
        this.config = config;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

}
