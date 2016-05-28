// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.MultiDimensionalArray;
import mazegen.util.Require;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Each MazeCell contains information only about the walls on its negative sides. Therefore, the ArrayMaze
 * needs a MultiDimensionalArray of MazeCells whose side lengths are one greater than the dimensions of
 * the maze. The MazeCells on the positive sides of the array are used to hold information about the walls
 * on the far positive sides of the maze.
 *
 * The placement of the extra MazeCells at the end of the MultiDimensionalArray instead of the beginning
 * ensures that the coordinates of the MazeCell required to check a wall on the negative side are the same as the
 * coordinates of the corresponding cell. On the other hand, the coordinate of the relevant side needs to be
 * incremented by one if a wall on the positive side is to be checked. The toIndices method performs the
 * conversion from global coordinates to internal array coordinates.
 *
 * Some of the sides of some of the cells on the positive edges of the array are outside the rectangular bounding
 * box of the maze. These walls are non-writable, and setWall will throw an exception if you try to write to them.
 */
public class ArrayMaze implements Maze {

    private final int[] shape;
    private final MultiDimensionalArray<MazeCell> cells;

    public ArrayMaze(int[] shape) {
        this(shape, false);
    }

    public ArrayMaze(int[] shape, boolean hasWalls) {
        Require.nonEmpty(shape, "shape");
        this.shape = shape;
        int[] internalShape = new int[shape.length];
        for (int i=0; i<shape.length; i++) {
            internalShape[i] = shape[i] + 1;
        }
        if (hasWalls) {
            cells = new MultiDimensionalArray<>(internalShape, indices -> {
                int edgeDimension = -1;
                boolean multipleEdges = false;
                for (int d=0; d<shape.length; d++) {
                    if (indices[d] == internalShape[d] - 1) {
                        if (edgeDimension == -1) {
                            edgeDimension = d;
                        }
                        else {
                            multipleEdges = true;
                            break;
                        }
                    }
                }
                if (edgeDimension == -1) {
                    return new MazeCell(shape.length, true);
                }
                else {
                    MazeCell cell = new MazeCell(shape.length);
                    if (!multipleEdges) {
                        cell.addWall(edgeDimension);
                    }
                    return cell;
                }
            });
        }
        else {
            cells = new MultiDimensionalArray<>(internalShape, indices -> {
                MazeCell cell = new MazeCell(shape.length);
                for (int dimension = 0; dimension < shape.length; dimension++) {
                    if (indices[dimension] == 0 || indices[dimension] == internalShape[dimension] - 1) {
                        boolean addWall = true;
                        for (int otherDimension = 0; otherDimension < shape.length; otherDimension++) {
                            if (otherDimension != dimension
                                    && indices[otherDimension] == internalShape[otherDimension] - 1) {
                                addWall = false;
                                break;
                            }
                        }
                        if (addWall) {
                            cell.addWall(dimension);
                        }
                    }
                }
                return cell;
            });
        }
    }

    public int getDimensionCount() {
        return shape.length;
    }

    public int[] getShape() {
        return shape;
    }

    public boolean containsCoordinate(MazeCoordinate coordinate) {
        Require.nDimensional(coordinate, getDimensionCount(), "coordinate");
        for (int d=0; d<coordinate.getDimensionCount(); d++) {
            if (d < 0 || d > shape[d]) {
                return false;
            }
        }
        return true;
    }

