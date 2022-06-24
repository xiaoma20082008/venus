package org.venus;

public interface Protocol {

    String name();

    ClientConnector create();

    void release(ClientConnector client);

    boolean check(ClientConnector client);
}
