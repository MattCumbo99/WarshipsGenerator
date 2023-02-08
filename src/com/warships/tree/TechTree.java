package com.warships.tree;

import java.awt.*;
import java.io.File;
import java.nio.BufferOverflowException;
import java.util.HashMap;
import java.util.Map;

import com.warships.constants.ConnectionConstants;
import com.warships.constants.WarshipConstants;
import com.warships.loaders.PresetLoader;
import com.warships.nodes.BonusNodes;
import com.warships.nodes.EngineNode;
import com.warships.nodes.TechNode;
import com.warships.nodes.UpgradeNode;
import com.warships.raffles.DefenseRaffle;
import com.warships.raffles.GunboatRaffle;
import com.warships.raffles.Raffle;
import com.warships.raffles.TroopRaffle;
import com.warships.utils.MathUtility;
import com.warships.utils.StringUtility;

public class TechTree {

    /*
     * Notes on tree generation:
     * - Nodes should occupy every space on the map except on (0, 1).
     * - Three nodes per vertical axis
     */

    private final Map<Point, TechNode> tree;
    private final PresetLoader loader;
    private final GunboatRaffle gbeRaffle;
    private final TroopRaffle troopRaffle;
    private final DefenseRaffle defenseRaffle;

    private int gbeNodes;
    private int troopDmgNodes;
    private int troopHealthNodes;
    private int defenseHealthNodes;
    private int defenseDamageNodes;

    private int currentEngineNumber;

    private int lastTopX;
    private int lastMidX;
    private int lastBotX;

    public TechTree() {
        this.tree = new HashMap<>();
        this.loader = new PresetLoader(new File(WarshipConstants.PRESET_FILENAME));

        this.gbeRaffle = new GunboatRaffle();
        this.troopRaffle = new TroopRaffle();
        this.defenseRaffle = new DefenseRaffle();

        this.currentEngineNumber = 1;

        // Initialize total amount of buff nodes to use
        this.gbeNodes = WarshipConstants.GBE_BONUS_AMOUNT;
        this.troopDmgNodes = WarshipConstants.TROOP_DMG_BONUS_AMOUNT;
        this.troopHealthNodes = WarshipConstants.TROOP_HEALTH_BONUS_AMOUNT;
        this.defenseDamageNodes = WarshipConstants.DEFENSE_DMG_BONUS_AMOUNT;
        this.defenseHealthNodes = WarshipConstants.DEFENSE_HEALTH_BONUS_AMOUNT;

        initializeBaseNodes();
    }

    public TechNode getNode(int x, int y) {
        Point point = new Point(x, y);

        return tree.get(point);
    }

    public Point getPositionOf(String name) {
        for (Map.Entry<Point, TechNode> id : this.tree.entrySet()) {
            Point nodePos = id.getKey();

            if (id.getValue().getName().equals(name)) {
                return new Point(nodePos);
            }
        }

        return null;
    }

