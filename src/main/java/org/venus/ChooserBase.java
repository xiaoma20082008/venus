package org.venus;

public abstract non-sealed class ChooserBase implements Chooser {

    @Override
    public final Protocol choose(Request request) {
        Protocol protocol = doChoose(select(request));
        if (protocol == null) {
            throw new IllegalArgumentException("no adaptive protocol found by request:" + request);
        }
        return protocol;
    }

    protected String select(Request request) {
        String name = (String) request.getAttr(SessionContext.PROTOCOL_KEY);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("no protocol key found by request. key = " + SessionContext.PROTOCOL_KEY);
        }
        return name;
    }

    protected abstract Protocol doChoose(String name);

}
