package org.venus.core;

import org.venus.HostStream;

public class StandardHostStream extends ContainerBase implements HostStream {

    public StandardHostStream() {
        getPipeline().setBasic(new StandardHostStreamValve(this));
        setName("StandardHostStream");
    }

}
