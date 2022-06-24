package org.venus.core;

import org.venus.InStream;

public class StandardInStream extends ContainerBase implements InStream {

    public StandardInStream() {
        getPipeline().setBasic(new StandardInStreamValve(this));
        setName("Upstream");
    }

}
