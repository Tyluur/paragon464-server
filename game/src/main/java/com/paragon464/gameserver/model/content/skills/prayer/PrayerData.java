package com.paragon464.gameserver.model.content.skills.prayer;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class PrayerData {

    protected static final int SKILL_ID = 5;
    protected static final int BURY_ANIMATION = 827;
    private static final Set<PRAYERS_DATA> PRAYERS_SET = Collections.unmodifiableSet(EnumSet.allOf(PRAYERS_DATA.class));
    private static final Set<BONES> BONES_SET = Collections.unmodifiableSet(EnumSet.allOf(BONES.class));

    public static PRAYERS_DATA getPrayers(int index) {
        return PRAYERS_SET.stream().filter(amlt -> amlt.ordinal() == index).findFirst().orElse(null);
    }

    public static PRAYERS_DATA getPrayers(String name) {
        return PRAYERS_SET.stream().filter(amlt -> amlt.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static BONES getBones(Item item) {
        return BONES_SET.stream().filter(amlt -> amlt.getId() == item.getId()).findFirst().orElse(null);
    }

    protected static PRAYERS_DATA getQuickPrayer(Player player, int button) {
        if (player.getSettings().isCursesEnabled()) {
            switch (button) {
                case 4:
                    return PRAYERS_DATA.CURSES_PROTECT_ITEM;
                case 5:
                    return PRAYERS_DATA.SAP_WARRIOR;
                case 6:
                    return PRAYERS_DATA.SAP_RANGER;
                case 7:
                    return PRAYERS_DATA.SAP_MAGIC;
                case 8:
                    return PRAYERS_DATA.SAP_SPIRIT;
                case 9:
                    return PRAYERS_DATA.BERSERKER;
                case 10:
                    return PRAYERS_DATA.DEFLECT_MAGIC;
                case 11:
                    return PRAYERS_DATA.DEFLECT_MISSILES;
                case 12:
                    return PRAYERS_DATA.DEFLECT_MELEE;
                case 13:
                    return PRAYERS_DATA.LEECH_ATTACK;
                case 14:
                    return PRAYERS_DATA.LEECH_RANGED;
                case 15:
                    return PRAYERS_DATA.LEECH_MAGIC;
                case 16:
                    return PRAYERS_DATA.LEECH_DEFENCE;
                case 17:
                    return PRAYERS_DATA.LEECH_STRENGTH;
                case 18:
                    return PRAYERS_DATA.LEECH_PRAYER;
                case 19:
                    return PRAYERS_DATA.LEECH_ENERGY;
                case 20:
                    return PRAYERS_DATA.LEECH_SPECIAL;
                case 21:
                    return PRAYERS_DATA.WRATH;
                case 22:
                    return PRAYERS_DATA.SOUL_SPLIT;
                case 23:
                    return PRAYERS_DATA.TURMOIL;
            }
        } else {
            switch (button) {
                case 4:
                    return PRAYERS_DATA.THICK_SKIN;
                case 5:
                    return PRAYERS_DATA.BURST_OF_SKIN;
                case 6:
                    return PRAYERS_DATA.CLARITY_OF_THOUGHT;
                case 7:
                    return PRAYERS_DATA.SHARP_EYE;
                case 8:
                    return PRAYERS_DATA.MYSTIC_WILL;
                case 9:
                    return PRAYERS_DATA.ROCK_SKIN;
                case 10:
                    return PRAYERS_DATA.SUPERHUMAN_STRENGTH;
                case 11:
                    return PRAYERS_DATA.IMPROVED_RFLEXES;
                case 12:
                    return PRAYERS_DATA.RAPID_RESTORE;
                case 13:
                    return PRAYERS_DATA.RAPID_HEAL;
                case 14:
                    return PRAYERS_DATA.PROTECT_ITEM;
                case 15:
                    return PRAYERS_DATA.HAWK_EYE;
                case 16:
                    return PRAYERS_DATA.MYSTIC_LORE;
                case 17:
                    return PRAYERS_DATA.STEEL_SKIN;
                case 18:
                    return PRAYERS_DATA.ULTIMATE_STRENGTH;
                case 19:
                    return PRAYERS_DATA.INCREDIBLE_REFLEXES;
                case 20:
                    return PRAYERS_DATA.PROTECT_MAGIC;
                case 21:
                    return PRAYERS_DATA.PROTECT_RANGE;
                case 22:
                    return PRAYERS_DATA.PROTECT_MELEE;
                case 23:
                    return PRAYERS_DATA.EAGLE_EYE;
                case 24:
                    return PRAYERS_DATA.MYSTIC_MIGHT;
                case 25:
                    return PRAYERS_DATA.RETRIBUTION;
                case 26:
                    return PRAYERS_DATA.REDEMPTION;
                case 27:
                    return PRAYERS_DATA.SMITE;
                case 28:
                    return PRAYERS_DATA.CHIVARLY;
                case 29:
                    return PRAYERS_DATA.PIETY;
            }
        }
        return null;
    }

    protected static PRAYERS_DATA getPrayer(Player player, int button) {
        if (player.getSettings().isCursesEnabled()) {
            switch (button) {
                case 12://Soul split
                    return PRAYERS_DATA.SOUL_SPLIT;
                case 13://Turmoil
                    return PRAYERS_DATA.TURMOIL;
                case 14://Berserker
                    return PRAYERS_DATA.BERSERKER;
                case 15://Protect item
                    return PRAYERS_DATA.CURSES_PROTECT_ITEM;
                case 16://Sap warrior
                    return PRAYERS_DATA.SAP_WARRIOR;
                case 17://Sap Range
                    return PRAYERS_DATA.SAP_RANGER;
                case 18://Sap Mage
                    return PRAYERS_DATA.SAP_MAGIC;
                case 19://Sap spirit
                    return PRAYERS_DATA.SAP_SPIRIT;
                case 0://Deflect magic
                    return PRAYERS_DATA.DEFLECT_MAGIC;
                case 1://Deflect range
                    return PRAYERS_DATA.DEFLECT_MISSILES;
                case 2://Deflect melee
                    return PRAYERS_DATA.DEFLECT_MELEE;
                case 3://Leech attack
                    return PRAYERS_DATA.LEECH_ATTACK;
                case 4://Leech range
                    return PRAYERS_DATA.LEECH_RANGED;
                case 5://Leech magic
                    return PRAYERS_DATA.LEECH_MAGIC;
                case 6://Leech def
                    return PRAYERS_DATA.LEECH_DEFENCE;
                case 7://Leech strength
                    return PRAYERS_DATA.LEECH_STRENGTH;
                case 8://Leech prayer
                    return PRAYERS_DATA.LEECH_PRAYER;
                case 9://Leech energy
                    return PRAYERS_DATA.LEECH_ENERGY;
                case 10://Leech spec
                    return PRAYERS_DATA.LEECH_SPECIAL;
                case 11://Wrath
                    return PRAYERS_DATA.WRATH;
            }
        } else {
            switch (button) {
                case 5:// Thick skin
                    return PRAYERS_DATA.THICK_SKIN;
                case 7:// Burst of skin
                    return PRAYERS_DATA.BURST_OF_SKIN;
                case 9:// Clarity of thought
                    return PRAYERS_DATA.CLARITY_OF_THOUGHT;
                case 11:// Sharp eye
                    return PRAYERS_DATA.SHARP_EYE;
                case 13:// Mystic Will
                    return PRAYERS_DATA.MYSTIC_WILL;
                case 15:// Rock Skin
                    return PRAYERS_DATA.ROCK_SKIN;
                case 17:// Superhuman strength
                    return PRAYERS_DATA.SUPERHUMAN_STRENGTH;
                case 19:// Improved reflexes
                    return PRAYERS_DATA.IMPROVED_RFLEXES;
                case 21:// Rapid restore
                    return PRAYERS_DATA.RAPID_RESTORE;
                case 23:// Rapid heal
                    return PRAYERS_DATA.RAPID_HEAL;
                case 25:// Protect 1 item
                    return PRAYERS_DATA.PROTECT_ITEM;
                case 27:// Hawk eye
                    return PRAYERS_DATA.HAWK_EYE;
                case 29:// Mystic Lore
                    return PRAYERS_DATA.MYSTIC_LORE;
                case 31:// Steel skin
                    return PRAYERS_DATA.STEEL_SKIN;
                case 33:// Ultimate str
                    return PRAYERS_DATA.ULTIMATE_STRENGTH;
                case 35:// Incredible Reflexes
                    return PRAYERS_DATA.INCREDIBLE_REFLEXES;
                case 37:// Protect from magic
                    return PRAYERS_DATA.PROTECT_MAGIC;
                case 39:// Protect from missles
                    return PRAYERS_DATA.PROTECT_RANGE;
                case 41:// Protect from melee
                    return PRAYERS_DATA.PROTECT_MELEE;
                case 43:// Eagle eye
                    return PRAYERS_DATA.EAGLE_EYE;
                case 45:// Mystic might
                    return PRAYERS_DATA.MYSTIC_MIGHT;
                case 47:// Retribution
                    return PRAYERS_DATA.RETRIBUTION;
                case 49:// Redemption
                    return PRAYERS_DATA.REDEMPTION;
                case 51:// Smite
                    return PRAYERS_DATA.SMITE;
                case 53:// Chivarly
                    return PRAYERS_DATA.CHIVARLY;
                case 55:// Piety
                    return PRAYERS_DATA.PIETY;
            }
        }
        return null;
    }

    public enum BONES {
        NORMAL(526, 4.5),
        WOLF(2859, 4.5),
        BURNT(528, 4.5),
        MONKEY(3179, 5),
        BAT(530, 5.3),
        BIG(532, 15),
        CURVED(10977, 15),
        LONG(10976, 15),
        JOGRE(3125, 15),
        ZOGRE(4812, 15),
        SHAIKAHAN(3123, 25),
        BABY_DRAGON(534, 30),
        WYVERN(6812, 50),
        DRAGON(536, 72),
        FAYRG(4830, 84),
        RAURG(4832, 96),
        DAGANNOTH(6729, 125),
        OURG(4834, 140);

        private int id;
        private double exp;

        BONES(int id, double exp) {
            this.id = id;
            this.exp = exp;
        }

        public int getId() {
            return id;
        }

        public double getExp() {
            return exp;
        }
    }

    protected enum PRAYERS_DATA {
        THICK_SKIN("Thick Skin", 0, 1, 83, 3, new int[]{5, 13, 24, 25}),
        BURST_OF_SKIN("Burst of Strength", 1, 4, 84, 3, new int[]{6, 14, 3, 11, 19, 4, 12, 20, 24, 25}),
        CLARITY_OF_THOUGHT("Clarity of Thought", 2, 7, 85, 3, new int[]{7, 15, 3, 11, 19, 4, 12, 20, 24, 25}),
        SHARP_EYE("Sharp Eye", 3, 8, 862, 3, new int[]{11, 19, 4, 12, 20, 14, 15, 6, 7, 1, 2, 24, 25}),
        MYSTIC_WILL("Mystic Will", 4, 9, 863, 3, new int[]{12, 20, 3, 11, 19, 14, 15, 6, 7, 1, 2, 24, 25}),
        ROCK_SKIN("Rock Skin", 5, 10, 86, 6, new int[]{0, 13, 24, 25}),
        SUPERHUMAN_STRENGTH("Superhuman Strength", 6, 13, 87, 6, new int[]{1, 14, 3, 11, 19, 4, 12, 20, 24, 25}),
        IMPROVED_RFLEXES("Improved Reflexes", 7, 16, 88, 6, new int[]{2, 15, 3, 11, 19, 4, 12, 20, 24, 25}),
        RAPID_RESTORE("Rapid Restore", 8, 19, 89, 1, null),
        RAPID_HEAL("Rapid Heal", 9, 22, 90, 2, null),
        PROTECT_ITEM("Protect Item", 10, 25, 91, 2, null),
        HAWK_EYE("Hawk Eye", 11, 26, 864, 6, new int[]{3, 19, 4, 12, 20, 14, 15, 6, 7, 1, 2, 24, 25}),
        MYSTIC_LORE("Mystic Lore", 12, 27, 865, 6, new int[]{4, 20, 3, 11, 19, 14, 15, 6, 7, 1, 2, 24, 25}),
        STEEL_SKIN("Steel Skin", 13, 28, 92, 12, new int[]{0, 5, 24, 25}),
        ULTIMATE_STRENGTH("Ultimate Strength", 14, 31, 93, 12, new int[]{1, 6, 3, 11, 19, 4, 12, 20, 24, 25}),
        INCREDIBLE_REFLEXES("Incredible Reflexes", 15, 34, 94, 12, new int[]{2, 7, 3, 11, 19, 4, 12, 20, 24, 25}),
        PROTECT_MAGIC("Protect from Magic", 16, 37, 95, 12, new int[]{17, 18, 21, 22, 23}),
        PROTECT_RANGE("Protect from Ranged", 17, 40, 96, 12, new int[]{16, 18, 21, 22, 23}),
        PROTECT_MELEE("Protect from Melee", 18, 43, 97, 12, new int[]{16, 17, 21, 22, 23}),
        EAGLE_EYE("Eagle Eye", 19, 44, 866, 12, new int[]{3, 11, 4, 12, 20, 14, 15, 6, 7, 1, 2, 24, 25}),
        MYSTIC_MIGHT("Mystic Might", 20, 45, 867, 12, new int[]{4, 12, 3, 11, 19, 14, 15, 6, 7, 1, 2, 24, 25}),
        RETRIBUTION("Retribution", 21, 46, 98, 3, new int[]{16, 18, 17, 22, 23}),
        REDEMPTION("Redemption", 22, 49, 99, 6, new int[]{16, 18, 17, 21, 23}),
        SMITE("Smite", 23, 52, 100, 18, new int[]{16, 18, 17, 21, 22}),
        CHIVARLY("Chivarly", 24, 60, 1052, 24, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 19, 20, 25}),
        PIETY("Piety", 25, 70, 1053, 24, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 19, 20, 24}),
        //START OF CURSES
        CURSES_PROTECT_ITEM("Protect Item", 26, 50, 83, 2, null),
        SAP_WARRIOR("Sap Warrior", 27, 50, 84, 14, new int[]{35, 36, 37, 38, 39, 40, 41, 42, 45}),
        SAP_RANGER("Sap Ranger", 28, 52, 85, 14, new int[]{35, 36, 37, 38, 39, 40, 41, 42, 45}),
        SAP_MAGIC("Sap Magic", 29, 54, 86, 14, new int[]{35, 36, 37, 38, 39, 40, 41, 42, 45}),
        SAP_SPIRIT("Sap Spirit", 30, 56, 87, 14, new int[]{35, 36, 37, 38, 39, 40, 41, 42, 45}),
        BERSERKER("Berserker", 31, 59, 88, 2, null),
        DEFLECT_MAGIC("Deflect Magic", 32, 65, 89, 12, new int[]{33, 34, 44}),
        DEFLECT_MISSILES("Deflect Missles", 33, 68, 90, 12, new int[]{32, 34, 44}),
        DEFLECT_MELEE("Deflect Melee", 34, 71, 91, 12, new int[]{32, 33, 44}),
        LEECH_ATTACK("Leech Attack", 35, 74, 92, 10, new int[]{27, 28, 29, 30, 45}),
        LEECH_RANGED("Leech Ranged", 36, 76, 93, 10, new int[]{27, 28, 29, 30, 45}),
        LEECH_MAGIC("Leech Magic", 37, 78, 94, 10, new int[]{27, 28, 29, 30, 45}),
        LEECH_DEFENCE("Leech Defence", 38, 80, 95, 10, new int[]{27, 28, 29, 30, 45}),
        LEECH_STRENGTH("Leech Strength", 39, 82, 96, 10, new int[]{27, 28, 29, 30, 45}),
        LEECH_PRAYER("Leech Prayer", 40, 83, 97, 10, new int[]{27, 28, 29, 30, 45}),
        LEECH_ENERGY("Leech Energy", 41, 84, 98, 10, new int[]{27, 28, 29, 30, 45}),
        LEECH_SPECIAL("Leech Special Attack", 42, 86, 99, 10, new int[]{27, 28, 29, 30, 45}),
        WRATH("Wrath", 43, 89, 100, 3, null),
        SOUL_SPLIT("Soul Split", 44, 92, 101, 18, new int[]{32, 33, 34}),
        TURMOIL("Turmoil", 45, 95, 102, 18, new int[]{27, 28, 29, 30, 35, 36, 37, 38, 39, 40, 41, 42});

        private String name;
        private int index, req, config, drain;
        private int[] prayersOff;

        PRAYERS_DATA(String name, int index, int req, int config, int drain, int[] prayersOff) {
            this.name = name;
            this.index = index;
            this.drain = drain;
            this.req = req;
            this.config = config;
            this.prayersOff = prayersOff;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }

        public int getReq() {
            return req;
        }

        public int getConfig() {
            return config;
        }

        public int[] getPrayersOff() {
            return prayersOff;
        }

        public int getDrain() {
            return drain;
        }
    }
}
