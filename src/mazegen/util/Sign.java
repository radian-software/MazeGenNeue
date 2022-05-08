// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.util;

/**
 * Using this enum is more expressive than simply using a literal positive or negative
 * sign on an integer, or using a boolean.
 */
public enum Sign {

    POSITIVE, NEGATIVE;

    public boolean isPositive() {
        return this == POSITIVE;
    }

    public boolean isNegative() {
        return this == NEGATIVE;
    }

    public int toInt() {
        return isPositive() ? 1 : -1;
    }

    public Sign invert() {
        return isPositive() ? NEGATIVE : POSITIVE;
    }

    @Override
    public String toString() {
        return isPositive() ? "+" : "-";
    }

}
