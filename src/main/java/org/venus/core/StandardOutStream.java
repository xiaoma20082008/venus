package org.venus.core;

import org.venus.OutStream;

public class StandardOutStream extends ContainerBase implements OutStream {

    public StandardOutStream() {
        getPipeline().setBasic(new StandardOutStreamValve(this));
        setName("StandardOutStream");
    }

}
