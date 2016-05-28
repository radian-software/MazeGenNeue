// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

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
