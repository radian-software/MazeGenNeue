// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

public enum Sign {

    POSITIVE, NEGATIVE;

    public Sign invert() {
        switch (this) {
            case POSITIVE: return NEGATIVE;
            case NEGATIVE: return POSITIVE;
            default: throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case POSITIVE: return "+";
            case NEGATIVE: return "-";
            default: throw new AssertionError();
        }
    }

}
