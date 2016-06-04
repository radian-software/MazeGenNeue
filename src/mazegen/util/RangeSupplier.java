// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

import java.util.function.Supplier;

/**
 * Non-pure Supplier that provides an arithmetic sequence of integers.
 */
public class RangeSupplier implements Supplier<Integer> {

    private int current;
    private final int step;

    public RangeSupplier() {
        this(0);
    }

    public RangeSupplier(int start) {
        this(start, 1);
    }

    public RangeSupplier(int start, int step) {
        this.current = start;
        this.step = step;
    }

    @Override
    public Integer get() {
        return current++;
    }

}
