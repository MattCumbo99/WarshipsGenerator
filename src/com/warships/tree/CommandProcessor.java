package com.warships.tree;

import com.warships.enums.CommandEnum;
import com.warships.nodes.ChoiceNode;
import com.warships.nodes.TechNode;
import com.warships.nodes.UpgradeNode;
import com.warships.utils.StringUtility;

import static java.util.AbstractMap.SimpleEntry;

public final class CommandProcessor {

    /**
     * Processes a command.
     *
     * @param input
     */
    public static void process(String input, TechTree tree) {
        String[] args = input.split(" ");
        String command = args[0].toLowerCase();

        if (CommandEnum.contains(command)) {
            switch (CommandEnum.valueOf(command.toUpperCase())) {
                case HELP:
                    showHelp(args.length == 1 ? null : args[1]);
                    break;
                case GET:
                    if (args.length < 3) {
                        System.out.println("Usage: GET [X Y]");
                    } else {
                        showNode(tree, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                    }
                    break;
                case UPGRADE:
                    if (args.length < 3) {
                        System.out.println("Usage: UPGRADE [X Y]");
                    } else {
                        upgradeNode(tree, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                    }
                    break;
                case UNLOCK:
                    if (args.length < 3) {
                        System.out.println("Usage: UNLOCK [X Y] [Choice #]");
                    } else {
                        unlockNode(tree, Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                                args.length > 3 ? Integer.parseInt(args[3]) : -1);
                    }
                    break;
                case REFRESH:
                    tree.displayNodes();
                    break;
                default:
                    throw new IllegalArgumentException("Instruction not accounted for: " + command);
            }
        } else {
            System.out.printf("'%s' is not recognized as an internal command.%n", args[0]);
        }
    }

    private static void unlockNode(TechTree tree, int x, int y, int selection) {
        TechNode node = tree.getNode(x, y);
        if (node.canBeUnlocked()) {
            if (!(node instanceof ChoiceNode)) {
                node.unlock();
                System.out.println("Node unlocked.");
            } else {
                if (selection != -1) {
                    ((ChoiceNode) node).unlock(selection);
                    System.out.println("Node unlocked.");
                } else {
                    System.out.println("Could not unlock node. A valid selection must be made.");
                }
            }
        } else {
            System.out.println("Unlock neighboring nodes first.");
        }
    }

    private static void upgradeNode(TechTree tree, int x, int y) {
        if (tree.buyUpgrade(x, y)) {
            System.out.print("Upgraded ");
            showNode(tree, x, y);
        } else {
            System.out.println("Unable to purchase upgrade for this node.");
        }
    }

    private static void showNode(TechTree tree, int x, int y) {
        TechNode node = tree.getNode(x, y);
        if (node != null) {
            System.out.printf("%s", node.getName());
            if (!node.isUnlocked()) {
                System.out.println(" (locked)");
            } else if (node instanceof UpgradeNode) {
                UpgradeNode upnode = (UpgradeNode) node;
                System.out.printf(" (level %s)%n", upnode.getLevel());
            } else {
                System.out.println();
            }

            if (node instanceof ChoiceNode) {
                ChoiceNode chnode = (ChoiceNode) node;
                SimpleEntry<String, Integer> choice1 = chnode.option(0);
                SimpleEntry<String, Integer> choice2 = chnode.option(1);
                SimpleEntry<String, Integer> choice3 = chnode.option(2);

                System.out.printf("1 -> %s x%s%n2 -> %s x%s%n3 -> %s x%s%n", choice1.getKey(), choice1.getValue(),
                        choice2.getKey(), choice2.getValue(), choice3.getKey(), choice3.getValue());
            }
        } else {
            System.out.println("There is no node at this position.");
        }
    }

    private static void showHelp(String command) {
        if (StringUtility.isNotBlank(command) && CommandEnum.contains(command)) {
            System.out.printf("%s\t\t%s%n", command.toUpperCase(), CommandEnum.descriptionOf(command));
        } else if (StringUtility.isNotBlank(command)) {
            System.out.println("This command is not supported by the help utility.");
        } else {
            for (CommandEnum cmd: CommandEnum.values()) {
                System.out.printf("%s\t\t%s%n", cmd.name(), cmd.description);
            }
        }
    }

    private CommandProcessor() {
        throw new IllegalStateException("Cannot instantiate static class.");
    }
}
