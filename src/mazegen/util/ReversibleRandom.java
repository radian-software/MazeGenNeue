// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The ReversibleRandom class is a random number generator that implements many
 * of the same methods as java.util.Random, but also allows for "reversibility".
 * Some additional utility methods, such as choose, are also provided for convenience.
 *
 * Let us consider the following sequence of calls, in which uppercase letters stand for arbitrary longs
 * and the caret (^) shows the position referenced by index in the history:
 *
 *                                                        result | seed | history   | index |   history command
 *
 * ( 1) ReversibleRandom random = new ReversibleRandom()           A      [A]         0
 *                                                                         ^
 * ( 2) random.nextInt()                                  R        B      [A]         0
 *                                                                         ^
 * ( 3) random.advanceGenerator()                                  B      [A, B]      1         advanceGenerator()
 *                                                                            ^
 * ( 4) random.nextInt()                                  S        C      [A, B]      1
 *                                                                            ^
 * ( 5) random.nextInt()                                  T        D      [A, B]      1
 *                                                                            ^
 * ( 6) random.advanceGenerator()                                  D      [A, B, D]   2         advanceGenerator()
 *                                                                               ^
 * ( 7) random.nextInt()                                  U        E      [A, B, D]   2
 *                                                                               ^
 * ( 8) random.nextInt()                                  V        F      [A, B, D]   2
 *                                                                               ^
 * ( 9) random.resetGenerator()                                    D      [A, B, D]   2         resetGenerator()
 *                                                                               ^
 * (10) random.nextInt()                                  U        E      [A, B, D]   2
 *                                                                               ^
 * (11) random.reverseGenerator()                                  B      [A, B, D]   1         reverseGenerator()
 *                                                                            ^
 * (12) random.nextInt()                                  S        C      [A, B, D]   1
 *                                                                            ^
 * (13) random.resetGenerator()                                    B      [A, B, D]   1         resetGenerator()
 *                                                                            ^
 * (14) random.nextInt()                                  S        C      [A, B, D]   1
 *                                                                            ^
 * (15) random.advanceGenerator()                                  D      [A, B, D]   2         advanceGenerator()
 *                                                                               ^
 * (16) random.nextInt()                                  U        E      [A, B, D]   2
 *                                                                               ^
 * (17) random.resetGenerator(0)                                   A      [A, B, D]   0         resetGenerator(0)
 *                                                                         ^
 * (18) random.nextInt()                                  R        B      [A, B, D]   0
 *                                                                         ^
 *
 * Discussion follows:
 *
 * ( 1) The initial seed of the generator is A. The history is initialized as a list
 *      length 1, and the index is at entry A in the history.
 *
 * ( 2) Generating a random number changes the seed, just as it would in any other
 *      random number generator. However, the history and index position are
 *      unchanged.
 *
 * ( 3) Advancing the generator increases the index position by one. Since the index
 *      now points to a position outside the history (which was previously of length
 *      1), the current seed is added to the history. Advancing the generator does
 *      not affect the current seed.
 *
 * ( 4) As before, generating random numbers changes the seed, but does not affect
 *      the history or index.
 *
 * ( 6) Since more than one random number was generated in between calls to
 *      advanceGenerator(), the history is "missing" a seed. This is typical. The
 *      caller has complete control over which seeds are saved in the history.
 *
 * ( 9) Calling resetGenerator() resets the seed to whatever is at the current index
 *      in the history. In this case, it resets the seed to D. In essence,
 *      resetGenerator() undoes the effects of any random number generation that
 *      happened since the last history-modifying command.
 *
 * (10) The number generated here is identical to the one generated by call (7)
 *      because of the call to resetGenerator().
 *
 * (11) Calling reverseGenerator() decreases the index position by one, and resets
 *      the current seed to whatever is at the new index position. In this case, it
 *      resets the seed to B.
 *
 * (12) The number generated here is identical to the one generated by call (4).
 *
 * (14) Calling resetGenerator() has the same effect as before, so that the number
 *      generated here is identical to the one generated by call (12).
 *
 * (15) Because the index is not at the end of the history, calling advanceGenerator()
 *      here does not affect the history. It simply increments the index position
 *      and resets the position to whatever is at the new index. In this way,
 *      advanceGenerator() and reverseGenerator() are inverses, except that their
 *      behavior when they move the index position out of the history is different:
 *      advanceGenerator() will add the current seed to the history if the index is
 *      at the end of the history, while reverseGenerator() will have no effect if
 *      the index is at the beginning of the history.
 *
 * (17) Calling resetGenerator(int) resets the index to the specified position, and
 *      resets the seed to whatever is at that position in the history. The specified
 *      index, however, must be a point within the history. Aside from their
 *      behavior at the endpoints of the history, several of the ReversibleRandom
 *      history modification methods may be understood as special cases of
 *      resetGenerator(int):
 *
 *      advanceGenerator() -> resetGenerator(currentIndex + 1)
 *      resetGenerator()   -> resetGenerator(currentIndex)
 *      reverseGenerator() -> resetGenerator(currentIndex - 1)
 *
 * The onRecord variable records whether or not the seed has been mutated from the
 * value recorded in the history. Accordingly, generating random numbers sets
 * onRecord to false, while invoking history modification methods sets onRecord to
 * true.
 *
 * This class uses an XORShift random number generator. For more information on the
 * algorithm, see the following link:
 *
 * http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
 *
 * XORShift random number generators cannot generate the number 0, nor can they be
 * seeded with it. Hence, this seed is disallowed in the constructors. The inability to
 * generate 0 should make no practical difference in the quality of random numbers
 * generated by the class.
 *
 * Since this class is not intended for secure applications, it makes no effort to pick
 * seeds unpredictably. In fact, it just uses System.nanoTime.
 *
 */
public class ReversibleRandom {

    // instance variables

    private long seed;
    private final List<Long> history = new ArrayList<>();
    private int index = 0;
    private boolean onRecord = true;

    // constructors

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

    // information getters

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

    // history modification

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

    // random number generation

    /**
     * See: http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
     */
    public long nextLong() {
        onRecord = false;
        seed ^= (seed << 21);
        seed ^= (seed >>> 35);
        seed ^= (seed << 4);
        return seed;
    }

    /**
     * See: http://stackoverflow.com/a/2546186/3538165
     */
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

    /**
     * Analogous to nextLong(long)
     */
    public int nextInt(int n) {
        int bits, val;
        do {
            bits = (nextInt() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + n - 1 < 0);
        return val;
    }

    /**
     * See: http://stackoverflow.com/a/37529283/3538165
     */
    public double nextDouble() {
        return (nextLong() >>> 11) * 0x1.0p-53;
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
