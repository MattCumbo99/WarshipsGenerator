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
import com.warships.nodes.ChoiceNode;
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

    private Map<Point, TechNode> tree;
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
    private int choiceNodes;

    private int lastTopX;
    private int lastMidX;
    private int lastBotX;

    /**
     * Initializes a Warships tech tree with required nodes.
     */
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
        this.choiceNodes = 12;

        // Total nodes = 75

        initializeBaseNodes();
    }

    /**
     * Generates the tech tree.
     */
    public void generate() {
        // Engine room 1 (Two engines)
        insertEngineRoom(2);
        // Engine room 2 (Three engines)
        insertEngineRoom(5);
        // Engine room 3 (Four engines)
        insertEngineRoom(3);
        // Engine room 4 (Five engines)
        insertEngineRoom(4);
        // Engine room 5 (Six engines)
        insertEngineRoom(2);
        // TODO Generate the last engine room
    }

    /**
     * Generates and inserts a new engine room layout to the tech tree.
     *
     * @param totalColumns Total amount of columns this engine room should have.
     */
    private void insertEngineRoom(int totalColumns) {
        int columnStartPos = this.lastBotX + 1;

        generateVerticalNodes(totalColumns);

        int columnEndPos = this.lastBotX + 1;

        MazeGenerator generator = new MazeGenerator(this.tree, columnStartPos, columnEndPos);
        generator.generateMaze();

        this.tree = generator.getResult();
    }

    /**
     * Retrieves the node at a specified position.
     *
     * @param x The X position of the node.
     * @param y The Y position of the node.
     * @return The node located at (x, y), or <code>null</code> if no node exists at this location.
     */
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
        TechNode node = getNode(x, y);
        if ((node instanceof UpgradeNode) && node.canBeUnlocked()) {
            UpgradeNode upnode = (UpgradeNode) node;
            return upnode.upgrade();
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

        final int connectorSpace = TechNode.NODE_WIDTH + WarshipConstants.HORIZONTAL_CONNECTION_LENGTH;

        for (int y = 2; y >= 0; y--) {
            int maxX = getMaxValueX(y);

            for (int x = 0; x <= maxX; x++) {
                TechNode currentNode = getNode(x, y);

                // Check if a node is present
                if (currentNode != null) {
                    // Display the node
                    nodes.append(currentNode);

                    appendHorizontalConnection(nodes, currentNode.hasRight());

                    // Check for a lower connection
                    if (currentNode.hasLower() && y > 0) {
                        // Add connector to buffer line
                        final int nodeFirstHalfWidth = WarshipConstants.NODE_NAME_MIN_WIDTH + 1;
                        final int firstHalfWithConnector = nodeFirstHalfWidth - WarshipConstants.VERTICAL_CONNECTOR.length();

                        appendSpaces(buff, firstHalfWithConnector);
                        buff.append(WarshipConstants.VERTICAL_CONNECTOR);
                        // Append spaces equal to the amount of the remaining width of the node
                        appendSpaces(buff, TechNode.NODE_WIDTH - nodeFirstHalfWidth);
                        // Fill in the space where a connector should be
                        appendSpaces(buff, WarshipConstants.HORIZONTAL_CONNECTION_LENGTH);
                    } else if (y == 0) {
                        // Display the X axis labels
                        appendGridX(buff, x);
                    } else {
                        // Empty connection
                        appendSpaces(buff, connectorSpace);
                    }
                } else {
                    // Empty spot for a node
                    appendSpaces(nodes, connectorSpace);
                    appendSpaces(buff, connectorSpace);
                }
            }

            System.out.println(nodes);
            System.out.println(buff);
            nodes.setLength(0);
            buff.setLength(0);
        }
    }

    /**
     * Attempts to connect a specified node to a neighbor. If the instruction
     * is not possible, nothing is changed.
     *
     * @param x          X position of the node.
     * @param y          Y position of the node.
     * @param connection The connection instruction.
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
        // Initialize the gunboat node
        UpgradeNode gunboat = loader.unloadNode("Gunboat");
        gunboat.unlock();
        gunboat.attach(); // Starter node, set attachment manually
        insertNode(0, 2, gunboat);

        // Initialize the landing craft node
        UpgradeNode landingCraft = loader.unloadNode("Landing Craft");
        landingCraft.unlock();
        landingCraft.attach(); // Starter node, set attachment manually
        insertNode(0, 0, landingCraft);

        // Defensive node 1 (right of gunboat node)
        UpgradeNode defense1 = loader.unloadNode(this.defenseRaffle.removeNonOverpowered());
        defense1.unlock();
        insertNode(1, 2, defense1);
        attemptConnection(1, 2, ConnectionConstants.LEFT);

        // Necessary troop node (second node in middle row)
        UpgradeNode troop = loader.unloadNode(this.troopRaffle.removeFirstChoice());
        troop.unlock();
        insertNode(1, 1, troop);
        attemptConnection(1, 1, ConnectionConstants.UPPER);

        // Defensive node 2 (right of landing craft node)
        UpgradeNode defense2 = loader.unloadNode(this.defenseRaffle.removeNonOverpowered());
        defense2.unlock();
        insertNode(1, 0, defense2);
        attemptConnection(1, 0, ConnectionConstants.LEFT);
        attemptConnection(1, 0, ConnectionConstants.UPPER);

        // Defensive node 3 (right of troop node)
        UpgradeNode defense3 = loader.unloadNode(this.defenseRaffle.removeNonOverpowered());
        defense3.unlock();
        insertNode(2, 1, defense3);
        attemptConnection(2, 1, ConnectionConstants.LEFT);

        // At this point, the tree meets the minimum requirements of having at least three
        // defenses (for generating choice nodes) and one troop node. So we can start the
        // process of generating random nodes.

        // Random node 1 (top row)
        UpgradeNode random1 = randomUpgradeNode();
        insertNode(2, 2, random1);
        attemptConnection(2, 2, ConnectionConstants.LOWER);

        // Random node 2 (bottom row)
        UpgradeNode random2 = randomUpgradeNode();
        insertNode(2, 0, random2);
        attemptConnection(2, 0, ConnectionConstants.UPPER);
    }

    /**
     * Inserts a specified number of columns into the tech tree. The last column
     * will always contain an engine node.
     *
     * @param numColumns Number of columns to generate.
     */
    private void generateVerticalNodes(int numColumns) {
        _generateVerticalNodes(this.lastBotX + 1, numColumns);
        this.currentEngineNumber++;
    }

    /**
     * Recursively inserts a specified number of columns into the tech tree. The last column
     * will always contain an engine node.
     *
     * @param starterX The next empty column position.
     * @param numColumns Number of columns to generate.
     */
    private void _generateVerticalNodes(int starterX, int numColumns) {
        if (numColumns <= 0) {
            return;
        }

        if (numColumns > 1) {
            // Generate a regular node column
            generateNodeColumn(starterX);
            _generateVerticalNodes(++starterX, --numColumns);
        } else {
            // Last column, insert the end (engine node)
            generateEngineColumn(starterX);
        }
    }

    /**
     * Inserts three unattached regular nodes in a column.
     *
     * @param starterX The column number to generate at. There should be no nodes at this position.
     */
    private void generateNodeColumn(int starterX) {
        for (int y = 0; y <= 2; y++) {
            TechNode node = randomNode();
            insertNode(starterX, y, node);
        }
    }

    /**
     * Appends an X axis label to a string builder.
     *
     * @param builder StringBuilder to append to.
     * @param x X position.
     */
    private static void appendGridX(StringBuilder builder, int x) {
        final int leftHalf = WarshipConstants.NODE_NAME_MIN_WIDTH + 1;
        builder.append(StringUtility.repeat(WarshipConstants.BLANK_SPACE, leftHalf));

        builder.append(x);

        if (x < 10) {
            builder.append(WarshipConstants.BLANK_SPACE);
        }

        final int secondHalf = (TechNode.NODE_WIDTH - leftHalf) - 2;
        builder.append(StringUtility.repeat(WarshipConstants.BLANK_SPACE, secondHalf));

        appendSpaces(builder, WarshipConstants.HORIZONTAL_CONNECTION_LENGTH);
    }

    /**
     * Inserts two unattached regular nodes and one engine node in a column.
     *
     * @param starterX The column number to generate at. There should be no nodes at this position.
     */
    private void generateEngineColumn(int starterX) {
        int engineSpot = MathUtility.random(0, 2);

        for (int y = 0; y <= 2; y++) {
            if (y == engineSpot) {
                // Insert the engine node
                EngineNode engine = new EngineNode(this.currentEngineNumber);
                insertNode(starterX, y, engine);
            } else {
                // Insert a regular node
                TechNode node = randomNode();
                insertNode(starterX, y, node);
            }
        }
    }

    /**
     * Gets the last column position for a specified row.
     *
     * @param y Row number.
     * @return The last columns position.
     */
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

    /**
     * Unloads a random non-engine node from the pool, or <code>null</code> if no more
     * nodes could be pulled.
     *
     * @return The removed node.
     */
    private TechNode randomNode() {
        int choice = MathUtility.random(0, 8);

        if (choice == 8 && this.choiceNodes > 0) {
            // The random selection is a choice node
            this.choiceNodes--;
            return new ChoiceNode(this.defenseRaffle);
        } else {
            return randomUpgradeNode();
        }
    }

    /**
     * Unloads a random upgradable node from the pool, or <code>null</code> if no more
     * nodes could be pulled.
     *
     * @return The removed node.
     */
    private UpgradeNode randomUpgradeNode() {
        if (loader.isEmpty() && !hasBonusNodes()) {
            return null;
        }

        int type = MathUtility.random(0, 7);
        boolean repeated = false;

        while (true) {
            /*
             * This switch statement intentionally does not have break statements
             * to allow iteration over alternative options.
             */
            switch (type) {
                case 0:
                    // Troop was chosen
                    if (!troopRaffle.isEmpty()) {
                        if (this.currentEngineNumber == 1) {
                            // Use a simple option for the first engine room
                            return loader.unloadNode(troopRaffle.removeFirstChoice());
                        } else if (this.currentEngineNumber < 4) {
                            // The tree can afford more advanced troop options
                            // after the specified engine number
                            return loader.unloadNode(troopRaffle.removeNonOverpowered());
                        } else {
                            // Anything goes in the later engine rooms
                            return loader.unloadNode(troopRaffle.removeRandom());
                        }
                    }
                case 1:
                    // Defense was chosen
                    if (!defenseRaffle.isEmpty()) {
                        if (this.currentEngineNumber > 1) {
                            return loader.unloadNode(defenseRaffle.removeRandom());
                        } else {
                            // To ensure a fair tree, avoid strong defenses in the
                            // first engine room.
                            return loader.unloadNode(defenseRaffle.removeNonOverpowered());
                        }
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
                        return new UpgradeNode(BonusNodes.GBE);
                    }
                case 4:
                    // Bonus Troop Damage node
                    if (this.troopDmgNodes > 0) {
                        this.troopDmgNodes--;
                        return new UpgradeNode(BonusNodes.TROOP_DAMAGE);
                    }
                case 5:
                    // Bonus defense damage node
                    if (this.defenseDamageNodes > 0) {
                        this.defenseDamageNodes--;
                        return new UpgradeNode(BonusNodes.BUILDING_DAMAGE);
                    }
                case 6:
                    // Bonus troop health node
                    if (this.troopHealthNodes > 0) {
                        this.troopHealthNodes--;
                        return new UpgradeNode(BonusNodes.TROOP_HEALTH);
                    }
                case 7:
                    // Bonus defense health node
                    if (this.defenseHealthNodes > 0) {
                        this.defenseHealthNodes--;
                        return new UpgradeNode(BonusNodes.BUILDING_HEALTH);
                    }

                    if (repeated) {
                        // For some reason, we could not locate any possible node.
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

    /**
     * Inserts a node into the tech tree. The corresponding last position of the inserted node
     * is automatically updated.
     *
     * @param x The X position of the node.
     * @param y The Y position of the node.
     * @param node The node to insert.
     */
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

    /**
     * Sets the last column position of the bottom row, should the provided value
     * be greater.
     *
     * @param x Value to compare.
     */
    private void challengeBotValue(int x) {
        if (x > this.lastBotX) {
            this.lastBotX = x;
        }
    }

    /**
     * Sets the last column position of the middle row, should the provided value
     * be greater.
     *
     * @param x Value to compare.
     */
    private void challengeMidValue(int x) {
        if (x > this.lastMidX) {
            this.lastMidX = x;
        }
    }

    /**
     * Sets the last column position of the top row, should the provided value
     * be greater.
     *
     * @param x Value to compare.
     */
    private void challengeTopValue(int x) {
        if (x > this.lastTopX) {
            this.lastTopX = x;
        }
    }

    /**
     * Ensures the provided position is a valid spot on the tree.
     * <br>
     * Some examples of invalid nodes are:
     * <ul>
     *     <li>[-1, 0]: Out of bounds X position.</li>
     *     <li>[0, 1]: Restricted position (this spot should always be empty).</li>
     *     <li>[0, -1]: Out of bounds Y position.</li>
     *     <li>[1, 3]: Out of bounds Y position.</li>
     * </ul>
     *
     * @param x The X position to validate.
     * @param y The Y position to validate.
     */
    private void assertValidPosition(int x, int y) {
        if (x < 0 || y < 0 || y > 2) {
            throw new ArrayIndexOutOfBoundsException("Node position is out of bounds: { x:" + x + ", y:" + y + " }");
        }

        if (x == 0 && y == 1) {
            throw new IllegalArgumentException("Position { x:0, y:1 } is restricted.");
        }
    }

    /**
     * Checks if this tech tree still has any bonus nodes left.
     *
     * @return true if any of the bonus node amounts are at least 1.
     */
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

    /**
     * Checks if a connection could be made between two nodes. A connection is
     * possible if both nodes are non-null and are in the same engine room.
     *
     * @param node1 First node to compare.
     * @param node2 Second node to compare.
     * @return true if both nodes are in the same engine room.
     */
    private static boolean isConnectionPossible(TechNode node1, TechNode node2) {
        return node1 != null && node2 != null && node1.isSameEngine(node2);
    }

    /**
     * Inserts a specified amount of spaces into a string builder object.
     *
     * @param str StringBuilder to use.
     * @param spaces Amount of spaces.
     */
    private static void appendSpaces(StringBuilder str, int spaces) {
        str.append(StringUtility.repeat(WarshipConstants.BLANK_SPACE, Math.max(0, spaces)));
    }

    /**
     * Inserts a connector string to a string builder object. This method is intended for use
     * within the display function only.
     *
     * @param builder StringBuilder to use (should contain nodes).
     * @param connected If the function should append a connection.
     */
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
