import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TechTree {

    private Map<Point, TechNode> tree;
    private PresetLoader loader;
    private int lastTopX;
    private int lastMidX;
    private int lastBotX;

    public TechTree() {
        this.tree = new HashMap<>();
        this.loader = new PresetLoader(new File(WarshipConstants.PRESET_FILENAME));

        initializeBaseNodes();
    }

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

    private void initializeBaseNodes() {
        UpgradeNode gunboat = loader.unloadNode("Gunboat");
        gunboat.unlock();
        insertNode(0, 2, gunboat);

        UpgradeNode landingCraft = loader.unloadNode("Landing Craft");
        landingCraft.unlock();
        insertNode(0, 0, landingCraft);

        UpgradeNode sniperTower = loader.unloadNode("Sniper Tower");
        sniperTower.unlock();
        sniperTower.setNextLeftNode(getNode(0, 2));
        insertNode(1, 2, sniperTower);

        UpgradeNode grenadier = loader.unloadNode("Grenadier");
        grenadier.unlock();
        grenadier.setNextUpperNode(getNode(1, 2));
        insertNode(1, 1, grenadier);

        UpgradeNode cannon = loader.unloadNode("Cannon");
        cannon.unlock();
        cannon.setNextUpperNode(getNode(1, 1));
        cannon.setNextLeftNode(getNode(0, 0));
        insertNode(1, 0, cannon);

        UpgradeNode flare = loader.unloadNode("Flare");
        flare.setNextLeftNode(getNode(1, 1));
        flare.unlock();
        insertNode(2, 1, flare);

        UpgradeNode gbeBonus = new UpgradeNode("GBE Bonus");
        gbeBonus.setUpgradeCosts(85000, 100000, 223000, 323000);
        gbeBonus.setNextLowerNode(getNode(2, 1));
        insertNode(2, 2, gbeBonus);

        UpgradeNode smoke = loader.unloadNode("Smoke Screen");
        smoke.setNextLeftNode(getNode(2, 1));
        insertNode(3, 1, smoke);

        UpgradeNode mortar = loader.unloadNode("Mortar");
        mortar.setNextLeftNode(getNode(2, 2));
        insertNode(3, 2, mortar);

        EngineNode engine1 = new EngineNode(1);
        engine1.setNextUpperNode(getNode(3, 1));
        insertNode(3, 0, engine1);

        UpgradeNode cryos = loader.unloadNode("Cryoneer");
        cryos.setNextLeftNode(getNode(1, 0));
        cryos.setUnlockCost(24);
        insertNode(2, 0, cryos);
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

    private void insertNode(int x, int y, TechNode node) {
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
                throw new ArrayIndexOutOfBoundsException("Index of Y:" + y);
        }

        tree.put(new Point(x, y), node);
    }

    private boolean upgradeNode(int x, int y) {
        if (getNode(x, y) instanceof UpgradeNode) {
            ((UpgradeNode) getNode(x, y)).upgrade();
            return true;
        } else {
            return false;
        }
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

    private TechNode getNode(int x, int y) {
        Point point = new Point(x, y);

        return tree.get(point);
    }

    private static void appendSpaces(StringBuilder str, int spaces) {
        for (int i = 0; i < spaces; i++) {
            str.append(WarshipConstants.BLANK_SPACE);
        }
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
