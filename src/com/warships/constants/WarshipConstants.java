package com.warships.constants;

public final class WarshipConstants {

    public static final int ENGINE_ONE_UNLOCK_COST = 36;
    public static final int ENGINE_TWO_UNLOCK_COST = 65;
    public static final int ENGINE_THREE_UNLOCK_COST = 90;
    public static final int ENGINE_FOUR_UNLOCK_COST = 110;
    public static final int ENGINE_FIVE_UNLOCK_COST = 165;

    public static final int TOTAL_COLUMNS_TWO_ENGINES = 2;
    public static final int TOTAL_COLUMNS_THREE_ENGINES = 5;
    public static final int TOTAL_COLUMNS_FOUR_ENGINES = 4;
    public static final int TOTAL_COLUMNS_FIVE_ENGINES = 3;
    public static final int TOTAL_COLUMNS_SIX_ENGINES = 3;

    /**
     * Maximum number of allowed bonus nodes for two engines.
     */
    public static final int TWO_ENGINES_BONUS_LIMIT = 1;

    /**
     * Maximum number of allowed bonus nodes for three engines.
     */
    public static final int THREE_ENGINES_BONUS_LIMIT = 3;

    /**
     * Maximum number of allowed bonus nodes for four engines.
     */
    public static final int FOUR_ENGINES_BONUS_LIMIT = 2;

    /**
     * Maximum number of allowed bonus nodes for five engines.
     */
    public static final int FIVE_ENGINES_BONUS_LIMIT = 2;

    /**
     * Maximum number of allowed bonus nodes for six engines.
     */
    public static final int SIX_ENGINES_BONUS_LIMIT = 2;

    /**
     * Percent chance that a node will be generated as a choice node.
     */
    public static final double CHOICE_NODE_CHANCE = 0.30;

    /**
     * Percent chance that a node will generate a base game component node.
     */
    public static final double COMPONENT_NODE_CHANCE = 0.90;

    public static final String PRESET_FILENAME = "PresetNodes.txt";

    /**
     * Minimum allowed width for the name of a node. Any name that does not
     * meet the length requirement will append {@link #NODE_BLANK} characters
     * until the demand is met. Node names that exceed this name limit will
     * cut off text that exceed the length.
     */
    public static final int NODE_NAME_MIN_WIDTH = 20;

    /**
     * Character to use when ensuring a fixed width for each displayed node.
     */
    public static final Character NODE_BLANK = '━';

    /**
     * Text to use when representing a node that cannot be unlocked.
     */
    public static final String NODE_LOCK = "[Locked]";

    /**
     * Character to use when representing an upgradeable node.
     */
    public static final Character NODE_UPGRADE_ICON = '↑';

    /**
     * Text to use when displaying an unlockable node.
     */
    public static final String NODE_UNLOCK = "«Unlock:%s »";

    /**
     * Text to use when displaying a choice node.
     */
    public static final String NODE_CHOICE = "*CHOICE*";

    /**
     * Character to use for representing unoccupied space when displaying the tree.
     */
    public static final Character BLANK_SPACE = ' ';

    /**
     * Character to use for creating horizontal connections.
     */
    public static final Character HORIZONTAL_CONNECTOR = '=';
    public static final int HORIZONTAL_CONNECTION_LENGTH = 5;

    /**
     * Text that represents a connection between nodes on different levels.
     * Must be a length of 3.
     */
    public static final String VERTICAL_CONNECTOR = "| |";

    private WarshipConstants() {
        throw new IllegalStateException("Cannot instantiate constants class.");
    }
}
