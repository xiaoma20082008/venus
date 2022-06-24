package org.venus.selectors;

import java.util.List;
import java.util.Random;

public class RandomSelector<T> extends SelectorBase<T> {

    @Override
    public T doSelect(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

}
