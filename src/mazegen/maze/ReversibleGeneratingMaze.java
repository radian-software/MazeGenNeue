// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.Require;

public interface ReversibleGeneratingMaze extends GeneratingMaze {

    public void reverseGeneration();
    public default void reverseGeneration(int steps) {
        Require.nonNegative(steps, "steps");
        for (int i=0; i<steps; i++) {
            reverseGeneration();
        }
    }

}
