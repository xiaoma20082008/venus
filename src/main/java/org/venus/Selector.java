package org.venus;

import java.util.List;

public interface Selector<T> {
    T select(List<T> list);
}
