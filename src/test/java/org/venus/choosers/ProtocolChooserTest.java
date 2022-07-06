package org.venus.choosers;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.ClientConnector;
import org.venus.Response;
import org.venus.SessionContext;
import org.venus.core.StandardHttpRequest;
import org.venus.nio.NioConnection;
import org.venus.protocols.http.HttpProtocol;

public class ProtocolChooserTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolChooserTest.class);

    @Test
    public void test_choose() {
        ProtocolChooser chooser = new ProtocolChooser();
        NioConnection connection = new NioConnection(null);
        connection.context().put(SessionContext.PROTOCOL_KEY, HttpProtocol.NAME);
        StandardHttpRequest request = StandardHttpRequest.builder()
                .method("GET")
                .uri("/index")
                .version("HTTP/1.1")
                .connection(connection)
                .build();
        try (ClientConnector client = chooser.choose(request).create();) {
            Response response = client.invoke(request);
            LOGGER.info("request={},response={}", request, response);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
