package org.venus.protocols.grpc;

import org.venus.Request;
import org.venus.Response;
import org.venus.protocols.ClientConnectorBase;

import java.util.concurrent.CompletableFuture;

/**
 * <a href="https://grpc.io/docs/guides/performance/#java">Grpc Best Practices</a>
 * <p>
 * <ul>
 *     <li><b>Use non-blocking stubs</b> to parallelize RPCs.</li>
 *     <li><b>Provide a custom executor that limits the number of threads, based on your workload</b> (cached (default), fixed, forkjoin, etc).</li>
 * </ul>
 */
public class GrpcClientConnector extends ClientConnectorBase {

    @Override
    public CompletableFuture<Response> invokeAsync(Request request) {
        return null;
    }
}
