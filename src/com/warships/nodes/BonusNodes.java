package com.warships.nodes;

public final class BonusNodes {

    public static final UpgradeNode GBE = new UpgradeNode("Gunboat Energy");
    public static final UpgradeNode BUILDING_HEALTH = new UpgradeNode("Building Health");
    public static final UpgradeNode BUILDING_DAMAGE = new UpgradeNode("Building Damage");
    public static final UpgradeNode TROOP_HEALTH = new UpgradeNode("Troop Health");
    public static final UpgradeNode TROOP_DAMAGE = new UpgradeNode("Troop Damage");

    static {
        GBE.setUpgradeCosts(120000, 163500, 223000, 323000);
        BUILDING_DAMAGE.setUpgradeCosts(120000, 163500, 223000, 323000);
        BUILDING_HEALTH.setUpgradeCosts(120000, 163500, 223000, 323000);
        TROOP_DAMAGE.setUpgradeCosts(120000, 163500, 223000, 323000);
        TROOP_HEALTH.setUpgradeCosts(120000, 163500, 223000, 323000);
    }

    private BonusNodes() {
        throw new IllegalStateException("Cannot instantiate constants class.");
    }
}
