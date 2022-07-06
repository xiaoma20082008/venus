package org.venus;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.io.Serial;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionContext extends ConcurrentHashMap<String, Object> {

    public static final String ID_KEY = "_ID_KEY_";
    public static final String SESSION_CTX_KEY = "_SESSION_CTX_KEY_";
    public static final String CONNECTION_KEY = "_CONNECTION_KEY_";
    public static final String PROTOCOL_KEY = "_PROTOCOL_KEY_";
    public static final String SESSION_OUT_KEY = "_SESSION_OUT_KEY_";
    public static final String INBOUND_KEY = "_INBOUND_KEY_";
    public static final String OUTBOUND_KEY = "_OUTBOUND_KEY_";
    public static final String ENDPOINT_KEY = "_ENDPOINT_KEY_";
    public static final AttributeKey<Connection> ATTR_CONNECTION_KEY = AttributeKey.valueOf(CONNECTION_KEY);

    @Serial
    private static final long serialVersionUID = -715526319865237315L;

    public SessionContext incoming(Channel incoming) {
        put(SESSION_CTX_KEY, incoming);
        return this;
    }

    public SessionContext outgoing(ClientConnector outgoing) {
        put(SESSION_OUT_KEY, outgoing);
        return this;
    }

    public SessionContext inbound(InStream instream) {
        put(INBOUND_KEY, instream);
        return this;
    }

    public SessionContext outbound(OutStream outbound) {
        put(OUTBOUND_KEY, outbound);
        return this;
    }

    public SessionContext endpoint(HostStream hostStream) {
        put(ENDPOINT_KEY, hostStream);
        return this;
    }

    public SessionContext id(long id) {
        put(ID_KEY, id);
        return this;
    }

    public Channel incoming() {
        return (Channel) get(SESSION_CTX_KEY);
    }

    public InStream inbound() {
        return (InStream) get(INBOUND_KEY);
    }

    public OutStream outbound() {
        return (OutStream) get(OUTBOUND_KEY);
    }

    public HostStream endpoint() {
        return (HostStream) get(ENDPOINT_KEY);
    }

    public long id() {
        return (Long) getOrDefault(ID_KEY, 0L);
    }

    public void reset() {
        clear();
    }
}
