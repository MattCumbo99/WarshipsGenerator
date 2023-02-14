package com.warships.nodes;

public class UpgradeNode extends TechNode {

    private final int[] upgradeCosts;
    private int upgradeCounter;

    public UpgradeNode() {
        this.upgradeCosts = new int[4];
        this.upgradeCounter = 0;
    }

    public UpgradeNode(String name) {
        this();
        super.setName(name);
    }

    public UpgradeNode(UpgradeNode node) {
        this.upgradeCosts = new int[4];
        setUpgradeCosts(node.getUpgradeCost(0), node.getUpgradeCost(1), node.getUpgradeCost(2), node.getUpgradeCost(3));

        super.setName(node.getName());
    }

    public int getLevel() {
        if (super.isUnlocked()) {
            return upgradeCounter + 1;
        }

        return upgradeCounter;
    }

    public void setUpgradeCosts(int level2, int level3, int level4, int level5) {
        this.upgradeCosts[0] = level2;
        this.upgradeCosts[1] = level3;
        this.upgradeCosts[2] = level4;
        this.upgradeCosts[3] = level5;
    }

    public int getUpgradeCost(int level) {
        return this.upgradeCosts[level];
    }

    public boolean upgrade() {
        if (this.upgradeCounter == 4) {
            return false;
        } else {
            this.upgradeCounter++;
            return true;
        }
    }

    public int getCurrentUpgradeCost() {
        if (this.upgradeCounter >= this.upgradeCosts.length) {
            return -1;
        }

        return this.upgradeCosts[this.upgradeCounter];
    }
}
