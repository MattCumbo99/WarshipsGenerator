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
    private TechNode nextLeftNode;
    private TechNode nextUpperNode;
    private TechNode nextRightNode;
    private TechNode nextLowerNode;

    public TechNode() {
        this.name = "?";
        this.engineNumber = 1;
        this.isUnlocked = false;
        this.unlockCost = 30;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnlockCost() {
        return unlockCost;
    }

    public void setUnlockCost(int unlockCost) {
        this.unlockCost = unlockCost;
    }

    public void unlock() {
        this.isUnlocked = true;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public TechNode getNextLeftNode() {
        return nextLeftNode;
    }

    public void setNextLeftNode(TechNode nextLeftNode) {
        this.nextLeftNode = nextLeftNode;
        nextLeftNode.nextRightNode = this;
    }

    public TechNode getNextUpperNode() {
        return nextUpperNode;
    }

    public void setNextUpperNode(TechNode nextUpperNode) {
        this.nextUpperNode = nextUpperNode;
        nextUpperNode.nextLowerNode = this;
    }

    public TechNode getNextRightNode() {
        return nextRightNode;
    }

    public void setNextRightNode(TechNode nextRightNode) {
        this.nextRightNode = nextRightNode;
        nextRightNode.nextLeftNode = this;
    }

    public void setEngineNumber(int engineNumber) {
        this.engineNumber = engineNumber;
    }

    public int getEngineNumber() {
        return engineNumber;
    }

    public TechNode getNextLowerNode() {
        return nextLowerNode;
    }

    public void setNextLowerNode(TechNode nextLowerNode) {
        this.nextLowerNode = nextLowerNode;
        nextLowerNode.nextUpperNode = this;
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
