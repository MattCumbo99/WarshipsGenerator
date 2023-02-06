package com.warships.raffles;

import com.warships.raffles.Raffle;

public class TroopRaffle extends Raffle {

    public static final String RIFLEMAN = "Rifleman";
    public static final String HEAVY = "Heavy";
    public static final String ZOOKA = "Zooka";
    public static final String WARRIOR = "Warrior";
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

    public TroopRaffle() {
        super.initialize(this.getClass());
    }

    /**
     * Picks and removes a necessary troop that can deal damage.
     *
     * @return String name of troop that is not a medic.
     */
    public String removeRandomImportant() {
        String troop = super.peekRandom();

        while (troop.equals(MEDIC)) {
            troop = super.peekRandom();
        }

        super.remove(troop);
        return troop;
    }
}
