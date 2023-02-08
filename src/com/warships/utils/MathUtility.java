package com.warships.utils;

public final class MathUtility {

    /**
     * Determines a proc chance based on a specified percentage.
     *
     * @param percent Percentage to proc.
     * @return true if the method should proc.
     */
    public static boolean chance(double percent) {
        if (percent >= 1.0) {
            return true;
        } else if (percent <= 0.0) {
            return false;
        }

        int lowerNumber = (int) (percent * 100);

        // Determine the deciding number
        int roll = random(1, 100);

        return (lowerNumber >= roll);
    }

    /**
     * Gets a random integer between a specified range.
     *
     * @param min Minimum number.
     * @param max Maximum number.
     * @return Random integer between the minimum and maximum.
     */
    public static int random(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    private MathUtility() {
        throw new IllegalStateException("Cannot instantiate utility class.");
    }
}
