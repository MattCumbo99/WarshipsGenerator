public enum AssetEnum {

    DEFAULT_GUNBOAT("Gunboat"), // Ship
    DEFAULT_LANDING_CRAFT("Landing Craft"), // Speedboat

    BONUS_GBE("\uD83D\uDD0B_"), // Battery
    BONUS_TROOP_HEALTH("\uD83E\uDDCD❤️"), // Person + heart
    BONUS_TROOP_DAMAGE("\uD83E\uDDCD\uD83D\uDCA5"), // Person + collision
    BONUS_DEFENSE_HEALTH("\uD83D\uDEE1️❤️"), // Shield + heart
    BONUS_DEFENSE_DAMAGE("\uD83D\uDEE1\uD83D\uDCA5"), // Shield + collision

    TROOP_RIFLEMEN("\uD83D\uDD2B\uD83E\uDDD1"), // water pistol + man
    TROOP_HEAVIES("\uD83E\uDDD4_"), // Man with beard
    TROOP_ZOOKAS("\uD83D\uDC69\uD83D\uDE80"), // Woman + rocket
    TROOP_WARRIORS("\uD83E\uDE93\uD83D\uDE20"), // Axe + angry face
    TROOP_TANKS("\uD83D\uDE98_"), // Blue automobile
    TROOP_MEDICS("⛑\uD83D\uDE2F"), // Rescue helmet + Hushed face
    TROOP_GRENADIERS("\uD83D\uDC68\uD83D\uDCA3"), // Man with mustache + bomb
    TROOP_SCORCHERS("\uD83D\uDE82\uD83D\uDD25"), // Red train + flame
    TROOP_LASER_RANGERS("\uD83E\uDDB8\u200D♀️"), // Woman superhero
    TROOP_CRYONEERS("\uD83D\uDC69❄"), // Woman + snowflake
    TROOP_BOMBARDIERS("\uD83D\uDC74\uD83D\uDCA3"), // Old man + bomb
    TROOP_MECHS("\uD83E\uDD16\uD83D\uDD2B"), // Robot + water pistol
    TROOP_ROCKET_CHOPPAS("\uD83D\uDE81_"), // Helicopter
    TROOP_HEAVY_CHOPPAS("\uD83D\uDEA1_"), // Aerial tramway

    GBE_ARTILLERY("\uD83D\uDCA3"), // Bomb
    GBE_FLARE("\uD83E\uDDE8"), // Firecracker
    GBE_MEDKIT("\uD83D\uDC5C"), // Handbag
    GBE_BARRAGE("\uD83D\uDE80"), // Rocket
    GBE_SHOCK("⚡"), // Voltage
    GBE_SMOKE("☁"), // Cloud
    GBE_CRITTERS("\uD83D\uDC7E"), // Alien monster

    DEFENSE_ENGINE("⚙⛽"), // Gear + gas pump
    DEFENSE_SNIPER_TOWER("\uD83C\uDFF9"), // Bow and arrow
    DEFENSE_ROCKET_LAUNCHER("\uD83D\uDE80"), // Rocket
    DEFENSE_CRITTER_LAUNCHER("\uD83D\uDC7E"), // Alien monster
    DEFENSE_SHOCK_LAUNCHER("⚡"),
    DEFENSE_FLAMETHROWER("\uD83D\uDD25"), // Flame
    DEFENSE_MACHINE_GUN("\uD83D\uDD2B"), // Water pistol
    DEFENSE_CANNON("\uD83D\uDCAA"), // Biceps
    DEFENSE_BOOM_CANNON("\uD83E\uDDBE"), // Mechanical biceps
    DEFENSE_MORTAR("\uD83D\uDCA3"); // Bomb

    private final String abbreviation;

    AssetEnum(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String abbr() {
        return abbreviation;
    }
}
