package com.paragon464.gameserver.model.content.skills.slayer;

public class TaskData {

    public static final HighLevel[] HIGH_LEVEL_TASKS = HighLevel.values();
    public static final LowLevel[] LOW_LEVEL_TASKS = LowLevel.values();
    public static final MediumLevel[] MEDIUM_TASKS = MediumLevel.values();
    public static final short DEFAULT_KILL_AMOUNT = 12;
    public static final short RANDOM_KILL_ADDON = 82;

    public enum HighLevel {

        BLACK_DRAGON("Black dragon", "Dragons Lair"),

        BLUE_DRAGON("Blue dragon", "Dragons Lair"),

        RED_DRAGON("Red dragon", "Dragons Lair"),

        IRON_DRAGON("Iron Dragon", "Dragons Dungeon"),

        BRONZE_DRAGON("Bronze Dragon", "Dragons Dungeon"),

        STEEL_DRAGON("Steel Dragon", "Dragons Dungeon"),

        MITHRIL_DRAGON("Mithril dragon", "Ancient Cavern"),

        BLACK_DEMON("Black demon", "Mid Dungeon"),

        HELL_HOUND("Hellhound", "Mid Dungeon"),

        SPIRITUAL_WARRIOR("Spiritual warrior", "God Wars"),

        ABYSSAL_DEMON("Abyssal demon", "Slayer Tower"),

        DARK_BEAST("Dark beast", "Slayer Tower"),

        TZHAAR_KET("TzHaar-Ket", "TzHaar Caves"),

        TZHAAR_KET2("TzHaar-Ket", "TzHaar Caves"),

        TZHAAR_XIL("TzHaar-Xil", "TzHaar Caves"),

        TZHAAR_XIL2("TzHaar-Xil", "TzHaar Caves");

        public final String name, area;
        public final byte lvl;

        HighLevel(String name, String area) {
            this.name = name;
            this.area = area;
            lvl = SlayerMonsters.getLevelForName(name.toLowerCase());
        }
    }

    public enum LowLevel {

        ROCK_CRAB("Rock crab", "South of Home"),

        CHAOS_DRUID("Chaos druid", "Training Dungeon"),

        EXPERIMENT("Experiment", "Experiements Dungeon"),

        HILL_GIANT("Hill giant", "Training Dungeon"),

        MOSS_GIANT("Moss giant", "Training Dungeon"),

        EARTH_WARRIOR("Earth warrior", "Training Dungeon"),

        GHOST("Ghost", "Training Dungeon"),

        SKELETON("Skeleton", "Training Dungeon"),

        GIANT_BAT("Giant bat", "Experiments Dungeon"),

        CRAWLING_HAND("Crawling hand", "Slayer Tower"),

        KALPHITE_WORK("Kalphite worker", "Kalphites Cave"),

        BANSHEE("Banshee", "Slayer Tower");

        public final String name, area;
        public final byte lvl;

        LowLevel(String name, String area) {
            this.name = name;
            this.area = area;
            lvl = SlayerMonsters.getLevelForName(name.toLowerCase());
        }
    }

    public enum MediumLevel {

        LESSER_DEMON("Lesser demon", "Mid Dungeon"),

        GREATER_DEMON("Greater demon", "Mid Dungeon"),

        GREEN_DRAGON("Green dragon", "Wilderness"),

        FIRE_GIANT("Fire giant", "Mid Dungeon"),

        INFERNAL_MAGE("Infernal mage", "Slayer Tower"),

        DUST_DEVIL("Dust devil", "Slayer Tower"),

        BLOOD_VELD("Bloodveld", "Slayer Tower"),

        ICE_WARRIOR("Ice warrior", "Asgarnian Dungeon"),

        TERROR_DOG("Terror Dog", "Dags Cave"),

        KURASK("Kurask", "Experiments Dungeon"),

        KALPHITE_SOLD("Kalphite soldier", "Kalphites Cave"),

        WALLASALKI("Wallasalki", "Dags Cave"),

        DAGANNOTH("Dagannoth", "Dags Cave");

        public final String name, area;
        public final byte lvl;

        MediumLevel(String name, String area) {
            this.name = name;
            this.area = area;
            lvl = SlayerMonsters.getLevelForName(name.toLowerCase());
        }
    }
}
