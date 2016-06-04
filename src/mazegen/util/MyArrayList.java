// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

import java.util.Collection;

/**
 * See MyList for more information on this class.
 */
public class MyArrayList<E> extends java.util.ArrayList<E> implements MyList<E> {

    public MyArrayList() {
        super();
    }

    public MyArrayList(Collection<? extends E> c) {
        super(c);
    }

    public MyArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public E get() {
        return get(size() - 1);
    }

    @Override
    public void set(E item) {
        set(size() - 1, item);
    }

    @Override
    public E remove() {
        return remove(size() - 1);
    }

}