    private boolean isWritable(int[] indices, Direction side) {
        boolean onEdge = false;
        for (int d=0; d<getDimensionCount(); d++) {
            if (indices[d] == cells.getShape()[d] - 1) {
                if (!onEdge) {
                    onEdge = true;
                }
                else {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] toIndices(MazeCoordinate coordinate, Direction side) {
        Require.nonNull(coordinate, "coordinate");
        Require.nDimensional(side, getDimensionCount(), "side");
        int[] indices;
        if (side.isNegative()) {
            indices = coordinate.getCoordinates();
        }
        else {
            indices = new int[getDimensionCount()];
            System.arraycopy(coordinate.getCoordinates(), 0, indices, 0, getDimensionCount());
            indices[side.getDimension()] += 1;
        }
        return indices;
    }

    public boolean isWritable(MazeFace face) {
        return isWritable(toIndices(face.getCoordinate(), face.getSide()), face.getSide());
    }

    public boolean hasWall(MazeFace face) {
        MazeCoordinate coordinate = face.getCoordinate();
        Direction side = face.getSide();
        try {
            return cells.get(toIndices(coordinate, side)).hasWall(side.getDimension());
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    protected void setWall(MazeFace face, boolean isWall) {
        MazeCoordinate coordinate = face.getCoordinate();
        Direction side = face.getSide();
        int[] indices = toIndices(coordinate, side);
        if (!isWritable(indices, side)) {
            throw new IndexOutOfBoundsException("face is not writable");
        }
        cells.get(indices).setWall(side.getDimension(), isWall);
    }

    protected void addWall(MazeFace face) {
        setWall(face, true);
    }

    protected void removeWall(MazeFace face) {
        setWall(face, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayMaze arrayMaze = (ArrayMaze) o;

        if (!Arrays.equals(shape, arrayMaze.shape)) return false;
        return cells.equals(arrayMaze.cells);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(shape);
        result = 31 * result + cells.hashCode();
        return result;
    }

    private char getFrontLeftCornerChar(MazeCoordinate coordinate) {
        MazeFace leftFace = new MazeFace(coordinate.offset(Direction.LEFT), Direction.FRONT),
                rightFace = new MazeFace(coordinate, Direction.FRONT),
                frontFace = new MazeFace(coordinate.offset(Direction.FRONT), Direction.LEFT),
                backFace = new MazeFace(coordinate, Direction.LEFT);
        boolean left = hasWall(leftFace);
        boolean right = hasWall(rightFace);
        boolean front = hasWall(frontFace);
        boolean back = hasWall(backFace);
        if (left) {
            //  .O
            // -+.
            //  .
            if (right) {
                //  .O
                // -+-
                //  .
                if (front) {
                    //  .O
                    // -+-
                    //  |
                    if (back) {
                        //  |O
                        // -+-
                        //  |
                        return '┼';
                    }
                    else {
                        //   O
                        // -+-
                        //  |
                        return '┬';
                    }
                }
                else {
                    //  .O
                    // -+-
                    //
                    if (back) {
                        //  |O
                        // -+-
                        //
                        return '┴';
                    }
                    else {
                        //   O
                        // -+-
                        //
                        return '─';
                    }
                }
            }
            else {
                //  .O
                // -+
                //  .
                if (front) {
                    //  .O
                    // -+
                    //  |
                    if (back) {
                        //  |O
                        // -+
                        //  |
                        return '┤';
                    }
                    else {
                        //   O
                        // -+
                        //  |
                        return '┐';
                    }
                }
                else {
                    //  .O
                    // -+
                    //
                    if (back) {
                        //  |O
                        // -+
                        //
                        return '┘';
                    }
                    else {
                        //   O
                        // -+
                        //
                        return '╴';
                    }
                }
            }
        }
        else {
            //  .O
            //  +.
            //  .
            if (right) {
                //  .O
                //  +-
                //  .
                if (front) {
                    //  .O
                    //  +-
                    //  |
                    if (back) {
                        //  |O
                        //  +-
                        //  |
                        return '├';
                    }
                    else {
                        //   O
                        //  +-
                        //  |
                        return '┌';
                    }
                }
                else {
                    //  .O
                    //  +-
                    //
                    if (back) {
                        //  |O
                        //  +-
                        //
                        return '└';
                    }
                    else {
                        //   O
                        //  +-
                        //
                        return '╶';
                    }
                }
            }
            else {
                //  .O
                //  +
                //  .
                if (front) {
                    //  .O
                    //  +
                    //  |
                    if (back) {
                        //  |O
                        //  +
                        //  |
                        return '│';
                    }
                    else {
                        //   O
                        //  +
                        //  |
                        return '╷';
                    }
                }
                else {
                    //  .O
                    //  +
                    //
                    if (back) {
                        //  |O
                        //  +
                        //
                        return '╵';
                    }
                    else {
                        //   O
                        //  +
                        //
                        return ' ';
                    }
                }
            }
        }
    }

    private char getFrontChar(MazeCoordinate coordinate) {
        MazeFace face = new MazeFace(coordinate, Direction.FRONT);
        return hasWall(face) ? '─' : ' ';
    }

    private char getLeftChar(MazeCoordinate coordinate) {
        MazeFace face = new MazeFace(coordinate, Direction.LEFT);
        return hasWall(face) ? '│' : ' ';
    }

    private char getCenterChar(MazeCoordinate coordinate) {
        if (getDimensionCount() == 2) return ' ';
        MazeFace bottomFace = new MazeFace(coordinate, Direction.DOWN),
                topFace = new MazeFace(coordinate, Direction.UP);
        boolean down = !hasWall(bottomFace);
        boolean up = !hasWall(topFace);
        if (down) {
            if (up) {
                return '↕';
            }
            else {
                return '↓';
            }
        }
        else {
            if (up) {
                return '↑';
            }
            else {
                return ' ';
            }
        }
    }

    private String layerToString(BiFunction<Integer, Integer, MazeCoordinate> coordinateMaker) {
        List<String> rows = new ArrayList<>();
        for (int y=0; y<=shape[1]; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x=0; x<=shape[0]; x++) {
                MazeCoordinate coord = coordinateMaker.apply(x, y);
                sb.append(getFrontLeftCornerChar(coord));
                if (x < shape[0]) {
                    sb.append(getFrontChar(coord));
                }
            }
            rows.add(sb.toString());
            if (y < shape[1]) {
                sb = new StringBuilder();
                for (int x = 0; x <= shape[0]; x++) {
                    MazeCoordinate coord = coordinateMaker.apply(x, y);
                    sb.append(getLeftChar(coord));
                    if (x < shape[0]) {
                        sb.append(getCenterChar(coord));
                    }
                }
                rows.add(sb.toString());
            }
        }
        Collections.reverse(rows);
        return String.join("\n", rows);
    }

    @Override
    public String toString() {
        switch (getDimensionCount()) {
            case 2:
                return layerToString(MazeCoordinate::new);
            case 3:
                List<String> layers = IntStream
                        .range(0, shape[2])
                        .mapToObj(z -> layerToString((x, y) -> new MazeCoordinate(x, y, z)))
                        .collect(Collectors.toList());
                Collections.reverse(layers);
                return String.join("\n", layers);
            default:
                return cells.toString();
        }
    }

}