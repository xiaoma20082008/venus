package org.venus;

public final class FilterRunner {

    private FilterInbound in;
    private ClientConnector cli;
    private FilterOutbound out;

    /**
     * 不出意外,这是个模板方法
     */
    public final Response run(Request req) {
        // in
        req = in.filter(req);
        // call
        Response resp = cli.invoke(req);
        if (resp.code() != 200) {
            return resp;
        }
        // out
        resp = out.filter(resp);
        return resp;
    }

}
