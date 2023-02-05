package com.warships.tree;

import com.warships.loaders.PresetLoader;
import com.warships.nodes.TechNode;
import com.warships.nodes.UpgradeNode;
import com.warships.raffles.DefenseRaffle;
import com.warships.raffles.GunboatRaffle;
import com.warships.raffles.Raffle;
import com.warships.constants.WarshipConstants;
import com.warships.raffles.TroopRaffle;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TechTree {

    private Map<Point, TechNode> tree;
    private PresetLoader loader;
    private Raffle gbeRaffle;
    private Raffle troopRaffle;
    private Raffle defenseRaffle;
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

    public boolean buyUpgrade(int x, int y) {
        if (getNode(x, y) instanceof UpgradeNode node) {
            node.upgrade();
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
        str.append(WarshipConstants.BLANK_SPACE.repeat(Math.max(0, spaces)));
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
