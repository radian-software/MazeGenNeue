// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.Require;

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
