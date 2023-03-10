package com.warships.tree;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import com.warships.constants.ConnectionConstants;
import com.warships.nodes.EngineNode;
import com.warships.nodes.TechNode;
import com.warships.utils.MathUtility;

public class MazeGenerator {

    private static final int mazeY = 2;

    private final TechNode[][] maze;
    private final int mazeX;
    private final int startPos;

    private boolean isGenerated;

    public MazeGenerator(Map<Point, TechNode> tree, int startPos, int treeWidth) {
        this.startPos = startPos;
        assertUninitializedTree(tree);

        this.maze = new TechNode[3][treeWidth];
        this.mazeX = treeWidth;
        this.isGenerated = false;

        initializeMaze(tree);
    }

    /**
     * Generates the maze. This method should only be called once per object.
     */
    public void generateMaze() {
        if (this.isGenerated) {
            throw new IllegalStateException("Maze has already been generated.");
        }

        generatePath(this.startPos, 0);
        connectMaze();
        this.isGenerated = true;
    }

    /**
     * Gets the resulting tech tree generated by this maze generator. The maze must
     * have called the {@link #generateMaze()} prior to this method.
     *
     * @return The resulting tech tree map.
     */
    public Map<Point, TechNode> getResult() {
        if (!this.isGenerated) {
            throw new IllegalStateException("Maze has not been generated.");
        }

        Map<Point, TechNode> result = new HashMap<>();

        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[y].length; x++) {
                Point point = new Point(x, y);
                TechNode node = maze[y][x];

                result.put(point, node);
            }
        }

        return result;
    }

    private void generatePath(int posX, int posY) {
        DIR[] directions = DIR.values();
        // Randomize the directions
        Collections.shuffle(Arrays.asList(directions));

        for (DIR direction : directions) {
            int nextNodeX = posX + direction.dx;
            int nextNodeY = posY + direction.dy;

            if (isInRange(nextNodeX, nextNodeY)) {
                TechNode nextNode = this.maze[nextNodeY][nextNodeX];

                if (nextNode != null && between(nextNodeX, this.mazeX+1) && between(nextNodeY, mazeY+1)
                        && (!nextNode.hasAny() || !nextNode.isAttached())) {
                    connectNodes(posX, posY, nextNodeX, nextNodeY, direction.bit);

                    generatePath(nextNodeX, nextNodeY);
                }
            }
        }
    }

    /**
     * Initializes the maze array.
     *
     * @param tree Map of points to base the array off of.
     */
    private void initializeMaze(Map<Point, TechNode> tree) {
        for (Map.Entry<Point, TechNode> entry: tree.entrySet()) {
            Point nodeLocation = entry.getKey();
            int posX = (int) nodeLocation.getX();
            int posY = (int) nodeLocation.getY();

            TechNode nodeData = entry.getValue();

            this.maze[posY][posX] = nodeData;
        }

        this.maze[0][this.startPos].attach();
    }

    private boolean isInRange(int x, int y) {
        return x >= 2 && x <= this.mazeX-1 && y >= 0 && y <= mazeY;
    }

    private void connectNodes(int firstX, int firstY, int secondX, int secondY, String connection) {
        switch (connection) {
            case ConnectionConstants.LEFT:
                this.maze[firstY][firstX].setNextLeftNode(this.maze[secondY][secondX]);
                break;
            case ConnectionConstants.RIGHT:
                this.maze[firstY][firstX].setNextRightNode(this.maze[secondY][secondX]);
                break;
            case ConnectionConstants.LOWER:
                this.maze[firstY][firstX].setNextUpperNode(this.maze[secondY][secondX]);
                break;
            case ConnectionConstants.UPPER:
                this.maze[firstY][firstX].setNextLowerNode(this.maze[secondY][secondX]);
                break;
            default:
                throw new UnsupportedOperationException("Unknown connection instruction: " + connection);
        }
    }

    private static boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }

    private void assertUninitializedTree(Map<Point, TechNode> nodes) {
        for (int y = 0; y <= 2; y++) {
            if (nodes.get(new Point(this.startPos, y)).hasLeft()) {
                throw new RejectedExecutionException("Node at (2, " + y + ") has a righter attachment. Cannot generate maze.");
            }
        }
    }

    /**
     * Attaches the generated maze to the rest of the tree.
     */
    private void connectMaze() {
        // TODO Optimize this routine
        if (this.startPos == 3) {
            // First engine room can connect to any of the starting nodes
            final int mainConnector = MathUtility.random(0, 2);
            this.maze[mainConnector][this.startPos-1].setNextRightNode(this.maze[mainConnector][this.startPos]);

            // Determine if we should place another connector
            boolean placeSecondConnector = MathUtility.chance(0.35);

            if (placeSecondConnector) {
                for (int y = 2; y >= 0; y--) {
                    if (!this.maze[y][this.startPos-1].hasRight()) {
                        this.maze[y][this.startPos-1].setNextRightNode(this.maze[y][this.startPos]);
                        break;
                    }
                }
            }
        } else {
            // Find the engine node from the previous maze and connect it
            for (int y = 0; y <= 2; y++) {
                if (this.maze[y][this.startPos-1] instanceof EngineNode) {
                    this.maze[y][this.startPos-1].setNextRightNode(this.maze[y][this.startPos]);
                    break;
                }
            }
        }
    }
}

enum DIR {
    N(ConnectionConstants.UPPER, 0, -1), S(ConnectionConstants.LOWER, 0, 1),
    E(ConnectionConstants.RIGHT, 1, 0), W(ConnectionConstants.LEFT, -1, 0);
    public final String bit;
    public final int dx;
    public final int dy;
    public DIR opposite;

    // use the static initializer to resolve forward references
    static {
        N.opposite = S;
        S.opposite = N;
        E.opposite = W;
        W.opposite = E;
    }

    DIR(String bit, int dx, int dy) {
        this.bit = bit;
        this.dx = dx;
        this.dy = dy;
    }
}
