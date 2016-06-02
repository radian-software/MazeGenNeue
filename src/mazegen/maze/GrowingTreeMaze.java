// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.ArrayUtil;
import mazegen.util.MultiDimensionalArray;
import mazegen.util.Require;
import mazegen.util.ReversibleRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * See http://weblog.jamisbuck.org/2011/1/27/maze-generation-growing-tree-algorithm
 */
public class GrowingTreeMaze extends ArrayMaze implements ReversibleGeneratingMaze {

    public enum State {
        PLACE_ROOT, GROW_TREE, PLACE_ENTRANCE_AND_EXIT, FINISHED;
    }

    public enum SelectionAlgorithm {
        RANDOM, FIRST, LAST, MIDDLE;
    }

    public interface Selector {
        int select(int size, ReversibleRandom random);
    }

    public static class SingleSelector implements Selector {

        private final SelectionAlgorithm selector;

        public SingleSelector(SelectionAlgorithm selector) {
            Require.nonNull(selector, "selector");
            this.selector = selector;
        }

        @Override
        public int select(int size, ReversibleRandom random) {
            switch (selector) {
                case RANDOM: return random.nextInt(size);
                case FIRST: return 0;
                case LAST: return size - 1;
                case MIDDLE: return size / 2;
                default: throw new AssertionError();
            }
        }

    }

    public static class DoubleSelector implements Selector {

        private final SingleSelector primarySelector, secondarySelector;
        private final double primaryChance;

        public DoubleSelector(SelectionAlgorithm primarySelector, SelectionAlgorithm secondarySelector, double primaryChance) {
            Require.nonNull(primarySelector, "primarySelector");
            Require.nonNull(secondarySelector, "secondarySelector");
            Require.between(primaryChance, 0, 1, "primaryChance");
            this.primarySelector = new SingleSelector(primarySelector);
            this.secondarySelector = new SingleSelector(secondarySelector);
            this.primaryChance = primaryChance;
        }

        @Override
        public int select(int size, ReversibleRandom random) {
            if (random.nextDouble() < primaryChance) {
                return primarySelector.select(size, random);
            }
            else {
                return secondarySelector.select(size, random);
            }
        }

    }

    public static class MultiSelector implements Selector {

        private final SingleSelector[] selectors;
        private final double[] weights;
        private final double totalWeight;

        public MultiSelector(SelectionAlgorithm[] selectors, double[] weights) {
            Require.nonEmpty(selectors, "selectors");
            Require.allNonNull(selectors, "selectors");
            Require.nonEmpty(weights, "weights");
            Require.allBetween(weights, 0, 1, "weights");
            Require.sameLength(selectors, weights, "selectors", "weights");
            this.selectors = new SingleSelector[selectors.length];
            for (int i=0; i<selectors.length; i++) {
                this.selectors[i] = new SingleSelector(selectors[i]);
            }
            this.weights = weights;
            double totalWeight = 0;
            for (double weight : weights) {
                totalWeight += weight;
            }
            this.totalWeight = totalWeight;
        }

        @Override
        public int select(int size, ReversibleRandom random) {
            double rand = random.nextDouble(totalWeight);
            int i = -1;
            int cumulativeWeight = 0;
            do {
                i += 1;
                cumulativeWeight += weights[i];
            }
            while (rand < cumulativeWeight);
            return i;
        }

    }

    private static final Selector DEFAULT_SELECTOR = new DoubleSelector(SelectionAlgorithm.LAST, SelectionAlgorithm.RANDOM, 0.5);

    private final ReversibleRandom random;
    private final MazeCoordinate root;
    private MazeCoordinate entrance, exit;
    private final Selector selector;
    // Contains only cells that have been visited but not completed
    private final List<MazeCoordinate> visitedCells;
    // Contains true entries for cells that have been visited
    private final MultiDimensionalArray<Boolean> visitedCellMatrix;
    // Contains only cells that have been completed
    private final List<MazeCoordinate> completedCells;
    // The directions in which walls were dug, or null if no wall was dug
    private final List<Direction> pathDirections;
    private State state;
    // Counts cells that have not been visited OR completed
    private int remainingCells;

    public GrowingTreeMaze(int[] shape) {
        this(shape, DEFAULT_SELECTOR);
    }

    public GrowingTreeMaze(int[] shape, Selector selector) {
        this(shape, selector, System.nanoTime());
    }

    public GrowingTreeMaze(int[] shape, long seed) {
        this(shape, DEFAULT_SELECTOR, seed);
    }

    public GrowingTreeMaze(int[] shape, Selector selector, long seed) {
        super(shape, true);
        Require.nonNull(selector, "selector");
        random = new ReversibleRandom(seed);
        int[] indices = new int[shape.length];
        for (int i=0; i<shape.length; i++) {
            indices[i] = random.nextInt(shape[i]);
        }
        root = new MazeCoordinate(indices);
        this.selector = selector;
        visitedCells = new ArrayList<>();
        visitedCellMatrix = new MultiDimensionalArray<>(shape, false);
        completedCells = new ArrayList<>();
        pathDirections = new ArrayList<>();
        state = State.PLACE_ROOT;
        remainingCells = getSize();
    }

    @Override
    public boolean isGenerationFinished() {
        return state == State.FINISHED;
    }

