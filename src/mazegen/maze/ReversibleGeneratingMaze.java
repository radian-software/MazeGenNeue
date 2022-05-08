// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.maze;

import mazegen.util.Require;

/**
 * A ReversibleGeneratingMaze is a GeneratingMaze that can take steps in reverse as well
 * as forward. It is expected that advanceGeneration and reverseGeneration are inverse
 * methods; that is, calling both will have no net effect (assuming that generation is
 * neither at the beginning nor end). Calling reverseGeneration when the
 * ReversibleGeneratingMaze is in its initial state should have no effect.
 *
 * ReversibleGeneratingMazes, like GeneratingMazes, are expected to be deterministic.
 * That is, no matter what sequence of calls of advanceGeneration, reverseGeneration, and
 * resetGeneration the Maze receives, it should remain on the same linear history of
 * generation steps.
 */
public interface ReversibleGeneratingMaze extends GeneratingMaze {

    public void reverseGeneration();
    public default void reverseGeneration(int steps) {
        Require.nonNegative(steps, "steps");
        for (int i=0; i<steps; i++) {
            reverseGeneration();
        }
    }

}
