// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.maze;

/**
 * This interface is provided so that a caller can reference methods in both the
 * GeneratingMaze and SolutionMaze interfaces on a single object.
 */
public interface GeneratingSolutionMaze extends GeneratingMaze, SolutionMaze {}
