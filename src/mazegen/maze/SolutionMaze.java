// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

/**
 * SolutionMazes are not necessarily expected to respond differently to Maze API
 * calls when showing their solution versus hiding it. However, it is conventional
 * for any graphical representations of the Maze to be modified so that they show
 * (or hide) the solution. For instance, the output of GrowingTreeMaze.toString
 * changes when the solution is being shown.
 *
 * If a SolutionMaze mutates over time (for instance, if it is also a GeneratingMaze),
 * it may not always be appropriate to show a solution, and in this case the
 * showSolution method should throw an IllegalStateException.
 */
public interface SolutionMaze extends Maze {

    public void showSolution();
    public void hideSolution();

}
