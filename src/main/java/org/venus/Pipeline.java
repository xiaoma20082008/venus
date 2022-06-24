package org.venus;

public interface Pipeline extends Contained {
    Valve getBasic();

    void setBasic(Valve basic);

    Valve getFirst();

    void addValue(Valve valve);

    void removeValue(Valve valve);

    Valve[] getValues();

}
