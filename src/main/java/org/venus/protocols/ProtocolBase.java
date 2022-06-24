package org.venus.protocols;

import org.venus.ClientConnector;
import org.venus.Protocol;

public abstract class ProtocolBase implements Protocol {

    protected final String name;

    public ProtocolBase(String name) {
        this.name = name;
    }

    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final ClientConnector create() {
        try {
            return doAcquire();
        } catch (Exception e) {
            throw new IllegalStateException("Acquire Client failed", e);
        }
    }

    @Override
    public final void release(ClientConnector client) {
        if (client != null) {
            try {
                doRelease(client);
            } catch (Exception e) {
                throw new IllegalStateException("Release Client failed", e);
            }
        }
    }

    @Override
    public final boolean check(ClientConnector client) {
        if (client != null) {
            try {
                return doCheck(client);
            } catch (Exception e) {
                throw new IllegalStateException("Check Client failed", e);
            }
        }
        return false;
    }

    protected abstract ClientConnector doAcquire() throws Exception;

    protected boolean doCheck(ClientConnector client) throws Exception {
        return true;
    }

    protected void doRelease(ClientConnector client) throws Exception {
        client.close();
    }

}
