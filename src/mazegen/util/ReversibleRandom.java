// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

import java.util.ArrayList;
import java.util.List;

public class ReversibleRandom {

    private long seed;
    private final List<Long> history = new ArrayList<>();
    private int index = 0;
    private boolean onRecord = true;

    private static long makeNonZero(long x) {
        return x != 0 ? x : 1;
    }

    public ReversibleRandom() {
        while (seed == 0) {
            seed = System.nanoTime();
        }
        history.add(seed);
    }

    public ReversibleRandom(long seed) {
        Require.nonZero(seed, "seed");
        this.seed = seed;
        history.add(seed);
    }

    public long getSeed() {
        return getSeed(0);
    }

    public long getSeed(int index) {
        return history.get(index);
    }

    public int getIndex() {
        return index;
    }

    public boolean inInitialState() {
        return index == 0;
    }

    public boolean inLatestState() {
        return index == history.size() - 1;
    }

    public boolean onRecord() {
        return onRecord;
    }

    public void advanceGenerator() {
        index += 1;
        if (index >= history.size()) {
            history.add(seed);
        }
        else {
            seed = history.get(index);
        }
        onRecord = true;
    }

    public void resetGenerator() {
        seed = history.get(index);
        onRecord = true;
    }

    public void resetGenerator(int index) {
        Require.between(index, 0, history.size() - 1, "index");
        this.index = index;
        seed = history.get(index);
        onRecord = true;
    }

    public void reverseGenerator() {
        if (inInitialState()) throw new IllegalStateException("cannot reverse from initial state");
        index -= 1;
        seed = history.get(index);
        onRecord = true;
    }

    public long nextLong() {
        onRecord = false;
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

    // overrides from Object

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReversibleRandom that = (ReversibleRandom) o;

        if (seed != that.seed) return false;
        if (index != that.index) return false;
        return history.equals(that.history);

    }

    @Override
    public int hashCode() {
        int result = (int) (seed ^ (seed >>> 32));
        result = 31 * result + history.hashCode();
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return String.format("ReversibleRandom(seed = %dL, history = %s, index = %d, onRecord = %b)", seed, history, index, onRecord);
    }

}
