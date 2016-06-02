// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

import java.util.ArrayList;
import java.util.List;

public class ReversibleRandom {

    private long seed;
    private final List<Long> history = new ArrayList<>();
    private int index = 0;

    public ReversibleRandom(long seed) {
        this.seed = seed;
    }

    public void advanceGenerator() {
        if (index < history.size()) {
            seed = history.get(index);
        }
        else {
            history.add(seed);
        }
        index += 1;
    }

    public long nextLong() {
        seed ^= (seed << 21);
        seed ^= (seed >>> 35);
        seed ^= (seed << 4);
        return seed;
    }

    public long nextLong(long n) {
        long bits, val;
        do {
            bits = (nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + n - 1 < 0L);
        return val;
    }

    public int nextInt() {
        return (int)nextLong();
    }

    public int nextInt(int n) {
        advanceGenerator();
        int bits, val;
        do {
            bits = (nextInt() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + n - 1 < 0);
        return val;
    }

    private static final double DOUBLE_UNIT = 0x1.0p-53;

    /**
     * See http://stackoverflow.com/a/37529283/3538165
     */
    public double nextDouble() {
        return (nextLong() >>> 11) * DOUBLE_UNIT;
    }

    public double nextDouble(double max) {
        return nextDouble() * max;
    }

    public double nextDouble(double min, double max) {
        return min + nextDouble(max - min);
    }

    public <T> T choose(List<T> list) {
        return list.get(nextInt(list.size()));
    }

    public long getInitialSeed() {
        return history.isEmpty() ? seed : history.get(0);
    }

    public boolean inInitialState() {
        return index == 0;
    }

    public void reverseGenerator() {
        if (inInitialState()) throw new IllegalStateException("cannot reverse from initial state");
        index -= 1;
    }

    public void resetGenerator() {
        index = 0;
    }

}
