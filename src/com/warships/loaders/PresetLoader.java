package com.warships.loaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.warships.nodes.UpgradeNode;

/**
 * Loader class for reading pre-defined upgrade node data.
 */
public class PresetLoader {

    private final String fileLocation;
    private final Map<String, UpgradeNode> presetNodes;
    private final List<String> loadedNodes;
    private int initialSize;

    public PresetLoader(String fileLocation) {
        this.presetNodes = new HashMap<>();
        this.loadedNodes = new ArrayList<>();
        this.fileLocation = fileLocation;
        this.initialSize = 0;

        generateNodes();
    }

    public PresetLoader(File file) {
        this(file.getPath());
    }

    public int getInitialSize() {
        return initialSize;
    }

    /**
     * Checks if a node exists under a specified name.
     *
     * @param name Name of the upgrade node.
     * @return true if the node is present.
     */
    public boolean contains(String name) {
        return this.presetNodes.containsKey(name);
    }

    /**
     * Checks if this loader holds at least one node.
     *
     * @return true if the loader is not empty.
     */
    public boolean isEmpty() {
        return this.presetNodes.isEmpty();
    }

    /**
     * Gets and removes the specified upgrade node.
     *
     * @param name Name of the upgrade node.
     * @return the removed node.
     */
    public UpgradeNode unloadNode(String name) {
        UpgradeNode node = this.presetNodes.remove(name);
        if (node == null) {
            throw new RuntimeException("Node is not present for pulling: " + name);
        }
        return node;
    }

    private void generateNodes() {
        File file = new File(this.fileLocation);

        try (Scanner sc = new Scanner(file)) {
            // Read each line from the file
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] info = line.split("\t");

                // Get information from the string data
                String nodeName = info[0];
                int[] upgrades = parseUpgradeCosts(info[1]);

                // Construct the node
                UpgradeNode node = new UpgradeNode(nodeName);
                node.setUpgradeCosts(upgrades[0], upgrades[1], upgrades[2], upgrades[3]);

                this.presetNodes.put(nodeName, node);
            }
            this.initialSize = this.presetNodes.size();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read file " + ex.getMessage());
        }
    }

    private int[] parseUpgradeCosts(String costs) {
        String[] upgrades = costs.split(" ");
        int[] results = new int[4];

        for (int i = 0; i < upgrades.length; i++) {
            results[i] = Integer.parseInt(upgrades[i]);
        }

        return results;
    }
}
