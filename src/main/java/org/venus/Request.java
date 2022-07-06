package org.venus;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Map;

public interface Request {

    // region request line: GET / HTTP/1.1

    String method();

    /**
     * uri = requestUrl + "?" + queryString
     *
     * @see #path()
     * @see #parameters()
     */
    String uri();

    /**
     * @see #uri()
     */
    String path();

    /**
     * @see #uri()
     */
    Map<String, List<String>> parameters();

    String version();

    // endregion request line

    // region  request header

    Map<String, String> headers();

    // endregion request header

    // region  request body

    ByteBuf body();

    // endregion request body

    SessionContext context();

    Connection connection();

    void putAttr(String key, Object attr);

    Object getAttr(String key);

    String content();
}
