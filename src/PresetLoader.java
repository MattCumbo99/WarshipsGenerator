import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

/**
 * Loader class for reading pre-defined upgrade node data.
 */
public class PresetLoader {

    private String fileLocation;
    private Map<String, UpgradeNode> presetNodes;
    private List<String> loadedNodes;

    public PresetLoader(String fileLocation) {
        this.presetNodes = new HashMap<>();
        this.loadedNodes = new ArrayList<>();
        this.fileLocation = fileLocation;

        generateNodes();
    }

    public PresetLoader(File file) {
        this(file.getPath());
    }

    /**
     * Gets a list of every node present.
     *
     * @return list of strings containing node names.
     */
    public List<String> availableNodes() {
        return Collections.unmodifiableList(this.loadedNodes);
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
     * Gets the specified upgrade node.
     *
     * @param name Name of the upgrade node.
     * @return upgrade node data.
     */
    public UpgradeNode peekNode(String name) {
        return this.presetNodes.get(name);
    }

    /**
     * Gets and removes the specified upgrade node.
     *
     * @param name Name of the upgrade node.
     * @return the removed node.
     */
    public UpgradeNode unloadNode(String name) {
        if (this.loadedNodes.remove(name)) {
            return this.presetNodes.remove(name);
        } else {
            throw new IllegalArgumentException("Node does not exist: " + name);
        }
    }

    /**
     * Gets and removes a random node.
     *
     * @return the removed node.
     */
    public UpgradeNode unloadRandomNode() {
        int max = this.presetNodes.size();
        int randomIndex = (int) Math.floor(Math.random() * (max - 1));

        return this.presetNodes.remove(this.loadedNodes.remove(randomIndex));
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

                insertNodeToList(nodeName);
                this.presetNodes.put(nodeName, node);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read file " + ex.getMessage());
        }
    }

    private void insertNodeToList(String name) {
        if (!this.loadedNodes.contains(name)) {
            this.loadedNodes.add(name);
        } else {
            throw new IllegalArgumentException("Duplicate node: " + name);
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
