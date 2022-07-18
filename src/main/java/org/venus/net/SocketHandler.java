package org.venus.net;

public interface SocketHandler<S> {
    SocketState process(SocketWrapperBase<S> socket, SocketEvent event);
}
