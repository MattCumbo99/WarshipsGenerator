package com.warships.raffles;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Raffle {

    private List<String> tickets;

    protected Raffle() {
        this.tickets = new ArrayList<>();
    }

    protected void initialize(Class<? extends Raffle> clazz) {
        // Get all static fields from the class
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(f ->
                Modifier.isStatic(f.getModifiers())).toList();

        for (Field field: fields) {
            try {
                Object value = field.get(clazz);
                if (value instanceof String strval) {
                    this.tickets.add(strval);
                }
            } catch (IllegalAccessException ex) {
                // Do nothing, no need to add private variable
            }
        }
    }

    public void remove(String ticket) {
        this.tickets.remove(ticket);
    }

    public boolean isEmpty() {
        return this.tickets.isEmpty();
    }

    public boolean contains(String ticket) {
        return this.tickets.contains(ticket);
    }

    public String removeRandom() {
        int max = this.tickets.size();
        int randomIndex = (int) Math.floor(Math.random() * (max - 1));

        return this.tickets.remove(randomIndex);
    }
}
