package org.venus;

import java.net.SocketAddress;

public interface Server extends Lifecycle {

    SocketAddress getLocalAddress();

}
