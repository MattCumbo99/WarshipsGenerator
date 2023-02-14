package com.warships.nodes;

import static java.util.AbstractMap.SimpleEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.warships.constants.WarshipConstants;
import com.warships.raffles.DefenseRaffle;
import com.warships.utils.MathUtility;

public class ChoiceNode extends TechNode {

    private final List<SimpleEntry<String, Integer>> choices;
    private int selectedChoice;

    private ChoiceNode() {
        super.setName(WarshipConstants.NODE_CHOICE);
        this.choices = new ArrayList<>(3);

        this.selectedChoice = -1; // Indicates this node is not unlocked
    }

    /**
     * Constructs this choice node using data from a defense raffle.
     *
     * @param raffle The defense raffle to base this node off of.
     */
    public ChoiceNode(DefenseRaffle raffle) {
        this();

        List<String> winners = raffle.winners();
        this.choices.addAll(Arrays.asList(
                new SimpleEntry<>(selectRandomFromRaffle(winners), 1),
                new SimpleEntry<>(selectRandomFromRaffle(winners), 1),
                new SimpleEntry<>(selectRandomFromRaffle(winners), 1)
        ));
    }

    @Override
    public void unlock() {
        throw new UnsupportedOperationException("No choice has been made.");
    }

    /**
     * Retrieves the selected choice from this node, or <code>null</code> if
     * no choice has been made.
     *
     * @return Pair data of the selected choice.
     */
    public SimpleEntry<String, Integer> selectedItem() {
        if (this.selectedChoice == -1) {
            return null;
        }

        SimpleEntry<String, Integer> selected = this.choices.get(this.selectedChoice);

        return new SimpleEntry<>(selected.getKey(), selected.getValue());
    }

    /**
     * Gets the choice under the specified number. Range should be 0-2.
     *
     * @param choiceNumber Number of the choice.
     * @return Pair data associated with the choice.
     */
    public SimpleEntry<String, Integer> option(int choiceNumber) {
        SimpleEntry<String, Integer> selected = this.choices.get(choiceNumber);

        return new SimpleEntry<>(selected.getKey(), selected.getValue());
    }

    /**
     * Unlocks the node using the selected choice.
     *
     * @param selectedChoice Index of the selection to make.
     */
    public void unlock(int selectedChoice) {
        if (selectedChoice < 0 || selectedChoice > 2) {
            throw new IllegalArgumentException("Invalid option#: " + selectedChoice);
        }

        this.selectedChoice = selectedChoice;
        super.setName(String.format("*%s x%s*", this.choices.get(selectedChoice).getKey(), this.choices.get(selectedChoice).getValue()));
        super.unlock();
    }

    private static String selectRandomFromRaffle(List<String> winners) {
        int max = winners.size() - 1;
        int randomIndex = MathUtility.random(0, max);

        return winners.remove(randomIndex);
    }
}
