// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

import mazegen.maze.Direction;
import mazegen.maze.MazeCoordinate;

import java.util.Objects;

public final class Require {

    private Require() {}

    public static <T> void nonNull(T obj, String name) {
        Objects.requireNonNull(obj, name + " cannot be null");
    }

    public static <T> void allNonNull(T[] array, String name) {
        nonNull(array, name);
        for (T obj : array) {
            Objects.requireNonNull(obj, name + " cannot have any null elements");
        }
    }

    public static void positive(int num, String name) {
        if (num <= 0) {
            throw new IllegalArgumentException(name + "must be positive + (was " + num + ")");
        }
    }

    public static void nonNegative(int num, String name) {
        if (num < 0) {
            throw new IllegalArgumentException(name + " cannot be negative (was " + num + ")");
        }
    }

    public static void nonZero(long num, String name) {
        if (num == 0) {
            throw new IllegalArgumentException(name + " cannot be zero");
        }
    }

    public static void between(int num, int lower, int upper, String name) {
        if (num < lower || num > upper) {
            throw new IllegalArgumentException(String.format("%s must be in the range [%d, %d] (was %d)", name, lower, upper, num));
        }
    }

    public static void between(double num, double lower, double upper, String name) {
        if (num < lower || num > upper) {
            throw new IllegalArgumentException(String.format("%s must be in the range [%f, %f] (was %f)", name, lower, upper, num));
        }
    }

    public static void allBetween(double[] array, double lower, double upper, String name) {
        Require.nonNull(array, name);
        for (double num : array) {
            between(num, lower, upper, "each element of " + name);
        }
    }

    public static <T> void nonEmpty(T[] array, String name) {
        Require.nonNull(array, name);
        if (array.length == 0) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }
    }

    public static void nonEmpty(boolean[] array, String name) {
        Require.nonNull(array, name);
        if (array.length == 0) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }
    }

    public static void nonEmpty(int[] array, String name) {
        Require.nonNull(array, name);
        if (array.length == 0) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }
    }

    public static void nonEmpty(double[] array, String name) {
        Require.nonNull(array, name);
        if (array.length == 0) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }
    }

    public static void nEntries(int[] array, int n, String name) {
        Require.nonNull(array, name);
        if (array.length != n) {
            throw new IllegalArgumentException(String.format("%s must have %d entries (has %d)", name, n, array.length));
        }
    }

    public static <T> void sameLength(T[] array1, double[] array2, String name1, String name2) {
        Require.nonNull(array1, name1);
        Require.nonNull(array2, name2);
        if (array1.length != array2.length) {
            throw new IllegalArgumentException(String.format("%s and %s must have the same number of entries (have %d and %d)", name1, name2, array1.length, array2.length));
        }
    }

    public static void nDimensional(Direction direction, int n, String name) {
        Require.nonNull(direction, name);
        if (direction.getDimension() >= n) {
            throw new IllegalArgumentException(String.format("%s can be at most %d-dimensional (is %d-dimensional)", name, n, direction.getDimension()));
        }
    }

    public static void nDimensional(MazeCoordinate coordinate, int n, String name) {
        Require.nonNull(coordinate, name);
        if (coordinate.getDimensionCount() != n) {
            throw new IllegalArgumentException(String.format("%s must be %d-dimensional (is %d-dimensional)", name, n, coordinate.getDimensionCount()));
        }
    }

}
