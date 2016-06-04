// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.maze;

import mazegen.util.*;

/**
 * This ReversibleGeneratingMaze implements the Growing Tree algorithm, which can
 * be summarized as follows:
 *
 * 1. Start with all walls of the Maze initially present.
 * 2. Let C be a list of cells, initially empty. Add one cell to C, at random.
 * 3. Choose a cell from C, and carve a passage to a random neighbor, adding that
 *    neighbor to C as well. If the cell has no neighbors, remove it from C.
 * 4. Repeat #3 until C is empty.
 *
 * For more information on the Growing Tree algorithm, see the following link:
 *
 * http://weblog.jamisbuck.org/2011/1/27/maze-generation-growing-tree-algorithm
 *
 * The behavior of the Growing Tree algorithm may be adjusted by changing the
 * algorithm used to choose cells from C in step 2. Here, the Selector interface
 * abstracts the operation of choosing a cell from C. Provided with the number of
 * cells in C and a random number generator, a Selector will return the index of
 * the cell to choose.
 *
 * The SelectionAlgorithm enum contains the basic algorithms that can be used to
 * select cells from C. Various concrete implementations of the Selector interface
 * are provided. The SingleSelector uses one of the SelectionAlgorithms every time.
 * The DoubleSelector and MultiSelector allow choosing randomly between two or more
 * algorithms each time a cell is selected.
 *
 * The most frequently used algorithms are as follows:
 *
 * - Choosing the LAST cell from C every time causes the algorithm to behave like
 *   the Recursive Backtracking algorithm.
 * - Choosing a RANDOM cell from C every time causes the algorithm to behave like
 *   Prim's algorithm.
 * - Choosing either the LAST cell from C or a RANDOM cell from C, with some nonzero
 *   probability for each, often yields the most interesting results. A 50/50 split
 *   is a good starting place.
 *
 * Each of these algorithms is implemented for convenience as RecursiveBacktracker,
 * PrimAlgorithm, and DefaultAlgorithm, respectively.
 *
 * The Growing Tree algorithm has several different states, as enumerated in the State
 * enum. At construction, the root cell is determined, but it is not immediately added
 * to C. At this point, the state is PLACE_ROOT. In the next step, the root cell is
 * added to C and the state is changed to GROW_TREE.
 *
 * The GROW_TREE phase corresponds to step 3 of the Growing Tree algorithm as
 * described above. At each step, a cell is either added to or removed from C.
 * According to the algorithm, step 3 should be repeated until C is empty. However,
 * this part can be optimized as follows: Note that when every cell is either in C or
 * has been in C in the past, the maze is complete. At this point, all that is left is
 * to remove cells from C, so we can immediately finish this stage of the algorithm
 * and set the state to PLACE_ENTRANCE_AND_EXIT.
 *
 * A GrowingTreeMaze is a perfect maze, and as such it is a tree, in the nomenclature
 * of graph theory (with cells being vertices and absent walls being edges). There is
 * a standard algorithm for finding a pair of vertices in a tree that are farther
 * apart than any other pair of vertices, which is as follows:
 *
 * 1. Pick an arbitrary vertex V1.
 * 2. Find the vertex V2 that is farthest from V1.
 * 3. Find the vertex V3 that is farthest from V2.
 * 4. Then V2 and V3 comprise the pair of vertices we seek.
 *
 * This algorithm cannot be used directly for placing the entrance and exit as far
 * away from each other as possible, since we do not seek a pair of cells that are
 * farther from each other than any other pair of cells, but rather a pair of *edge*
 * cells that are farther from each other than any other pair of *edge* cells.
 *
 * This class, therefore, uses a slightly modified version of the standard algorithm,
 * in which the searches in steps 2 and 3 are constrained to edge cells. I am unsure
 * as to whether or not this algorithm finds the edge cells that are *farthest* apart,
 * but in practice it seems good enough.
 *
 * In the PLACE_ENTRANCE_AND_EXIT state, the entrance and exit positions are generated
 * and the relevant two walls are removed simultaneously. (This is the only step in
 * which two walls are modified.) The state is then changed to FINISHED, at which point
 * further steps will have no effect.
 *
 * Since this Maze implements ReversibleGeneratingMaze, it implements its algorithms in
 * such a way that all of the above steps are entirely reversible. If the reversibility
 * requirement were eliminated, then the implementation could certainly be streamlined.
 *
 * This class uses ReversibleRandom for its random number generation, to support the
 * reversibility of its generation. The general outline of calls to ReversibleRandom
 * methods is as follows:
 *
 * - The ReversibleRandom object is initialized in the constructor.
 * - Several random numbers are generated immediately to place the root cell (i.e.,
 *   the initial cell in C).
 * - When advanceGeneration is called and the state is GROW_TREE:
 *   - advanceGenerator() is invoked and several random numbers are generated to
 *     choose a cell from C and a random neighbor (it is possible that no random
 *     numbers are actually required in a given step, if the Selector is
 *     deterministic and the cell has no neighbors, but this does not make any
 *     difference).
 * - When resetGeneration is called:
 *   - resetGenerator(0) is invoked. This will reset the ReversibleRandom object to
 *     its state in the constructor. It is unnecessary to repeat the random-number-
 *     generating calls in the constructor, since the seed will be reset anyway by
 *     advanceGenerator() when advanceGeneration is called.
 * - When reverseGeneration is called to reverse a GROW_TREE step:
 *   - resetGenerator() is invoked, to undo the changes in seed caused by the random
 *     number generation in the previous command.
 *   - More random numbers are (possibly) generated to determine how to undo the
 *     previous step.
 *   - reverseGenerator() is invoked, to undo the effects of advanceGenerator().
 *
 * This class overrides the protected support methods for ArrayMaze.toString so that
 * dots (.) will be shown on the toString representation of a GrowingTreeMaze if the
 * solution is being shown. The algorithm is trivial and inefficient: When figuring
 * out which character to show at a particular location, just iterate through the
 * solution path to see if it passes through the location. If so, show a dot.
 *
 * The implementations in ArrayMaze are given precedence, so that the arrows for
 * three-dimensional mazes are not overwritten with dots. This makes reading the
 * solution path from a three-dimensional maze slightly inconvenient, but there is
 * no easy way to show both the solution path and the arrows in a single character.
 */
