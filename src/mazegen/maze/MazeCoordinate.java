// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.ArrayUtil;
import mazegen.util.Require;

import java.util.Arrays;

/*
 * These coordinates are zero-based and refer to the distinct squares or (hyper)cubes comprising a Maze.
 * In a three-dimensional context, the 0-axis runs from left to right, the 1-axis runs from front to back,
 * and the 2-axis runs from bottom to top. In a two-dimensional context, we imagine the maze as being viewed
 * from above, so that the 0-axis runs from left to right and the 1-axis runs from bottom to top.
 */
public class MazeCoordinate {

    private final int[] coordinates;

    public MazeCoordinate(int[] coordinates) {
        Require.nonEmpty(coordinates, "coordinates");
        this.coordinates = coordinates;
    }

    public int getDimensionCount() {
        return coordinates.length;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public int getCoordinate(int dimension) {
        return coordinates[dimension];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MazeCoordinate that = (MazeCoordinate) o;

        return Arrays.equals(coordinates, that.coordinates);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(coordinates);
    }

    @Override
    public String toString() {
        return "(" + ArrayUtil.join(", ", coordinates) + ")";
    }

}
