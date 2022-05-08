// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.maze;

/**
 * A Maze can have any number of dimensions, but it must be rectangular. Most frequently, Mazes
 * are either two or three dimensions, but any nonnegative integer is a valid number of
 * dimensions.
 *
 * The dimensions of a Maze are referred to with nonnegative integers: that is, a three-dimensional
 * Maze will have dimensions 0, 1, and 2. Dimensions need not have any inherent significance, but
 * typically the following conventions are followed for the orientations of the axes. For
 * three-dimensional Mazes, the 0-axis (or x-axis) runs from left to right; the 1-axis (or y-axis)
 * runs from front to back; and the 2-axis (or z-axis) runs from bottom to top. For two-dimensional
 * Mazes, we imagine them as a slice of a three-dimensional Maze viewed from above, so that the
 * 0-axis runs from left to right and the 1-axis runs from bottom to top.
 *
 * The shape of a Maze is the array containing its side lengths. So, the shape of a three-dimensional
 * Maze will be an array of length 3. The first entry will be the side length in the direction of the
 * 0-axis, the second entry the side length in the direction of the 1-axis, and so on.
 *
 * The origin of the coordinate system used for Mazes lies at one of the corners of the Maze. The
 * positive directions of each of the axes follow one of the edges of the Maze. Each set of integer
 * coordinates, then, uniquely identifies a particular square, cube, or hypercube (that is, a "cell")
 * of the Maze. Furthermore, the range of these coordinates is determined by the shape of the Maze.
 * For a two-dimensional Maze, for example, the cube specified by a pair of coordinates (x, y) lies
 * within the outer walls of the Maze if and only if (0 <= x < shape[0] && 0 <= y < shape[1]).
 *
 * Each cell of a Maze will have (2 * dimensionCount) faces. For instance, the cells of a
 * three-dimensional Maze are cubes, and each has 6 faces. The defining operation (hasWall) of a Maze
 * is checking whether there is a wall along a specified face. The information required for this
 * operation is the cell as well as the face. The cell is specified by a MazeCoordinate, which is
 * simply a container for coordinates as described above; the face is specified by a Direction. In
 * particular, it is specified by the Direction from the center of the cell to the center of the face.
 * These two objects are packaged together into the MazeFace that hasWall expects.
 */
public interface Maze {

    public int getDimensionCount();

    public int[] getShape();

    public boolean hasWall(MazeFace face);

}
