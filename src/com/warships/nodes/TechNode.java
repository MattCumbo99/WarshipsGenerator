package com.warships.nodes;

import com.warships.constants.WarshipConstants;
import com.warships.utils.StringUtility;

public class TechNode {

    public static final int NODE_WIDTH = WarshipConstants.NODE_NAME_MIN_WIDTH + 16;

    public static String emptyNodeString() {
        return StringUtility.repeat(WarshipConstants.BLANK_SPACE, TechNode.NODE_WIDTH + 3);
    }

    private String name;
    private int unlockCost;
    private int engineNumber;
    private boolean isUnlocked;
    private boolean isAttached;
    private TechNode nextLeftNode;
    private TechNode nextUpperNode;
    private TechNode nextRightNode;
    private TechNode nextLowerNode;

    public TechNode() {
        this.name = "?";
        this.engineNumber = 1;
        this.isUnlocked = false;
        this.unlockCost = 30;
        this.isAttached = false;
        this.nextLeftNode = null;
        this.nextLowerNode = null;
        this.nextRightNode = null;
        this.nextUpperNode = null;
    }

    public TechNode(String name, int unlockCost) {
        this();
        this.name = name;
        this.unlockCost = unlockCost;
    }

    /**
     * Gets the name associated with this node.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the node.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks if this node is attached to the main tech tree and
     * if an unlock path to it is possible.
     *
     * @return true if this node is attached.
     */
    public boolean isAttached() {
        return isAttached;
    }

    /**
     * Marks this node as attached to the main tree.
     */
    public void attach() {
        isAttached = true;
    }

    /**
     * Gets the unlock cost of this node.
     *
     * @return the unlock cost.
     */
    public int getUnlockCost() {
        return unlockCost;
    }

    /**
     * Sets the unlock cost of this node.
     *
     * @param unlockCost The unlock cost.
     */
    public void setUnlockCost(int unlockCost) {
        this.unlockCost = unlockCost;
    }

    /**
     * Unlocks the node and allows neighboring nodes to be unlocked.
     */
    public void unlock() {
        this.isUnlocked = true;
    }

    /**
     * Checks if this node is unlocked.
     *
     * @return true if this node is unlocked.
     */
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /**
     * Gets the node connected to the left of this node.
     *
     * @return the node to the left.
     */
    public TechNode getNextLeftNode() {
        return nextLeftNode;
    }

    /**
     * Sets the left connection of this node.
     *
     * @param nextLeftNode Node to connect.
     */
    public void setNextLeftNode(TechNode nextLeftNode) {
        if (nextLeftNode != null) {
            this.nextLeftNode = nextLeftNode;
            nextLeftNode.nextRightNode = this;

            if (!this.isAttached) {
                this.isAttached = nextLeftNode.isAttached();
            }

            attemptAttachment();
        } else if (this.nextLeftNode != null) {
            // Disconnect the node
            this.nextLeftNode.nextRightNode = null;
            this.nextLeftNode = null;
        }
    }

    /**
     * Gets the node connected above this node.
     *
     * @return the upper connected node.
     */
    public TechNode getNextUpperNode() {
        return nextUpperNode;
    }

    /**
     * Sets the upper connection of this node.
     *
     * @param nextUpperNode The node to connect.
     */
    public void setNextUpperNode(TechNode nextUpperNode) {
        if (nextUpperNode != null) {
            this.nextUpperNode = nextUpperNode;
            nextUpperNode.nextLowerNode = this;

            if (!this.isAttached) {
                this.isAttached = nextUpperNode.isAttached();
            }

            attemptAttachment();
        } else if (this.nextUpperNode != null) {
            // Disconnect the node
            this.nextUpperNode.nextLowerNode = null;
            this.nextUpperNode = null;
        }
    }

    /**
     * Gets the node connected to the right of this node.
     *
     * @return the node connected to the right.
     */
    public TechNode getNextRightNode() {
        return nextRightNode;
    }

    /**
     * Sets the right connection of this node.
     *
     * @param nextRightNode Node to connect.
     */
    public void setNextRightNode(TechNode nextRightNode) {
        if (nextRightNode != null) {
            this.nextRightNode = nextRightNode;
            nextRightNode.nextLeftNode = this;

            if (!this.isAttached) {
                this.isAttached = nextRightNode.isAttached();
            }

            attemptAttachment();
        } else if (this.nextRightNode != null) {
            // Disconnect the node
            this.nextRightNode.nextLeftNode = null;
            this.nextRightNode = null;
        }
    }

    /**
     * Gets the node connected below this node.
     *
     * @return the lower connected node.
     */
    public TechNode getNextLowerNode() {
        return nextLowerNode;
    }

    /**
     * Sets the lower connection of this node.
     *
     * @param nextLowerNode Node to connect.
     */
    public void setNextLowerNode(TechNode nextLowerNode) {
        if (nextLowerNode != null) {
            this.nextLowerNode = nextLowerNode;
            nextLowerNode.nextUpperNode = this;

            if (!this.isAttached) {
                this.isAttached = nextLowerNode.isAttached();
            }

            attemptAttachment();
        } else if (this.nextLowerNode != null) {
            // Disconnect the node
            this.nextLowerNode.nextUpperNode = null;
            this.nextLowerNode = null;
        }
    }

    /**
     * Sets the engine number of this node.
     *
     * @param engineNumber The engine number to set.
     */
    public void setEngineNumber(int engineNumber) {
        this.engineNumber = engineNumber;
    }

    /**
     * Gets the engine number of this node.
     *
     * @return the engine number.
     */
    public int getEngineNumber() {
        return engineNumber;
    }

