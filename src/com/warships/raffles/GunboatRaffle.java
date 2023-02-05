package com.warships.raffles;

public class GunboatRaffle extends Raffle {

    public static final String FLARE = "Flare";
    public static final String MEDKIT = "Medkit";
    public static final String SHOCK_BOMB = "Shock Bomb";
    public static final String ARTILLERY = "Artillery";
    public static final String BARRAGE = "Barrage";
    public static final String CRITTERS = "Critters";
    public static final String SMOKE_SCREEN = "Smoke Screen";

    public GunboatRaffle() {
        super.initialize(this.getClass());
    }
}