    private MazeCoordinate getMostDistantEdgeCell(MazeCoordinate fromCell) {
        // cells we need to visit
        List<MazeCoordinate> cells = new ArrayList<>();
        // the directions we're visiting those cells from (to avoid backtracking)
        List<Direction> fromDirections = new ArrayList<>();
        // the distances of those cells from the starting cell
        List<Integer> distances = new ArrayList<>();
        cells.add(fromCell);
        fromDirections.add(null);
        distances.add(0);
        // the edge cell the farthest yet from the starting cell
        MazeCoordinate toCell = null;
        int greatestDistance = 0;
        while (!cells.isEmpty()) {
            MazeCoordinate cell = ArrayUtil.pop(cells);
            Direction fromDirection = ArrayUtil.pop(fromDirections);
            int distance = ArrayUtil.pop(distances);
            // found an edge cell farther from the starting cell than the last one
            if (distance > greatestDistance && isEdgeCell(cell)) {
                toCell = cell;
                greatestDistance = distance;
            }
            // check each direction
            for (Direction toDirection : Direction.getAllDirections(getDimensionCount())) {
                // don't backtrack, and don't cut through walls
                if (!toDirection.equals(fromDirection) && !hasWall(new MazeFace(cell, toDirection))) {
                    cells.add(cell.offset(toDirection));
                    // the direction we're coming from is the opposite of the direction we're
                    // going in
                    fromDirections.add(toDirection.invert());
                    distances.add(distance + 1);
                }
            }
        }
        return toCell;
    }

    private void setEntranceAndExit() {
        MazeCoordinate origin = MazeCoordinate.getOrigin(getDimensionCount());
        entrance = getMostDistantEdgeCell(origin);
        exit = getMostDistantEdgeCell(entrance);
    }

    private void unsetEntranceAndExit() {
        entrance = null;
        exit = null;
    }

    @Override
    public void advanceGeneration() {
        if (state != State.FINISHED) {
            random.advanceGenerator();
        }
        switch (state) {
            case PLACE_ROOT:
                visitedCells.add(root);
                visitedCellMatrix.set(root.getCoordinates(), true);
                remainingCells -= 1;
                state = State.GROW_TREE;
                break;
            case GROW_TREE:
                // pick a random visited cell
                int cellIndex = selector.select(visitedCells.size(), random);
                MazeCoordinate cell = visitedCells.get(cellIndex);
                // find unvisited neighbors of that cell
                List<MazeCoordinate> neighbors = new ArrayList<>();
                List<Direction> directions = new ArrayList<>();
                for (Direction direction : Direction.getAllDirections(getDimensionCount())) {
                    MazeCoordinate neighbor = cell.offset(direction);
                    try {
                        if (!visitedCellMatrix.get(neighbor.getCoordinates())) {
                            neighbors.add(neighbor);
                            directions.add(direction);
                        }
                    }
                    catch (IndexOutOfBoundsException e) {
                        // who cares?
                    }
                }
                // does it have any unvisited neighbors?
                if (!neighbors.isEmpty()) {
                    // if so, visit one
                    int neighborIndex = random.nextInt(neighbors.size());
                    MazeCoordinate neighbor = neighbors.get(neighborIndex);
                    Direction direction = directions.get(neighborIndex);
                    removeWall(new MazeFace(cell, direction));
                    visitedCells.add(neighbor);
                    visitedCellMatrix.set(neighbor.getCoordinates(), true);
                    pathDirections.add(directions.get(neighborIndex));
                    remainingCells -= 1;
                }
                else {
                    // otherwise, mark this cell as completed
                    visitedCells.remove(cellIndex);
                    completedCells.add(cell);
                    pathDirections.add(null);
                }
                if (remainingCells == 0) {
                    state = State.PLACE_ENTRANCE_AND_EXIT;
                }
                break;
            case PLACE_ENTRANCE_AND_EXIT:
                setEntranceAndExit();
                removeWall(getExternalFace(entrance));
                removeWall(getExternalFace(exit));
                state = State.FINISHED;
                break;
            case FINISHED:
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void resetGeneration() {
        random.resetGenerator();
        unsetEntranceAndExit();
        visitedCells.clear();
        visitedCellMatrix.fill(false);
        state = State.PLACE_ROOT;
        remainingCells = getSize();
    }

    @Override
    public void reverseGeneration() {
        if (state != State.PLACE_ROOT) {
            random.reverseGenerator();
        }
        switch (state) {
            case FINISHED:
                addWall(getExternalFace(entrance));
                addWall(getExternalFace(exit));
                unsetEntranceAndExit();
                state = State.PLACE_ENTRANCE_AND_EXIT;
                break;
            case PLACE_ENTRANCE_AND_EXIT:
            case GROW_TREE:
                if (!pathDirections.isEmpty()) {
                    Direction direction = ArrayUtil.pop(pathDirections);
                    boolean hasNeighbors = direction != null;
                    if (hasNeighbors) {
                        MazeCoordinate neighbor = ArrayUtil.pop(visitedCells);
                        addWall(new MazeFace(neighbor, direction.invert()));
                        visitedCellMatrix.set(neighbor.getCoordinates(), false);
                        remainingCells += 1;
                    }
                    else {
                        int size = visitedCells.size() + 1;
                        int cellIndex = selector.select(visitedCells.size(), random);
                        visitedCells.add(cellIndex, ArrayUtil.pop(completedCells));
                    }
                    state = State.GROW_TREE;
                }
                else {
                    visitedCells.remove(0);
                    visitedCellMatrix.set(root.getCoordinates(), false);
                    remainingCells += 1;
                    state = State.PLACE_ROOT;
                }
                break;
            case PLACE_ROOT:
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public String getState() {
        switch (state) {
            case PLACE_ROOT: return "placing root";
            case GROW_TREE: return "growing tree";
            case PLACE_ENTRANCE_AND_EXIT: return "placing entrance and exit";
            case FINISHED: return "finished";
            default: throw new AssertionError();
        }
    }

}
