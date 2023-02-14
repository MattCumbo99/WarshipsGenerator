package com.warships.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CommandEnum {

    UNLOCK("Unlocks a node."),
    GET("Displays information about a node."),
    UPGRADE("Upgrades a node to the next level."),
    HELP("Displays information about a command."),
    REFRESH("Refreshes and displays the tree."),
    EXIT("Exits the program.");

    private static final Map<String, CommandEnum> valueMap = new HashMap<>();
    static {
        for (CommandEnum value: EnumSet.allOf(CommandEnum.class)) {
            valueMap.put(value.name(), value);
        }
    }

    public static String descriptionOf(String name) {
        return valueMap.get(name.toUpperCase()).description;
    }

    public static boolean contains(String command) {
        return valueMap.containsKey(command.toUpperCase());
    }

    public final String description;

    CommandEnum(String description) {
        this.description = description;
    }
}
