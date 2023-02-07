package com.warships.raffles;

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

    public TroopRaffle() {
        super.initialize(this.getClass());
    }

    /**
     * Picks and removes a necessary troop that can deal damage.
     *
     * @return String name of troop that is not a medic or Dr. Kavan.
     */
    public String removeRandomImportant() {
        String troop = super.peekRandom();

        // Medics cannot deal damage.
        while (troop.equals(MEDIC) || troop.equals(DR_KAVAN)) {
            troop = super.peekRandom();
        }

        super.remove(troop);
        return troop;
    }
}
