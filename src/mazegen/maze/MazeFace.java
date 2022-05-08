// Copyright (c) 2016 Radian LLC and contributors.
package mazegen.maze;

/**
 * This class is a simple package of a MazeCoordinate and Direction that represents
 * one face of a Maze cell, so that the two can be returned as a single object from
 * method calls (for instance, ArrayMaze.getExternalFace).
 */
public class MazeFace {

    private final MazeCoordinate coordinate;
    private final Direction side;

    public MazeFace(MazeCoordinate coordinate, Direction side) {
        this.coordinate = coordinate;
        this.side = side;
    }

    public MazeCoordinate getCoordinate() {
        return coordinate;
    }

    public Direction getSide() {
        return side;
    }

}
