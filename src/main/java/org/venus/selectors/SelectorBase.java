package org.venus.selectors;

import org.venus.Selector;

import java.util.List;

public abstract class SelectorBase<T> implements Selector<T> {

    @Override
    public T select(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        }
        return doSelect(list);
    }

    protected abstract T doSelect(List<T> list);
}
