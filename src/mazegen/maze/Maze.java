// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

public interface Maze {

    public int getDimensionCount();
    public int[] getShape();
    public boolean isWall(MazeCoordinate cell, Direction side);

}
