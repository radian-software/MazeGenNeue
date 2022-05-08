// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The data is stored with the first index being the index of fastest iteration. However, operations
 * that process every element of the array do not guarantee any particular order.
 */
public final class MultiDimensionalArray<T> {

    private final int[] shape;
    private final T[] data;

    @SuppressWarnings("unchecked")
    public MultiDimensionalArray(int[] shape) {
        Require.nonEmpty(shape, "shape");
        int size = 1;
        for (int sideLength : shape) {
            Require.positive(sideLength, "entry in shape");
            size *= sideLength;
        }
        this.shape = shape;
        // due to type erasure, you can't instantiate an array of a generic type in Java
        // the resulting awkward construction generates a spurious "unchecked cast" warning
        data = (T[]) new Object[size];
    }

    public MultiDimensionalArray(int[] shape, T fill) {
        this(shape);
        if (fill != null) {
            fill(fill);
        }
    }

    public MultiDimensionalArray(int[] shape, Supplier<T> fill) {
        this(shape);
        fill(fill);
    }

    public MultiDimensionalArray(int[] shape, Function<int[], T> fill) {
        this(shape);
        fill(fill);
    }

    public boolean isValid(int[] indices) {
        Require.nEntries(indices, shape.length, "indices");
        for (int i=0; i<indices.length; i++) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts from a set of indices (one for each dimension) to a single index locating
     * the referenced item in the data array.
     */
    private int toIndex(int[] indices) {
        if (!isValid(indices)) {
            throw new IndexOutOfBoundsException(Arrays.toString(indices));
        }
        int index = 0;
        int cumulativeSize = 1;
        for (int i=0; i<shape.length; i++) {
            index += cumulativeSize * indices[i];
            cumulativeSize *= shape[i];
        }
        return index;
    }

    public T get(int... indices) {
        return data[toIndex(indices)];
    }

    public void set(int[] indices, T item) {
        data[toIndex(indices)] = item;
    }

    public void fill(T fill) {
        Arrays.fill(data, fill);
    }

    public void fill(Supplier<T> fill) {
        Require.nonNull(fill, "fill");
        for (int i=0; i<data.length; i++) {
            data[i] = fill.get();
        }
    }

    public void fill(Function<int[], T> fill) {
        Require.nonNull(fill, "fill");
        int i = 0;
        int[] indices = new int[shape.length];
        outerLoop:
        while (true) {
            data[i] = fill.apply(indices);
            i += 1;
            int j = 0;
            while (indices[j] == shape[j] - 1) {
                j += 1;
                if (j >= shape.length) {
                    break outerLoop;
                }
            }
            for (int k=0; k<j; k++) {
                indices[k] = 0;
            }
            indices[j] += 1;
        }
    }

    public int getDimensionCount() {
        return shape.length;
    }

    public int[] getShape() {
        return shape;
    }

    public int getSideLength(int dimension) {
        return shape[dimension];
    }

    public int getSize() {
        return data.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiDimensionalArray<?> that = (MultiDimensionalArray<?>) o;

        return Arrays.equals(shape, that.shape) && Arrays.deepEquals(data, that.data);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(shape);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        List<String> items = new ArrayList<>();
        int i = 0;
        int[] indices = new int[shape.length];
        int brackets = -shape.length;
        boolean stop = false;
        while (!stop) {
            int j = 0;
            while (indices[j] == shape[j] - 1) {
                j += 1;
                if (j >= shape.length) {
                    stop = true;
                    break;
                }
            }
            for (int k=0; k<j; k++) {
                indices[k] = 0;
            }
            if (!stop) {
                indices[j] += 1;
            }
            if (brackets < 0) {
                brackets = -brackets;
            }
            else {
                brackets = -j;
            }
            StringBuilder sb = new StringBuilder();
            if (brackets > 0) {
                for (int k = 0; k < brackets; k++) {
                    sb.append('[');
                }
            }
            sb.append(data[i].toString());
            if (brackets < 0) {
                for (int k=0; k<-brackets; k++) {
                    sb.append(']');
                }
            }
            items.add(sb.toString());
            i += 1;
        }
        return String.join(", ", items);
    }

}
