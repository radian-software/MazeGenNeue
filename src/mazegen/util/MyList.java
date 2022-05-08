// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.util;

/**
 * Simple extension of the List interface that adds some convenient overloads
 * of the get, set, and remove methods. These overloads act on the last element
 * of the list.
 *
 * The idea is that list.remove() is more elegant than list.remove(list.size() - 1).
 */
public interface MyList<E> extends java.util.List<E> {

    E get();
    void set(E item);
    E remove();

}
