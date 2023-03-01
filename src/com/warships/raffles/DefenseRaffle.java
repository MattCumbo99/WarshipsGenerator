package com.warships.raffles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefenseRaffle extends Raffle {

    public static final String SNIPER_TOWER = "Sniper Tower";
    public static final String MORTAR = "Mortar";
    public static final String CANNON = "Cannon";
    public static final String BOOM_CANNON = "Boom Cannon";
    public static final String MACHINE_GUN = "Machine Gun";
    public static final String FLAMETHROWER = "Flamethrower";
    public static final String ROCKET_LAUNCHER = "Rocket Launcher";
    public static final String SHOCK_LAUNCHER = "Shock Launcher";
    public static final String CRITTER_LAUNCHER = "Critter Launcher";
    public static final String MINE = "Mine";
    public static final String BOOM_MINE = "Boom Mine";
    public static final String SHOCK_MINE = "Shock Mine";

    public DefenseRaffle() {
        super.initialize(this.getClass());
    }

    private static final List<String> OVERPOWERED_DEFENSES = Collections.unmodifiableList(Arrays.asList(
       SHOCK_LAUNCHER, ROCKET_LAUNCHER, BOOM_CANNON, SHOCK_MINE
    ));

    /**
     * Gets and removes a defense with preference for non-challenging options. Nodes that are not
     * prioritized can be found in the {@link #OVERPOWERED_DEFENSES} variable.
     *
     * @return A defense name which is not overpowered.
     */
    public String removeNonOverpowered() {
        return super.removeWithFilter(OVERPOWERED_DEFENSES);
    }
}
