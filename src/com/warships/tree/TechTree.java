package com.warships.tree;

import com.warships.constants.ConnectionConstants;
import com.warships.loaders.PresetLoader;
import com.warships.nodes.TechNode;
import com.warships.nodes.UpgradeNode;
import com.warships.raffles.DefenseRaffle;
import com.warships.raffles.GunboatRaffle;
import com.warships.constants.WarshipConstants;
import com.warships.raffles.TroopRaffle;
import com.warships.utils.StringUtility;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TechTree {

    /*
     * Notes on tree generation:
     * - Nodes should occupy every space on the map except on (0, 1).
     * - Three nodes per vertical axis
     */

    private Map<Point, TechNode> tree;
    private PresetLoader loader;
    private GunboatRaffle gbeRaffle;
    private TroopRaffle troopRaffle;
    private DefenseRaffle defenseRaffle;
    private int lastTopX;
    private int lastMidX;
    private int lastBotX;

    public TechTree() {
        this.tree = new HashMap<>();
        this.loader = new PresetLoader(new File(WarshipConstants.PRESET_FILENAME));

        this.gbeRaffle = new GunboatRaffle();
        this.troopRaffle = new TroopRaffle();
        this.defenseRaffle = new DefenseRaffle();

        initializeBaseNodes();
    }

    public TechNode getNode(int x, int y) {
        Point point = new Point(x, y);

        return tree.get(point);
    }

    public Point getPositionOf(String name) {
        for (Map.Entry<Point, TechNode> id: this.tree.entrySet()) {
            Point nodePos = id.getKey();

            if (id.getValue().getName().equals(name)) {
                return nodePos;
            }
        }

        return null;
    }

    public TechNode getNode(String name) {
        for (Map.Entry<Point, TechNode> id: this.tree.entrySet()) {
            String nodeName = id.getValue().getName();

            if (nodeName.equals(name)) {
                return id.getValue();
            }
        }

        return null;
    }

    public boolean buyUpgrade(int x, int y) {
        if (getNode(x, y) instanceof UpgradeNode) {
            UpgradeNode node = (UpgradeNode) getNode(x, y);
            node.upgrade();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Outputs the tech tree to the console.
     */
    public void displayNodes() {
        StringBuilder nodes = new StringBuilder();
        // Buffer string for connectors between vertical nodes
        StringBuilder buff = new StringBuilder();

        for (int y = 2; y >= 0; y--) {
            int maxX = getMaxValueX(y);

            for (int x = 0; x <= maxX; x++) {
                TechNode currentNode = getNode(x, y);

                if (currentNode != null) {
                    // Display the node
                    nodes.append(currentNode);

                    appendHorizontalConnection(nodes, currentNode.hasRight());

                    // Check for a lower connection
                    if (currentNode.hasLower()) {
                        // Add connector to buffer line
                        appendSpaces(buff, (TechNode.NODE_WIDTH/2));
                        buff.append(WarshipConstants.VERTICAL_CONNECTOR);
                        appendSpaces(buff, (TechNode.NODE_WIDTH/2));
                    } else {
                        appendSpaces(buff, TechNode.NODE_WIDTH + WarshipConstants.HORIZONTAL_CONNECTION_LENGTH);
                    }
                } else {
                    // Empty spot for a node
                    appendSpaces(nodes, TechNode.NODE_WIDTH + WarshipConstants.HORIZONTAL_CONNECTION_LENGTH);
                    appendSpaces(buff, TechNode.NODE_WIDTH + WarshipConstants.HORIZONTAL_CONNECTION_LENGTH);
                }
            }

            System.out.println(nodes);
            System.out.println(buff);
            nodes.setLength(0);
            buff.setLength(0);
        }
    }

    /**
     * Attempts to connect a specified node to its neighbors using set instructions.
     * <br>
     * Instructions can be appended for multiple attachments at once. For example,
     * using "LeftRight" will attempt to make the node at <code>x, y</code> connected
     * to the nodes to the left and right.
     * <br>
     * If the instructions are not possible, nothing is changed.
     *
     * @param x X position of the node.
     * @param y Y position of the node.
     * @param connection Instructions of the connection.
     */
    public void attemptConnection(int x, int y, String connection) {
        TechNode node = getNode(x, y);
        if (node == null) {
            return;
        }

        TechNode leftNode = getNode(x - 1, y);
        if (connection.contains(ConnectionConstants.LEFT) && isConnectionPossible(node, leftNode)) {
            node.setNextLeftNode(leftNode);
        }

        TechNode upperNode = getNode(x, y + 1);
        if (connection.contains(ConnectionConstants.UPPER) && isConnectionPossible(node, upperNode)) {
            node.setNextUpperNode(upperNode);
        }

        TechNode rightNode = getNode(x + 1, y);
        if (connection.contains(ConnectionConstants.RIGHT) && isConnectionPossible(node, rightNode)) {
            node.setNextRightNode(rightNode);
        }

        TechNode lowerNode = getNode(x, y - 1);
        if (connection.contains(ConnectionConstants.LOWER) && isConnectionPossible(node, lowerNode)) {
            node.setNextLowerNode(lowerNode);
        }
    }

    private void initializeBaseNodes() {
        UpgradeNode gunboat = loader.unloadNode("Gunboat");
        gunboat.unlock();
        insertNode(0, 2, gunboat);

        UpgradeNode landingCraft = loader.unloadNode("Landing Craft");
        landingCraft.unlock();
        insertNode(0, 0, landingCraft);

        UpgradeNode defense1 = loader.unloadNode(this.defenseRaffle.removeRandom());
        defense1.unlock();
        insertNode(1, 2, defense1);
        attemptConnection(1, 2, "Left");

        UpgradeNode troop = loader.unloadNode(this.troopRaffle.removeRandomImportant());
        troop.unlock();
        insertNode(1, 1, troop);
        attemptConnection(1, 1, "Upper");

        UpgradeNode defense2 = loader.unloadNode(this.defenseRaffle.removeRandom());
        defense2.unlock();
        insertNode(1, 0, defense2);
        attemptConnection(1, 0, "LeftUpper");

        UpgradeNode defense3 = loader.unloadNode(this.defenseRaffle.removeRandom());
        defense3.unlock();
        insertNode(2, 1, defense3);
        attemptConnection(2, 1, "Left");
    }

    private void generateVerticalNodes(int x) {
        for (int y = 0; y < 3; y++) {
            if (getNode(x, y) != null) {
                // TODO algorithm for generating vertical nodes
            }
        }
    }

    private int getMaxValueX(int y) {
        switch (y) {
            case 2:
                return this.lastTopX;
            case 1:
                return this.lastMidX;
            case 0:
                return this.lastBotX;
            default:
                throw new ArrayIndexOutOfBoundsException("Index of Y:" + y);
        }
    }

    private UpgradeNode randomNode() {
        if (loader.isEmpty()) {
            return null;
        }

        int type = (int) Math.floor(Math.random() * 2);
        switch (type) {
            case 0:
                // Troop was chosen
                return loader.unloadNode(troopRaffle.removeRandom());
            case 1:
                // Defense was chosen
                return loader.unloadNode(defenseRaffle.removeRandom());
            case 2:
                // Gunboat ability was chosen
                return loader.unloadNode(gbeRaffle.removeRandom());
            default:
                throw new RuntimeException("Random number exceeded 2: " + type);
        }
    }

    private void insertNode(int x, int y, TechNode node) {
        assertValidPosition(x, y);

        switch (y) {
            case 0:
                challengeBotValue(x);
                break;
            case 1:
                challengeMidValue(x);
                break;
            case 2:
                challengeTopValue(x);
                break;
            default:
                // This will never get reached due to assertion of valid position
                throw new ArrayIndexOutOfBoundsException("Index of Y:" + y);
        }

        tree.put(new Point(x, y), node);
    }

    private void challengeBotValue(int x) {
        if (x > this.lastBotX) {
            this.lastBotX = x;
        }
    }

    private void challengeMidValue(int x) {
        if (x > this.lastMidX) {
            this.lastMidX = x;
        }
    }

    private void challengeTopValue(int x) {
        if (x > this.lastTopX) {
            this.lastTopX = x;
        }
    }

    private void assertValidPosition(int x, int y) {
        if (x < 0 || y < 0 || y > 2 || x > getMaxValueX(y)+1) {
            throw new ArrayIndexOutOfBoundsException("Node position is out of bounds: { x:" + x + ", y:" + y + " }");
        }

        if (x == 0 && y == 1) {
            throw new IllegalArgumentException("Position { x:0, y:1 } is restricted.");
        }
    }

    private static boolean isConnectionPossible(TechNode node1, TechNode node2) {
        return node1 != null && node2 != null && node1.isSameEngine(node2);
    }

    private static void appendSpaces(StringBuilder str, int spaces) {
        str.append(StringUtility.repeat(WarshipConstants.BLANK_SPACE, Math.max(0, spaces)));
    }

    private static void appendHorizontalConnection(StringBuilder builder, boolean connected) {
        for (int i = 0; i < WarshipConstants.HORIZONTAL_CONNECTION_LENGTH; i++) {
            if (connected) {
                builder.append(WarshipConstants.HORIZONTAL_CONNECTOR);
            } else {
                builder.append(WarshipConstants.BLANK_SPACE);
            }
        }
    }
}
