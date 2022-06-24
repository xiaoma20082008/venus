package org.venus.protocols;

import org.venus.ClientConnector;
import org.venus.Request;
import org.venus.Response;
import org.venus.core.LifecycleBase;

public abstract class ClientConnectorBase extends LifecycleBase implements ClientConnector {

    @Override
    public Response invoke(Request request) {
        return invokeAsync(request).join();
    }
}
