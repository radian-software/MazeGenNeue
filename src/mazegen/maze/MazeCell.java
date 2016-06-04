// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.Require;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A MazeCell represents a single cell (square, cube, or hypercube) of a Maze. Each cell has
 * (2 * dimensionCount) faces, and there is either a wall or no wall for each of these faces.
 * However, a MazeCell only stores information about the faces on its negative sides, to
 * avoid redundancy. Indeed, every face has two "sides"; that is, it is bordered by two cells.
 * If every MazeCell were to keep track of each of its faces, the information about whether
 * any given face has a wall would be duplicated between two MazeCells. The asymmetry created
 * by this design choice is hidden behind the Maze abstraction in ArrayMaze. That is, the
 * ArrayMaze.hasWall method accepts any face for a given cell, then decides which MazeCell
 * to which to delegate the call based on whether the face is on a positive or negative
 * side of a cell.
 */
final class MazeCell {

    private final boolean[] walls;

    public MazeCell(int dimensionCount) {
        Require.positive(dimensionCount, "dimensionCount");
        this.walls = new boolean[dimensionCount];
    }

    public MazeCell(int dimensionCount, boolean isWall) {
        this(dimensionCount);
        if (isWall) {
            Arrays.fill(walls, true);
        }
    }

    public MazeCell(boolean... walls) {
        Require.nonEmpty(walls, "walls");
        this.walls = walls;
    }

    public boolean hasWall(int dimension) {
        return walls[dimension];
    }

    public boolean isClear() {
        for (boolean wall : walls) {
            if (wall) return false;
        }
        return true;
    }

    public void setWall(int dimension, boolean isWall) {
        walls[dimension] = isWall;
    }

    public void addWall(int dimension) {
        setWall(dimension, true);
    }

    public void removeWall(int dimension) {
        setWall(dimension, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MazeCell mazeCell = (MazeCell) o;

        return Arrays.equals(walls, mazeCell.walls);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(walls);
    }

    @Override
    public String toString() {
        if (walls.length <= 4) {
            StringBuilder sb = new StringBuilder("{");
            for (int d=0; d<walls.length; d++) {
                if (hasWall(d)) {
                    sb.append(Direction.DIMENSION_NAMES[d]);
                }
            }
            sb.append("}");
            return sb.toString();
        }
        else {
            List<Integer> wallDimensions = new ArrayList<>();
            for (int d=0; d<walls.length; d++) {
                if (walls[d]) {
                    wallDimensions.add(d);
                }
            }
            return wallDimensions
                    .stream()
                    .map(i -> Integer.toString(i))
                    .collect(Collectors.joining(", ", "{", "}"));
        }
    }
}
