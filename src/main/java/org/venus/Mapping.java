package org.venus;

public class Mapping {

    public InStream instream;
    public HostStream hostStream;
    public OutStream outbound;

    public Mapping(InStream instream, HostStream hostStream, OutStream outbound) {
        this.instream = instream;
        this.hostStream = hostStream;
        this.outbound = outbound;
    }
}
