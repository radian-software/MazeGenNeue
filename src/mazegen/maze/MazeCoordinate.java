// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.ArrayUtil;

import java.util.Arrays;
import java.util.Objects;

public class MazeCoordinate {

    private final int[] coordinates;

    public MazeCoordinate(int[] coordinates) {
        Objects.requireNonNull(coordinates, "coordinates cannot be null");
        if (coordinates.length == 0) throw new IllegalArgumentException("coordinates cannot be empty");
        this.coordinates = coordinates;
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
