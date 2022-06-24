package org.venus.selectors;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinSelector<T> extends SelectorBase<T> {

    private final AtomicInteger index = new AtomicInteger();

    @Override
    public T doSelect(List<T> list) {
        int i = Math.abs(index.getAndIncrement()) % list.size();
        return list.get(i);
    }

}
