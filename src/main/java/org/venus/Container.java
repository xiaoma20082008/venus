package org.venus;

public interface Container extends Lifecycle {

    Pipeline getPipeline();

    String getName();

    void setName(String name);

    Container getNext();

    void setNext(Container next);
}
