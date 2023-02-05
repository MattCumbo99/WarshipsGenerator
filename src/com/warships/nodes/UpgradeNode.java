package com.warships.nodes;

import com.warships.nodes.TechNode;

public class UpgradeNode extends TechNode {

    private int[] upgradeCosts;
    private int upgradeCounter;

    public UpgradeNode() {
        this.upgradeCosts = new int[4];
        this.upgradeCounter = 0;
    }

    public UpgradeNode(String name) {
        this();
        super.setName(name);
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

    public void upgrade() {
        if (this.upgradeCounter == 4) {
            System.out.println("Cannot upgrade node " + getName() + ". Upgrades are maxed!");
        } else {
            this.upgradeCounter++;
        }
    }

    public int getCurrentUpgradeCost() {
        if (this.upgradeCounter >= this.upgradeCosts.length) {
            return -1;
        }

        return this.upgradeCosts[this.upgradeCounter];
    }
}
