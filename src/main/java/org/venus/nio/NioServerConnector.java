package org.venus.nio;

import org.venus.Container;
import org.venus.ServerConnector;
import org.venus.core.LifecycleBase;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NioServerConnector extends LifecycleBase implements ServerConnector {

    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public void setContainer(Container container) {

    }

    @Override
    public Charset getURICharset() {
        return null;
    }

    @Override
    public String getURIEncoding() {
        return null;
    }

    @Override
    public void setURIEncoding(String encoding) {

    }

    @Override
    public InetSocketAddress localAddress() {
        return null;
    }
}
