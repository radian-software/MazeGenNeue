// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.Require;

/**
 * A GeneratingMaze is a Maze that contains some sort of state. Typically, a GeneratingMaze is
 * not expected to be mutable to external classes. Rather, it mutates itself according to a
 * predefined algorithm. For examples of maze generation algorithms, see the following link:
 *
 * http://weblog.jamisbuck.org/2011/2/7/maze-generation-algorithm-recap
 *
 * GeneratingMazes are expected to finish generation at some point. After generation is
 * finished, the isGenerationFinished method should return true and the advanceGeneration()
 * method should not have any effect when called.
 *
 * Although GeneratingMazes are not necessarily reversible step by step (unlike
 * ReversibleGeneratingMazes) they can be reset using the resetGeneration method. It is
 * expected that a single GeneratingMaze will generate exactly the same Maze in exactly the
 * same steps if it is reset, so Mazes that are dependent on a random number generator will
 * need to keep track of its seed.
 *
 * The getState method is provided so that a GeneratingMaze can return some information about
 * its current state (for instance, "carving passages" or "placing entrance and exit").
 */
public interface GeneratingMaze extends Maze {

    public boolean isGenerationFinished();
    public void advanceGeneration();
    public default void advanceGeneration(int steps) {
        Require.nonNegative(steps, "steps");
        for (int i=0; i<steps; i++) {
            advanceGeneration();
        }
    }
    public default void finishGeneration() {
        while (!isGenerationFinished()) {
            advanceGeneration();
        }
    }
    public void resetGeneration();
    public String getState();

}
