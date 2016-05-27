// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

public interface ReversibleGeneratingMaze extends GeneratingMaze {

    public void reverseGeneration();
    public default void reverseGeneration(int steps) {
        for (int i=0; i<steps; i++) {
            reverseGeneration();
        }
    }

}
