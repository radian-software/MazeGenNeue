// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.maze;

import mazegen.util.Require;
import mazegen.util.Sign;

/**
 * See Maze for information on the coordinate system used here.
 */
public class Direction {

    public static final String[] DIMENSION_NAMES = {"x", "y", "z", "w"};

    public static final Direction LEFT = new Direction(0, Sign.NEGATIVE);
    public static final Direction RIGHT = new Direction(0, Sign.POSITIVE);
    public static final Direction FRONT = new Direction(1, Sign.NEGATIVE);
    public static final Direction BACK = new Direction(1, Sign.POSITIVE);
    public static final Direction DOWN = new Direction(2, Sign.NEGATIVE);
    public static final Direction UP = new Direction(2, Sign.POSITIVE);

    /**
     * Results of the getAllDirections method for low dimensions are cached because
     * this method is often called for the same number of dimensions repeatedly.
     */
    private static final int highestPrecomputedDimensions = 4;
    private static final Direction[][] allDirections = new Direction[highestPrecomputedDimensions +1][];
    static {
        for (int d = 1; d<=highestPrecomputedDimensions; d++) {
            allDirections[d] = computeAllDirections(d);
        }
    }

    private final int dimension;
    private final Sign sign;

    public Direction(int dimension, Sign sign) {
        Require.nonNegative(dimension, "dimension");
        Require.nonNull(sign, "sign");
        this.dimension = dimension;
        this.sign = sign;
    }

    /**
     * This constructor is the inverse of toInt.
     */
    public Direction(int identifier) {
        Require.nonNegative(identifier, "identifier");
        this.dimension = identifier / 2;
        this.sign = identifier % 2 == 0 ? Sign.NEGATIVE : Sign.POSITIVE;
    }

    private static final Direction[] computeAllDirections(int dimensions) {
        Direction[] directions = new Direction[dimensions * 2];
        for (int dimension=0; dimension<dimensions; dimension++) {
            directions[dimension * 2] = new Direction(dimension, Sign.NEGATIVE);
            directions[dimension * 2 + 1] = new Direction(dimension, Sign.POSITIVE);
        }
        return directions;
    }

    public static final Direction[] getAllDirections(int dimensions) {
        Require.positive(dimensions, "dimensions");
        if (dimensions < highestPrecomputedDimensions) {
            return allDirections[dimensions];
        }
        else {
            return computeAllDirections(dimensions);
        }
    }

    public int getDimension() {
        return dimension;
    }

    public Sign getSign() {
        return sign;
    }

    public boolean isPositive() {
        return sign.isPositive();
    }

    public boolean isNegative() {
        return sign.isNegative();
    }

    public int getSignInt() {
        return sign.toInt();
    }

    public Direction invert() {
        return new Direction(dimension, sign.invert());
    }

    /**
     * This is equivalent to Arrays.asList(getAllDirections(getDimension() + 1)).indexOf(this).
     */
    public int toInt() {
        return dimension * 2 + (sign.isPositive() ? 1 : 0);
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
        return sign.toString() + (dimension < 4 ? DIMENSION_NAMES[dimension] : dimension);
    }

}
