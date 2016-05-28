// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

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