    public TechNode getNode(String name) {
        for (Map.Entry<Point, TechNode> id : this.tree.entrySet()) {
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
                        appendSpaces(buff, (TechNode.NODE_WIDTH / 2));
                        buff.append(WarshipConstants.VERTICAL_CONNECTOR);
                        appendSpaces(buff, (TechNode.NODE_WIDTH / 2));
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
     * @param x          X position of the node.
     * @param y          Y position of the node.
     * @param connection Instructions of the connection.
     * @return true if the connection was possible.
     */
    public boolean attemptConnection(int x, int y, String connection) {
        TechNode node = getNode(x, y);
        if (node == null) {
            return false;
        }

        TechNode leftNode = getNode(x - 1, y);
        if (ConnectionConstants.LEFT.equals(connection) && isConnectionPossible(node, leftNode)) {
            node.setNextLeftNode(leftNode);
            return true;
        }

        TechNode upperNode = getNode(x, y + 1);
        if (ConnectionConstants.UPPER.equals(connection) && isConnectionPossible(node, upperNode)) {
            node.setNextUpperNode(upperNode);
            return true;
        }

        TechNode rightNode = getNode(x + 1, y);
        if (ConnectionConstants.RIGHT.equals(connection) && isConnectionPossible(node, rightNode)) {
            node.setNextRightNode(rightNode);
            return true;
        }

        TechNode lowerNode = getNode(x, y - 1);
        if (ConnectionConstants.LOWER.equals(connection) && isConnectionPossible(node, lowerNode)) {
            node.setNextLowerNode(lowerNode);
            return true;
        }

        return false;
    }

    /**
     * Initializes the tech tree with necessary starting nodes. The requirements are as
     * follows:
     * <ul>
     *     <li>Gunboat + Landing craft nodes</li>
     *     <li>At least one attacking unit</li>
     *     <li>At least three defenses (for valid choice nodes)</li>
     * </ul>
     */
    private void initializeBaseNodes() {
        UpgradeNode gunboat = loader.unloadNode("Gunboat");
        gunboat.unlock();
        gunboat.attach(); // Starter node, set attachment manually
        insertNode(0, 2, gunboat);

        UpgradeNode landingCraft = loader.unloadNode("Landing Craft");
        landingCraft.unlock();
        landingCraft.attach(); // Starter node, set attachment manually
        insertNode(0, 0, landingCraft);

        UpgradeNode defense1 = loader.unloadNode(this.defenseRaffle.removeRandom());
        defense1.unlock();
        insertNode(1, 2, defense1);
        attemptConnection(1, 2, ConnectionConstants.LEFT);

        UpgradeNode troop = loader.unloadNode(this.troopRaffle.removeRandomImportant());
        troop.unlock();
        insertNode(1, 1, troop);
        attemptConnection(1, 1, ConnectionConstants.UPPER);

        UpgradeNode defense2 = loader.unloadNode(this.defenseRaffle.removeRandom());
        defense2.unlock();
        insertNode(1, 0, defense2);
        attemptConnection(1, 0, ConnectionConstants.LEFT);
        attemptConnection(1, 0, ConnectionConstants.UPPER);

        UpgradeNode defense3 = loader.unloadNode(this.defenseRaffle.removeRandom());
        defense3.unlock();
        insertNode(2, 1, defense3);
        attemptConnection(2, 1, ConnectionConstants.LEFT);

        UpgradeNode random1 = randomNode();
        insertNode(2, 2, random1);
        attemptConnection(2, 2, ConnectionConstants.LOWER);

        UpgradeNode random2 = randomNode();
        insertNode(2, 0, random2);
        attemptConnection(2, 0, ConnectionConstants.UPPER);

        generateVerticalNodes(this.lastBotX + 1, 2);
    }

    private void generateVerticalNodes(int starterX, int numColumns) {
        if (numColumns <= 0) {
            return;
        }

        if (numColumns > 1) {
            generateNodeColumn(starterX);
            generateVerticalNodes(++starterX, --numColumns);
        } else {
            generateEngineColumn(starterX);
        }

    }

    private void generateNodeColumn(int starterX) {
        for (int y = 0; y <= 2; y++) {
            // TODO algorithm for generating vertical nodes
            UpgradeNode node = randomNode();
            node.setEngineNumber(this.currentEngineNumber);
            insertNode(starterX, y, node);
        }

        int leftSequence = MathUtility.random(0, 6);
        int vertSequence = MathUtility.random(0, 2);

        establishLeftConnections(starterX, leftSequence);
        establishVerticalConnections(starterX, vertSequence);
    }

    private void generateEngineColumn(int starterX) {
        int engineSpot = MathUtility.random(0, 2);

        for (int y = 0; y <= 2; y++) {
            if (y == engineSpot) {
                // Insert the engine node
                EngineNode engine = new EngineNode(this.currentEngineNumber);
                insertNode(starterX, y, engine);
            } else {
                // Insert a regular node
                UpgradeNode node = randomNode();
                node.setEngineNumber(this.currentEngineNumber);
                insertNode(starterX, y, node);
            }
        }

        int leftSequence = MathUtility.random(0, 6);
        int vertSequence = MathUtility.random(0, 2);

        establishLeftConnections(starterX, leftSequence);
        establishVerticalConnections(starterX, vertSequence);

        EngineNode engineNode = (EngineNode) getNode(starterX, engineSpot);
        if (!engineNode.isAttached()) {
            // Rogue engine node detected. Connect it with the main tree
            attemptConnection(starterX, engineSpot, ConnectionConstants.LEFT);
        }

        this.currentEngineNumber++;
    }

    private void establishVerticalConnections(int columnX, int sequenceNumber) {
        switch (sequenceNumber) {
            case 0:
                attemptConnection(columnX, 0, ConnectionConstants.UPPER);
                break;
            case 1:
                attemptConnection(columnX, 1, ConnectionConstants.UPPER);
                break;
            case 2:
                attemptConnection(columnX, 0, ConnectionConstants.UPPER);
                attemptConnection(columnX, 1, ConnectionConstants.UPPER);
                break;
            default:
                throw new UnsupportedOperationException("Sequence number not supported: " + sequenceNumber);
        }
    }

    private void establishLeftConnections(int columnX, int sequenceNumber) {
        switch (sequenceNumber) {
            case 0:
            case 1:
            case 2:
                attemptConnection(columnX, sequenceNumber, ConnectionConstants.LEFT);
                break;
            case 3:
                attemptConnection(columnX, 0, ConnectionConstants.LEFT);
                attemptConnection(columnX, 1, ConnectionConstants.LEFT);
                break;
            case 4:
                attemptConnection(columnX, 1, ConnectionConstants.LEFT);
                attemptConnection(columnX, 2, ConnectionConstants.LEFT);
                break;
            case 5:
                attemptConnection(columnX, 0, ConnectionConstants.LEFT);
                attemptConnection(columnX, 2, ConnectionConstants.LEFT);
                break;
            case 6:
                attemptConnection(columnX, 0, ConnectionConstants.LEFT);
                attemptConnection(columnX, 1, ConnectionConstants.LEFT);
                attemptConnection(columnX, 2, ConnectionConstants.LEFT);
                break;
            default:
                throw new UnsupportedOperationException("Sequence number not supported: " + sequenceNumber);
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
        if (loader.isEmpty() && !hasBonusNodes()) {
            return null;
        }

        int type = MathUtility.random(0, 7);
        boolean repeated = false;

        while (true) {
            switch (type) {
                case 0:
                    // Troop was chosen
                    if (!troopRaffle.isEmpty()) {
                        return loader.unloadNode(troopRaffle.removeRandom());
                    }
                case 1:
                    // Defense was chosen
                    if (!defenseRaffle.isEmpty()) {
                        return loader.unloadNode(defenseRaffle.removeRandom());
                    }
                case 2:
                    // Gunboat ability was chosen
                    if (!gbeRaffle.isEmpty()) {
                        return loader.unloadNode(gbeRaffle.removeRandom());
                    }
                case 3:
                    // Bonus GBE node
                    if (this.gbeNodes > 0) {
                        this.gbeNodes--;
                        return BonusNodes.GBE;
                    }
                case 4:
                    // Bonus Troop Damage node
                    if (this.troopDmgNodes > 0) {
                        this.troopDmgNodes--;
                        return BonusNodes.TROOP_DAMAGE;
                    }
                case 5:
                    // Bonus defense damage node
                    if (this.defenseDamageNodes > 0) {
                        this.defenseDamageNodes--;
                        return BonusNodes.BUILDING_DAMAGE;
                    }
                case 6:
                    // Bonus troop health node
                    if (this.troopHealthNodes > 0) {
                        this.troopHealthNodes--;
                        return BonusNodes.TROOP_HEALTH;
                    }
                case 7:
                    // Bonus defense health node
                    if (this.defenseHealthNodes > 0) {
                        this.defenseHealthNodes--;
                        return BonusNodes.BUILDING_HEALTH;
                    }

                    if (repeated) {
                        throw new BufferOverflowException();
                    }

                    // Setting the type to 0 will allow iteration over each
                    // possible option for using a random node.
                    type = 0;
                    repeated = true;
                    break;
                default:
                    throw new RuntimeException("Random number contains unknown value: " + type);
            }
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
        if (x < 0 || y < 0 || y > 2) {
            throw new ArrayIndexOutOfBoundsException("Node position is out of bounds: { x:" + x + ", y:" + y + " }");
        }

        if (x == 0 && y == 1) {
            throw new IllegalArgumentException("Position { x:0, y:1 } is restricted.");
        }
    }

    private boolean hasBonusNodes() {
        return this.defenseHealthNodes > 0 || this.troopHealthNodes > 0 || this.defenseDamageNodes > 0
                || this.troopDmgNodes > 0 || this.gbeNodes > 0;
    }

    private static UpgradeNode useNodeForRandom(PresetLoader preloader, Raffle raffle) {
        if (!raffle.isEmpty()) {
            return preloader.unloadNode(raffle.removeRandom());
        }

        return null;
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
