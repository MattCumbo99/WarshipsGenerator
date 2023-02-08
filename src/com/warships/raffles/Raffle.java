package com.warships.raffles;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.warships.utils.MathUtility;

/**
 * The super class of all raffle objects. Raffles
 * are created using accessible <code>static String</code>
 * variables declared within the subclass.
 */
public abstract class Raffle {

    private final List<String> tickets;
    private final List<String> winners;

    protected Raffle() {
        this.tickets = new ArrayList<>();
        this.winners = new ArrayList<>();
    }

    /**
     * Initializes the raffle using all variables within the class containing the following
     * signature:
     * <pre>public static final String</pre>
     *
     * @param clazz The subclass to initialize.
     */
    protected void initialize(Class<? extends Raffle> clazz) {
        // Get all static fields from the class
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(f ->
                Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());

        for (Field field : fields) {
            try {
                Object value = field.get(clazz);
                if (value instanceof String) {
                    String strval = (String) value;
                    this.tickets.add(strval);
                }
            } catch (IllegalAccessException ex) {
                // Do nothing, no need to add inaccessible variable
            }
        }
    }

    /**
     * Removes the specified ticket. If the ticket does not exist, nothing happens.
     *
     * @param ticket Name of the ticket to remove.
     */
    public void remove(String ticket) {
        if (this.tickets.contains(ticket)) {
            this.winners.add(ticket);
            this.tickets.remove(ticket);
        }
    }

    /**
     * Gets the list of tickets that have been removed from this raffle.
     *
     * @return the list of winners.
     */
    public List<String> winners() {
        return new ArrayList<>(this.winners);
    }

    /**
     * Retrieves a random ticket that has been pulled from the raffle.
     *
     * @return the selected ticket.
     */
    public String getRandomWinner() {
        int max = this.winners.size() - 1;
        int randomIndex = MathUtility.random(0, max);

        return this.winners.get(randomIndex);
    }

    /**
     * Checks if the raffle has any remaining tickets.
     *
     * @return false if the raffle has at least one ticket.
     */
    public boolean isEmpty() {
        return this.tickets.isEmpty();
    }

    /**
     * Checks if the raffle still has a specified ticket.
     *
     * @param ticket Name of the ticket.
     * @return true if the raffle has not selected the ticket.
     */
    public boolean contains(String ticket) {
        return this.tickets.contains(ticket);
    }

    /**
     * Removes a random ticket from the raffle.
     *
     * @return the removed ticket.
     */
    public String removeRandom() {
        int max = this.tickets.size() - 1;
        int randomIndex = MathUtility.random(0, max);

        String result = this.tickets.remove(randomIndex);
        this.winners.add(result);

        return result;
    }

    /**
     * Retrieves a random ticket from the raffle.
     *
     * @return a random ticket.
     */
    public String peekRandom() {
        int max = this.tickets.size() - 1;
        int randomIndex = MathUtility.random(0, max);

        return this.tickets.get(randomIndex);
    }
}
