package com.warships.raffles;

public class DefenseRaffle extends Raffle {

    public static final String SNIPER_TOWER = "Sniper Tower";
    public static final String MORTAR = "Mortar";
    public static final String CANNON = "Cannon";
    public static final String BOOM_CANNON = "Boom Cannon";
    public static final String MACHINE_GUN = "Machine Gun";
    public static final String FLAMETHROWER = "Flamethrower";
    public static final String ROCKET_LAUNCHER = "Rocket Launcher";
    public static final String SHOCK_LAUNCHER = "Shock Launcher";
    public static final String MINE = "Mine";
    public static final String BOOM_MINE = "Boom Mine";
    public static final String SHOCK_MINE = "Shock Mine";

    public DefenseRaffle() {
        super.initialize(this.getClass());
    }
}