public class GrowingTreeMaze extends ArrayMaze implements ReversibleGeneratingSolutionMaze {

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

    public static class RecursiveBacktracker extends SingleSelector {

        public RecursiveBacktracker() {
            super(SelectionAlgorithm.LAST);
        }

    }

    public static class PrimAlgorithm extends SingleSelector {

        public PrimAlgorithm() {
            super(SelectionAlgorithm.LAST);
        }

    }

    public static class DefaultAlgorithm extends DoubleSelector {

        public DefaultAlgorithm() {
            this(0.5);
        }

        public DefaultAlgorithm(double primChance) {
            super(SelectionAlgorithm.RANDOM, SelectionAlgorithm.LAST, primChance);
        }

    }

    private final ReversibleRandom random;
    private final MazeCoordinate root;
    /**
     * The cells closest to the entrance and exit, respectively, that are still
     * within the maze boundaries.
     */
    private MazeCoordinate entrance, exit;
    private final Selector selector;
    /**
     * Contains cells in C. Addition and removal take place at the end of the list.
     */
    private final MyList<MazeCoordinate> visitedCells;
    /**
     * Contains true entries for cells in C and cells that have been in C at any
     * point in the past.
     */
    private final MultiDimensionalArray<Boolean> visitedCellMatrix;
    /**
     * Contains cells that were, but are no longer, in C. Addition and removal take
     * place at the end of the list.
     */
    private final MyList<MazeCoordinate> completedCells;
    /**
     * For each GROW_TREE step, contains the Direction in which the wall was dug
     * (or null, if no wall was dug in that step), in chronological order.
     */
    private final MyList<Direction> pathDirections;
    private State state;
    /**
     * The number of cells that have never been in C. When this number reaches
     * zero, we can short-circuit and proceed immediately to the
     * PLACE_ENTRANCE_AND_EXIT state.
     */
    private int remainingCells;
    /**
     * The unique solution path to the maze, in order from entrance to exit, or null
     * if the solution is not being shown. The first and last cells of this list
     * will be outside the outer walls of the maze; they are the cells found by
     * proceeding directly out of the maze from the entrance and exit, respectively.
     * The second and second-to-last cells are the true entrance and exit cells
     * that are recorded in the corresponding instance variables. The first and last
     * cells are included so that the solution path is shown to extend all the way
     * out of the maze in its toString representation.
     */
    private MyList<MazeCoordinate> solution;

