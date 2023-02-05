package com.warships.constants;

public final class WarshipConstants {

    public static final int ENGINE_ONE_UNLOCK_COST = 36;
    public static final int ENGINE_TWO_UNLOCK_COST = 65;
    public static final int ENGINE_THREE_UNLOCK_COST = 90;
    public static final int ENGINE_FOUR_UNLOCK_COST = 110;
    public static final int ENGINE_FIVE_UNLOCK_COST = 165;

    public static final String PRESET_FILENAME = "PresetNodes.txt";

    public static final int NODE_NAME_MIN_WIDTH = 15;

    public static final String NODE_BLANK = "━";
    public static final String NODE_LOCK = "[Locked]";
    public static final String NODE_UPGRADE_ICON = "↑";
    public static final String NODE_UNLOCK = "«Unlock:%s »";

    public static final String BLANK_SPACE = " ";

    public static final String HORIZONTAL_CONNECTOR = "=";
    public static final int HORIZONTAL_CONNECTION_LENGTH = 5;

    public static final String VERTICAL_CONNECTOR = "│⇅│";

    private WarshipConstants() {
        throw new IllegalStateException("Cannot instantiate constants class.");
    }
}
