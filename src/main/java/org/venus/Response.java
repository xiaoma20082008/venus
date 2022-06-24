package org.venus;

import io.netty.buffer.ByteBuf;

import java.util.Map;

public interface Response {

    // region  response line: HTTP/1.1 200 OK

    String protocol();

    int code();

    String reason();

    // endregion response line

    // region  response header

    Map<String, String> headers();

    // endregion response header

    // region  response body

    ByteBuf body();

    // endregion response body

    String content();
}
