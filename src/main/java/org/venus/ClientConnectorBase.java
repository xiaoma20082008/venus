package org.venus;

import org.venus.core.LifecycleBase;

public abstract class ClientConnectorBase extends LifecycleBase implements ClientConnector {

    @Override
    public Response invoke(Request request) {
        return invokeAsync(request).join();
    }
}