    public GrowingTreeMaze(int[] shape) {
        this(shape, new DefaultAlgorithm());
    }

    public GrowingTreeMaze(int[] shape, Selector selector) {
        this(shape, selector, System.nanoTime());
    }

    public GrowingTreeMaze(int[] shape, long seed) {
        this(shape, new DefaultAlgorithm(), seed);
    }

    public GrowingTreeMaze(int[] shape, Selector selector, long seed) {
        super(shape, true);
        if (getSize() == 1) {
            throw new IllegalArgumentException("maze must have more than one cell");
        }
        Require.nonNull(selector, "selector");
        random = new ReversibleRandom(seed);
        int[] indices = new int[shape.length];
        for (int i=0; i<shape.length; i++) {
            indices[i] = random.nextInt(shape[i]);
        }
        root = new MazeCoordinate(indices);
        this.selector = selector;
        visitedCells = new MyArrayList<>();
        visitedCellMatrix = new MultiDimensionalArray<>(shape, false);
        completedCells = new MyArrayList<>();
        pathDirections = new MyArrayList<>();
        state = State.PLACE_ROOT;
        remainingCells = getSize();
    }

    @Override
    public boolean isGenerationFinished() {
        return state == State.FINISHED;
    }

    private MazeCoordinate getMostDistantEdgeCell(MazeCoordinate fromCell) {
        // cells we need to visit
        MyList<MazeCoordinate> cells = new MyArrayList<>();
        // the directions we're visiting those cells from (to avoid backtracking)
        MyList<Direction> fromDirections = new MyArrayList<>();
        // the distances of those cells from the starting cell
        MyList<Integer> distances = new MyArrayList<>();
        cells.add(fromCell);
        fromDirections.add(null);
        distances.add(0);
        // the edge cell the farthest yet from the starting cell
        MazeCoordinate toCell = null;
        int greatestDistance = 0;
        while (!cells.isEmpty()) {
            MazeCoordinate cell = cells.remove();
            Direction fromDirection = fromDirections.remove();
            int distance = distances.remove();
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
        switch (state) {
            case PLACE_ROOT:
                visitedCells.add(root);
                visitedCellMatrix.set(root.getCoordinates(), true);
                remainingCells -= 1;
                state = State.GROW_TREE;
                break;
            case GROW_TREE:
                random.advanceGenerator();
                // select a cell from C
                int cellIndex = selector.select(visitedCells.size(), random);
                MazeCoordinate cell = visitedCells.get(cellIndex);
                // find neighbors of that cell that have never been in C
                MyList<MazeCoordinate> neighbors = new MyArrayList<>();
                MyList<Direction> directions = new MyArrayList<>();
                for (Direction direction : Direction.getAllDirections(getDimensionCount())) {
                    MazeCoordinate neighbor = cell.offset(direction);
                    try {
                        if (!visitedCellMatrix.get(neighbor.getCoordinates())) {
                            neighbors.add(neighbor);
                            directions.add(direction);
                        }
                    }
                    catch (IndexOutOfBoundsException e) {
                        // we have stepped out of the maze boundaries
                    }
                }
                // does it have any such neighbors?
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
                // short-circuit (avoids emptying C unnecessarily)
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
        random.resetGenerator(0);
        unsetEntranceAndExit();
        visitedCells.clear();
        visitedCellMatrix.fill(false);
        completedCells.clear();
        pathDirections.clear();
        state = State.PLACE_ROOT;
        remainingCells = getSize();
    }

    @Override
    public void reverseGeneration() {
        switch (state) {
            case FINISHED:
                addWall(getExternalFace(entrance));
                addWall(getExternalFace(exit));
                unsetEntranceAndExit();
                state = State.PLACE_ENTRANCE_AND_EXIT;
                break;
            case PLACE_ENTRANCE_AND_EXIT:
            case GROW_TREE:
                // the list-modifying operations are essentially just inverses of those
                // in advanceGeneration, but they use different information to decide
                // how to modify said lists (because we are in a different state when
                // approaching a step from the future rather than the past)
                if (!pathDirections.isEmpty()) {
                    random.resetGenerator();
                    Direction direction = pathDirections.remove();
                    boolean hasNeighbors = direction != null;
                    if (hasNeighbors) {
                        MazeCoordinate neighbor = visitedCells.remove();
                        addWall(new MazeFace(neighbor, direction.invert()));
                        visitedCellMatrix.set(neighbor.getCoordinates(), false);
                        remainingCells += 1;
                    }
                    else {
                        int cellIndex = selector.select(visitedCells.size() + 1, random);
                        visitedCells.add(cellIndex, completedCells.remove());
                    }
                    random.reverseGenerator();
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

    @Override
    public void showSolution() {
        if (state == State.FINISHED) {
            Direction[] directions = Direction.getAllDirections(getDimensionCount());
            solution = new MyArrayList<>();
            // when we find ourselves in a certain cell, from which way did we come?
            MyList<Integer> fromIndices = new MyArrayList<>();
            // and in which way to we intend to go?
            MyList<Integer> toIndices = new MyArrayList<>();
            // we initially enter from outside the maze, moving into the entrance
            Direction entranceNormalDirection = getExternalFace(entrance).getSide();
            solution.add(entrance.offset(entranceNormalDirection));
            fromIndices.add(entranceNormalDirection.toInt());
            toIndices.add(entranceNormalDirection.invert().toInt());
            // we want to reach the cell just outside the exit
            MazeCoordinate goal = exit.offset(getExternalFace(exit).getSide());
            while (true) {
                MazeCoordinate cell = solution.get();
                // have we reached the end yet?
                if (cell.equals(goal)) {
                    return;
                }
                int toIndex = toIndices.get();
                int fromIndex = fromIndices.get();
                // look for new directions in which to go until we run out of
                // directions, but skip the direction from which we came and
                // any directions that are blocked by walls
                while (toIndex < directions.length
                        && (toIndex == fromIndex || hasWall(new MazeFace(cell, directions[toIndex])))) {
                    toIndex += 1;
                }
                // continue in the next direction
                if (toIndex < directions.length) {
                    toIndices.set(toIndex + 1);
                    solution.add(cell.offset(directions[toIndex]));
                    fromIndices.add(directions[toIndex].invert().toInt());
                    toIndices.add(0);
                }
                // backtrack if we have reached a dead end or we have found a
                // dead end in every direction from an intersection
                else {
                    solution.remove();
                    fromIndices.remove();
                    toIndices.remove();
                }
            }
        }
        else {
            throw new IllegalStateException("cannot show solution for unfinished maze");
        }
    }

    @Override
    public void hideSolution() {
        solution = null;
    }

    @Override
    protected char getFrontChar(MazeCoordinate cell) {
        char c = super.getFrontChar(cell);
        if (c == ' ' && solution != null) {
            int index = solution.indexOf(cell);
            if (index != -1) {
                MazeCoordinate neighbor = cell.offset(Direction.FRONT);
                try {
                    if (solution.get(index - 1).equals(neighbor) || solution.get(index + 1).equals(neighbor)) {
                        return '.';
                    }
                } catch (IndexOutOfBoundsException e) {}
            }
        }
        return c;
    }

    @Override
    protected char getLeftChar(MazeCoordinate cell) {
        char c = super.getLeftChar(cell);
        if (c == ' ' && solution != null) {
            int index = solution.indexOf(cell);
            if (index != -1) {
                MazeCoordinate neighbor = cell.offset(Direction.LEFT);
                try {
                    if (solution.get(index - 1).equals(neighbor) || solution.get(index + 1).equals(neighbor)) {
                        return '.';
                    }
                }
                catch (IndexOutOfBoundsException e) {}
            }
        }
        return c;
    }

    @Override
    protected char getCenterChar(MazeCoordinate cell) {
        char c = super.getCenterChar(cell);
        if (c != ' ' || solution == null) {
            return c;
        }
        else {
            return solution.contains(cell) ? '.' : ' ';
        }
    }

}
