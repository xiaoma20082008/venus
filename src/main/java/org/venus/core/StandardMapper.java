package org.venus.core;

import org.venus.*;

public class StandardMapper implements Mapper {

    private final InStream instream = new StandardInStream();
    private final HostStream hostStream = new StandardHostStream();
    private final OutStream outStream = new StandardOutStream();

    private final Mapping mapping = new Mapping(instream, hostStream, outStream);

    @Override
    public Mapping map(Request request) {
        return mapping;
    }

}
