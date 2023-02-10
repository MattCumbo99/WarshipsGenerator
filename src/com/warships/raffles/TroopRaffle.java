package com.warships.raffles;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TroopRaffle extends Raffle {

    public static final String RIFLEMAN = "Rifleman";
    public static final String HEAVY = "Heavy";
    public static final String ZOOKA = "Zooka";
    public static final String HIDDEN_WARRIOR = "Hidden Warrior";
    public static final String MEDIC = "Medic";
    public static final String TANK = "Tank";
    public static final String GRENADIER = "Grenadier";
    public static final String SCORCHER = "Scorcher";
    public static final String CRYONEER = "Cryoneer";
    public static final String BOMBARDIER = "Bombardier";
    public static final String MECH = "Mech";
    public static final String ROCKET_CHOPPA = "Rocket Choppa";
    public static final String HEAVY_CHOPPA = "Heavy Choppa";
    public static final String SEEKER = "Seeker";
    public static final String CPT_EVERSPARK = "Cpt. Everspark";
    public static final String DR_KAVAN = "Dr. Kavan";
    public static final String PVT_BULLIT = "Pvt. Bullit";
    public static final String SGT_BRICK = "Sgt. Brick";

    /**
     * Filter for troops that are considered hard to defend against.
     */
    private static final List<String> OVERPOWERED_TROOP_FILTER = Arrays.asList(
            HEAVY_CHOPPA, SEEKER, SCORCHER, PVT_BULLIT
    );

    /**
     * Filter for troops that cannot deal damage without a third party.
     */
    private static final List<String> PASSIVE_TROOP_FILTER = Arrays.asList(
            MEDIC, DR_KAVAN
    );

    /**
     * Filter for troops that contain expensive upgrade costs.
     */
    private static final List<String> COSTLY_TROOP_FILTER = Arrays.asList(
            PVT_BULLIT, CPT_EVERSPARK
    );

    /**
     * Filter for troops that are considered a bad choice for the first engine room.
     */
    private static final List<String> FIRST_TROOP_FILTER = Stream.of(
            OVERPOWERED_TROOP_FILTER, PASSIVE_TROOP_FILTER, COSTLY_TROOP_FILTER
    ).flatMap(Collection::stream).collect(Collectors.toList());

    public TroopRaffle() {
        super.initialize(this.getClass());
    }


    /**
     * Gets and removes a simple choice for the first node of a tech tree.
     *
     * @return String name of the selected choice.
     */
    public String removeFirstChoice() {
        return super.removeWithFilter(FIRST_TROOP_FILTER);
    }

    /**
     * Gets and removes a troop with preference for non-challenging options. Nodes that are not
     * prioritized can be found in the {@link #OVERPOWERED_TROOP_FILTER} variable.
     *
     * @return String name of a troop which is not overpowered.
     */
    public String removeNonOverpowered() {
        return super.removeWithFilter(OVERPOWERED_TROOP_FILTER);
    }
}
