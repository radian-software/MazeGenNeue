// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.StringUtil;
import mazegen.util.Require;

import java.util.Arrays;

/**
 * See Maze for information about the coordinate system used in this class.
 */
public class MazeCoordinate {

    private final int[] coordinates;

    public MazeCoordinate(int... coordinates) {
        Require.nonEmpty(coordinates, "coordinates");
        this.coordinates = coordinates;
    }

    public static MazeCoordinate getOrigin(int dimensionCount) {
        return new MazeCoordinate(new int[dimensionCount]);
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

    public MazeCoordinate offset(Direction... directions) {
        Require.nonNull(directions, "directions");
        int[] newCoordinates = new int[coordinates.length];
        System.arraycopy(coordinates, 0, newCoordinates, 0, coordinates.length);
        for (Direction dir : directions) {
            newCoordinates[dir.getDimension()] += dir.getSignInt();
        }
        return new MazeCoordinate(newCoordinates);
    }

    public MazeCoordinate[] getNeighbors() {
        Direction[] directions = Direction.getAllDirections(coordinates.length);
        MazeCoordinate[] neighbors = new MazeCoordinate[directions.length];
        for (int i=0; i<directions.length; i++) {
            neighbors[i] = offset(directions[i]);
        }
        return neighbors;
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
        return "(" + StringUtil.join(", ", coordinates) + ")";
    }

}