    /**
     * Checks if this node is in the same engine tree as another. Comparing
     * <code>EngineNode</code>s will always return <code>true</code>.
     *
     * @param node Node to check.
     * @return true if this node or the specified node are in the same engine room.
     */
    public boolean isSameEngine(TechNode node) {
        return (node instanceof EngineNode) || (this instanceof EngineNode)
                || (node.getEngineNumber() == this.engineNumber);
    }

    /**
     * Checks if this node has an upper connection.
     *
     * @return true if the next upper node is not null.
     */
    public boolean hasUpper() {
        return nextUpperNode != null;
    }

    /**
     * Checks if this node has a connection to the right.
     *
     * @return true if the next right node is not null.
     */
    public boolean hasRight() {
        return nextRightNode != null;
    }

    /**
     * Checks if this node has a lower connection.
     *
     * @return true if the next lower node is not null.
     */
    public boolean hasLower() {
        return nextLowerNode != null;
    }

    /**
     * Checks if this node has a connection to the left.
     *
     * @return true if the next left node is not null.
     */
    public boolean hasLeft() {
        return nextLeftNode != null;
    }

    /**
     * Checks if this node has any connections.
     *
     * @return true if this node has a connection.
     */
    public boolean hasAny() {
        return hasLeft() || hasUpper() || hasRight() || hasLower();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("(");

        if (!isUnlocked && canBeUnlocked()) {
            appendUnlock(builder, this.name, this.unlockCost);
        } else if (!isUnlocked) {
            appendLock(builder, this.name);
        } else if (this instanceof UpgradeNode) {
            UpgradeNode node = (UpgradeNode) this;
            appendName(builder, this.name);
            int level = node.getLevel();

            appendLevelInfo(builder, level);

            if (level < 5) {
                // Node is not maxed, display the upgrade cost
                appendUpgradeInfo(builder, node.getCurrentUpgradeCost());
            } else {
                appendMaxUpgrade(builder);
            }
        }

        builder.append(")");

        return builder.toString();
    }

    public boolean equals(TechNode node) {
        if (node == null) {
            return false;
        }

        boolean isSameType = node.getClass().equals(this.getClass());
        if (!isSameType) {
            return false;
        }

        boolean isSameName = node.getName().equals(this.name);
        if (!isSameName) {
            return false;
        }

        return true;
    }

    /**
     * Recursively attaches any connected nodes claiming they are not attached.
     */
    private void attemptAttachment() {
        if (this.isAttached) {
            _attachRecursive(this.nextLeftNode);
            _attachRecursive(this.nextLowerNode);
            _attachRecursive(this.nextRightNode);
            _attachRecursive(this.nextUpperNode);
        }
    }

    private void _attachRecursive(TechNode otherNode) {
        if (otherNode == null) {
            return;
        }

        if (!otherNode.isAttached()) {
            otherNode.attach();
            _attachRecursive(otherNode.getNextLeftNode());
            _attachRecursive(otherNode.getNextRightNode());
            _attachRecursive(otherNode.getNextUpperNode());
            _attachRecursive(otherNode.getNextLowerNode());
        }
    }

    private static void appendMaxUpgrade(StringBuilder builder) {
        builder.append(WarshipConstants.NODE_BLANK);
        builder.append("MAXED");
        builder.append(WarshipConstants.NODE_BLANK);
        builder.append(WarshipConstants.NODE_BLANK);
    }

    /**
     * Total appended characters: 15
     */
    private static void appendName(StringBuilder builder, String name) {
        builder.append(name);
        if (name.length() < WarshipConstants.NODE_NAME_MIN_WIDTH) {
            int spacesNeeded = (WarshipConstants.NODE_NAME_MIN_WIDTH - name.length());

            while (spacesNeeded > 0) {
                builder.append(WarshipConstants.NODE_BLANK);
                spacesNeeded--;
            }
        }
    }

    private static void appendLock(StringBuilder builder, String id) {
        appendName(builder, id);

        int remainingWidth = 14;
        int spacesNeeded = (remainingWidth - WarshipConstants.NODE_LOCK.length());

        while (spacesNeeded > 0) {
            builder.append(WarshipConstants.NODE_BLANK);
            spacesNeeded--;
        }

        builder.append(WarshipConstants.NODE_LOCK);
    }

    /**
     * Total appended characters: 4
     */
    private static void appendUnlock(StringBuilder builder, String id, int cost) {
        appendName(builder, id);
        builder.append(String.format(WarshipConstants.NODE_UNLOCK, cost));
        builder.append(WarshipConstants.NODE_BLANK);
        builder.append(WarshipConstants.NODE_BLANK);
    }

    /**
     * Total appended characters: 8
     */
    private static void appendUpgradeInfo(StringBuilder builder, int upgradeCost) {
        builder.append(WarshipConstants.NODE_UPGRADE_ICON);
        builder.append(upgradeCost);

        if (upgradeCost < 1000000) {
            // Fill in additional spaces
            if (upgradeCost < 100000) {
                builder.append(WarshipConstants.NODE_BLANK + WarshipConstants.NODE_BLANK);
            } else {
                builder.append(WarshipConstants.NODE_BLANK);
            }
        }
    }

    /**
     * Total appended characters: 6
     */
    private static void appendLevelInfo(StringBuilder builder, int level) {
        builder.append("[Lv.");
        builder.append(level);
        builder.append("]");
    }

    private boolean canBeUnlocked() {
        return (this.nextLowerNode != null && this.nextLowerNode.isUnlocked)
                || (this.nextRightNode != null && this.nextRightNode.isUnlocked)
                || (this.nextUpperNode != null && this.nextUpperNode.isUnlocked)
                || (this.nextLeftNode != null && this.nextLeftNode.isUnlocked);
    }

}
