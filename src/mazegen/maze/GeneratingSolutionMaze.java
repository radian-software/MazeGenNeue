// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

/**
 * This interface is provided so that a caller can reference methods in both the
 * GeneratingMaze and SolutionMaze interfaces on a single object.
 */
public interface GeneratingSolutionMaze extends GeneratingMaze, SolutionMaze {}
