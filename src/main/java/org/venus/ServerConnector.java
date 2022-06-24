package org.venus;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public interface ServerConnector extends Lifecycle, Contained {

    Charset getURICharset();

    String getURIEncoding();

    void setURIEncoding(String encoding);

    Mapper getMapper();

    InetSocketAddress localAddress();
}
