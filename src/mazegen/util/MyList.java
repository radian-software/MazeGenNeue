// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

public interface MyList<E> extends java.util.List<E> {

    E get();
    void set(E item);
    E remove();

}
