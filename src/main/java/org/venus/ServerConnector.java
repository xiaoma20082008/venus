package org.venus;

import java.net.InetSocketAddress;

public interface ServerConnector extends Lifecycle, Contained {

    InetSocketAddress localAddress();
}
