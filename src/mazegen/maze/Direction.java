// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.Sign;

import java.util.Objects;

public class Direction {

    private final int dimension;
    private final Sign sign;

    public Direction(int dimension, Sign sign) {
        Objects.requireNonNull(sign, "Sign cannot be null");
        if (dimension < 0) throw new IllegalArgumentException("Dimension cannot be negative");
        this.dimension = dimension;
        this.sign = sign;
    }

    public static final Direction[] getAllDirections(int dimensions) {
        Direction[] directions = new Direction[dimensions * 2];
        for (int dimension=0; dimension<dimensions; dimension++) {
            directions[dimension * 2] = new Direction(dimension, Sign.NEGATIVE);
            directions[dimension * 2 + 1] = new Direction(dimension, Sign.POSITIVE);
        }
        return directions;
    }

    public int getDimension() {
        return dimension;
    }

    public Sign getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Direction direction = (Direction) o;

        if (dimension != direction.dimension) return false;
        return sign == direction.sign;

    }

    @Override
    public int hashCode() {
        int result = dimension;
        result = 31 * result + sign.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return sign.toString() + dimension;
    }

}