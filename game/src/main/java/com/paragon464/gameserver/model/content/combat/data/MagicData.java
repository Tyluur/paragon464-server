package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class MagicData {

    public static final int[] SPELL_LEVEL = {
        /* Modern spells */
        1, 3, 5, 9, 11, 13, 17, 19, 20, 23, 29, 35, 39, 41, 47, 50, 50, 50, 53, 59, 60, 60, 60, 62, 65, 66, 70, 73,
        74, 75, 79, 80, 82, 85, 90,
        /* Ancient spells */
        50, 52, 56, 58, 61, 62, 64, 68, 70, 73, 74, 76, 80, 82, 85, 86, 88, 92, 94, 97,
        /* Lunar spells */
        93
    };

    public static final int[] START_GFX = {
        /* Modern spells */
        90, 102, 93, 96, 105, 99, 117, 108, 177, 120, 123, 126, 145, 132, 135, 87, 177, 327, 138, 129, -1, -1, -1,
        158, 161, 167, 164, 170, -1, 155, 177, 173, -1, 1842, -1,
        /* Ancient spells */
        -1, -1, -1, -1, 1845, -1, -1, -1, -1, 1848, -1, -1, -1, 366, 1850, -1, -1, -1, -1, 1853,
        /* Lunar spells */
        -1
    };

    public static final int[] END_GFX = {
        /* Modern spells */
        92, 104, 95, 98, 107, 101, 119, 110, 181, 122, 125, 128, 147, 134, 137, 89, 180, 329, 140, 131, 76, 77, 78,
        160, 163, 169, 166, 172, -1, 157, 179, 175, -1, 1843, -1,
        /* Ancient spells */
        385, 379, 373, 361, 1847, 389, 382, 376, 363, 1849, 387, 381, 375, 367, 1851, 391, 383, 377, 369, 1854,
        /* Lunar spells */
        -1
    };

    public static final int[] PROJECTILE_GFX = {
        /* Modern spells */
        91, 103, 94, 97, 106, 100, 118, 109, 178, 121, 124, 127, 146, 133, 136, 88, 178, 328, 139, 130, -1, -1, -1,
        159, 162, 168, 165, 171, -1, 156, 178, 174, -1, 1842, -1,
        /* Ancient spells */
        384, 378, -1, 360, 1846, -1, -1, -1, -1, -1, 386, 380, 374, -1, 1852, -1, -1, -1, -1, -1,
        /* Lunar spells */
        -1
    };

    public static final int[] SPELL_MAX_HIT = {
        /* Modern spells */
        5, -1, 6, 7, -1, 8, 9, -1, -1, 10, 11, 12, 15, 13, 14, -1, 2, 15, 15, 16, 20, 20, 20, 17, 18, -1, 19, -1,
        -1, 20, 3, -1, -1, -1, -1,
        /* Ancient spells */
        15, 16, 17, 18, 18, 19, 20, 21, 22, 24, 23, 24, 25, 26, 28, 27, 28, 29, 30, 35,
        /* Lunar spells */
        -1
    };
    public static final int[] FREEZE_TIMERS = {
        /* Modern spells */
        -1, -1, -1, -1, -1, -1, -1, -1, 8, -1, -1, -1, -1, -1, -1, -1, 17, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 25, -1, -1, -1, -1,
        /* Ancient spells */
        -1, -1, -1, 8, -1, -1, -1, -1, 17, -1, -1, -1, -1, 25, -1, -1, -1, -1, 33, -1,
        /* Lunar spells */
        -1
    };
    public static final Item[][] RUNES = {{new Item(556, 1), new Item(558, 1)}, // Wind
        // strike
        {new Item(555, 3), new Item(557, 2), new Item(559, 1)}, // Confuse.
        {new Item(555, 1), new Item(556, 1), new Item(558, 1)}, // Water
        // strike.
        {new Item(557, 2), new Item(556, 1), new Item(558, 1)}, // Earth
        // strike.
        {new Item(555, 3), new Item(557, 2), new Item(559, 1)}, // Weaken.
        {new Item(554, 3), new Item(556, 1), new Item(558, 1)}, // Fire
        // strike.
        {new Item(556, 1), new Item(562, 1)}, // Wind bolt.
        {new Item(555, 2), new Item(557, 3), new Item(559, 1)}, // Curse.
        {new Item(557, 3), new Item(555, 3), new Item(561, 2)}, // Bind.
        {new Item(555, 2), new Item(556, 2), new Item(562, 1)}, // Water
        // bolt.
        {new Item(557, 3), new Item(556, 2), new Item(562, 1)}, // Earth
        // bolt.
        {new Item(554, 3), new Item(556, 2), new Item(562, 1)}, // Fire
        // bolt.
        {new Item(557, 2), new Item(556, 2), new Item(562, 1)}, // Crumble
        // undead.
        {new Item(556, 3), new Item(560, 1)}, // Wind blast.
        {new Item(555, 3), new Item(556, 3), new Item(560, 1)}, // Water
        // blast.
        {new Item(554, 5), new Item(560, 1)}, // Iban blast.
        {new Item(557, 4), new Item(555, 4), new Item(561, 3)}, // Snare.
        {new Item(560, 1), new Item(558, 4)}, // Magic dart.
        {new Item(557, 4), new Item(556, 3), new Item(560, 1)}, // Earth
        // blast.
        {new Item(554, 5), new Item(556, 4), new Item(560, 1)}, // Fire
        // blast.
        {new Item(554, 2), new Item(565, 2), new Item(556, 4)}, // Saradomin
        // strike.
        {new Item(554, 1), new Item(565, 2), new Item(556, 4)}, // Claws
        // of
        // guthix.
        {new Item(554, 4), new Item(565, 2), new Item(556, 1)}, // Flames
        // of
        // zamorak.
        {new Item(556, 5), new Item(565, 1)}, // Wind wave.
        {new Item(555, 7), new Item(556, 5), new Item(565, 1)}, // Water
        // wave.
        {new Item(557, 5), new Item(555, 5), new Item(566, 1)}, // Vulnerability.
        {new Item(557, 7), new Item(556, 5), new Item(565, 1)}, // Earth
        // wave.
        {new Item(557, 8), new Item(555, 8), new Item(566, 1)}, // Enfeeble.
        {new Item(566, 1), new Item(563, 1), new Item(557, 1)}, // Teleother
        // lumbridge.
        {new Item(554, 7), new Item(556, 5), new Item(565, 1)}, // Fire
        // wave.
        {new Item(557, 5), new Item(555, 5), new Item(561, 4)}, // Entangle.
        {new Item(557, 12), new Item(555, 12), new Item(566, 1)}, // Stun.
        {new Item(566, 1), new Item(555, 1), new Item(563, 1)}, // Teleother
        // Falador.
        {new Item(562, 1), new Item(563, 1), new Item(560, 1)}, // Teleblock.
        {new Item(566, 2), new Item(563, 1)}, // Teleother camelot.
        {new Item(562, 2), new Item(560, 2), new Item(554, 1), new Item(556, 1)}, // Smoke
        // rush
        {new Item(562, 2), new Item(560, 2), new Item(556, 1), new Item(566, 1)}, // Shadow
        // rush
        {new Item(562, 2), new Item(560, 2), new Item(565, 1)}, // Blood
        // rush
        {new Item(562, 2), new Item(560, 2), new Item(555, 2)}, // Ice
        // rush
        {new Item(562, 2), new Item(557, 1), new Item(566, 1)}, // Miasmic
        // rush
        {new Item(562, 4), new Item(560, 2), new Item(554, 2), new Item(556, 2)}, // Smoke
        // burst
        {new Item(562, 4), new Item(560, 2), new Item(556, 2), new Item(566, 2)}, // Shadow
        // burst
        {new Item(562, 4), new Item(560, 2), new Item(565, 2)}, // Blood
        // burst
        {new Item(562, 4), new Item(560, 2), new Item(555, 4)}, // Ice
        // burst
        {new Item(562, 4), new Item(557, 2), new Item(566, 2)}, // Miasmic
        // burst
        {new Item(560, 2), new Item(565, 2), new Item(554, 2), new Item(556, 2)}, // Smoke
        // blitz
        {new Item(560, 2), new Item(565, 2), new Item(556, 2), new Item(566, 2)}, // Shadow
        // blitz
        {new Item(560, 2), new Item(565, 4)}, // Blood blitz
        {new Item(560, 2), new Item(565, 2), new Item(555, 3)}, // Ice
        // blitz
        {new Item(565, 2), new Item(557, 3), new Item(566, 3)}, // Miasmic
        // blitz
        {new Item(560, 4), new Item(565, 2), new Item(554, 4), new Item(556, 4)}, // Smoke
        // barrage
        {new Item(560, 4), new Item(565, 2), new Item(556, 4), new Item(566, 3)}, // Shadow
        // barrage
        {new Item(560, 4), new Item(565, 4), new Item(566, 1)}, // Blood
        // barrage
        {new Item(560, 4), new Item(565, 2), new Item(555, 6)}, // Ice
        // barrage
        {new Item(565, 4), new Item(557, 4), new Item(566, 4)},// Miasmic
        // barrage
        {new Item(9075, 3), new Item(560, 2), new Item(557, 10)},//venge other
    };
    public static int[] SPELL_STAFFS = {
        /* Modern spells */
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1409, -1, 4170, -1, -1, 2415, 2416, 2417, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        /* Ancient spells */
        -1, -1, -1, -1, 13867, -1, -1, -1, -1, 13867, -1, -1, -1, -1, 13867, -1, -1, -1, -1, 13867,
        /* Lunar spells */
        -1
    };

    public static boolean isMultiSpell(int spell) {
        switch (spell) {
            case 40:// Smoke burst
            case 41:// Shadow burst
            case 42:// Blood burst
            case 43:// Ice burst
            case 50:// Smoke barrage
            case 51:// Shadow barrage
            case 52:// Blood barrage
            case 53:// Ice barrage
            case 44:// Miasmic burst
            case 54:// Miasmic barrage
                return true;
        }
        return false;
    }

    public static int getSpellEndGfxHeight(Player player, int spell) {
        String name = getSpellName(player, spell);
        if (name == null) {
            return -1;
        }
        if (name.endsWith("Rush")) {
            return 0;
        }
        if (name.endsWith("Burst")) {
            return 0;
        }
        if (name.endsWith("Blitz")) {
            return 0;
        }
        if (name.endsWith("Barrage")) {
            return 0;
        }
        if (name.equals("Teleport Block")) {
            return 0;
        }
        if (name.equals("Flames of Zamorak")) {
            return 0;
        }
        return 100;
    }

    public static String getSpellName(Player player, int spellIndex) {
        if (player.getSettings().getMagicType() == 1) {// Modern
            switch (spellIndex) {
                case 0:
                    return "Wind Strike";
                case 1:
                    return "Confuse";
                case 2:
                    return "Water Strike";
                case 3:
                    return "Earth Strike";
                case 4:
                    return "Weaken";
                case 5:
                    return "Fire Strike";
                case 6:
                    return "Wind Bolt";
                case 7:
                    return "Curse";
                case 8:
                    return "Bind";
                case 9:
                    return "Water Bolt";
                case 10:
                    return "Earth Bolt";
                case 11:
                    return "Fire Bolt";
                case 12:
                    return "Crumble Undead";
                case 13:
                    return "Wind Blast";
                case 14:
                    return "Water Blast";
                case 15:
                    return "Iban Blast";
                case 16:
                    return "Snare";
                case 17:
                    return "Magic Dart";
                case 18:
                    return "Earth Blast";
                case 19:
                    return "Fire Blast";
                case 20:
                    return "Saradomin Strike";
                case 21:
                    return "Claws of Guthix";
                case 22:
                    return "Flames of Zamorak";
                case 23:
                    return "Wind Wave";
                case 24:
                    return "Water Wave";
                case 25:
                    return "Vulnerability";
                case 26:
                    return "Earth Wave";
                case 27:
                    return "Enfeeble";
                case 28:
                    return "Teleother Lumbridge";
                case 29:
                    return "Fire Wave";
                case 30:
                    return "Entangle";
                case 31:
                    return "Stun";
                case 32:
                    return "Teleother Falador";
                case 33:
                    return "Teleport Block";
            }
        } else if (player.getSettings().getMagicType() == 2) {// Ancient
            switch (spellIndex) {
                case 35:
                    return "Smoke Rush";
                case 36:
                    return "Shadow Rush";
                case 37:
                    return "Blood Rush";
                case 38:
                    return "Ice Rush";
                case 39:
                    return "Miasmic Rush";
                case 40:
                    return "Smoke Burst";
                case 41:
                    return "Shadow Burst";
                case 42:
                    return "Blood Burst";
                case 43:
                    return "Ice Burst";
                case 44:
                    return "Miasmic Burst";
                case 45:
                    return "Smoke Blitz";
                case 46:
                    return "Shadow Blitz";
                case 47:
                    return "Blood Blitz";
                case 48:
                    return "Ice Blitz";
                case 49:
                    return "Miasmic Blitz";
                case 50:
                    return "Smoke Barrage";
                case 51:
                    return "Shadow Barrage";
                case 52:
                    return "Blood Barrage";
                case 53:
                    return "Ice Barrage";
                case 54:
                    return "Miasmic Barrage";
            }
        } else if (player.getSettings().getMagicType() == 3) {// Lunar
            switch (spellIndex) {
                case 55:
                    return "Vengeance Other";
            }
        }
        return "Unsupported spell.";
    }

    public static int getSpellProjectileStartHeight(Player player, int spell) {
        String name = getSpellName(player, spell);
        if (name.equalsIgnoreCase("Teleport Block")) {
            return 35;
        }
        if (name.equalsIgnoreCase("blood blitz")) {
            return 20;
        }
        return 45;
    }

    public static int getSpellProjectileEndHeight(Player player, int spell) {
        String name = getSpellName(player, spell);
        if (name.equalsIgnoreCase("blood blitz")) {
            return 0;
        }
        return 40;
    }

    public static int getSpellStartHeight(Player player, int spell) {
        String name = getSpellName(player, spell);
        if (name.equals("Teleport Block")) {
            return 30;
        }
        if (name.startsWith("Miasmic")) {
            return 0;
        }
        return 100;
    }

    public static final int getSpellAnimation(Player player, int spell) {
        String name = getSpellName(player, spell);
        if (containsElemental(name)) {
            if (name.endsWith("Strike")) {
                return 1162;
            }
            if (name.endsWith("Bolt")) {
                return 1162;
            }
            if (name.endsWith("Blast")) {
                return 1162;
            }
            if (name.endsWith("Wave")) {
                return 1167;
            }
        }
        if (name.equals("Confuse")) {
            return 1165;
        }
        if (name.equals("Weaken")) {
            return 1164;
        }
        if (name.equals("Curse")) {
            return 1163;
        }
        if (name.equals("Bind")) {
            return 1161;
        }
        if (name.equals("Crumble Undead")) {
            return 1165;
        }
        if (name.equals("Iban Blast")) {
            return 708;
        }
        if (name.equals("Snare")) {
            return 1161;
        }
        if (name.equals("Magic Dart")) {
            return 1576;
        }
        if (isGodSpell(spell)) {
            return 811;
        }
        if (name.equals("Entangle")) {
            return 1161;
        }
        if (name.equals("Teleport Block")) {
            return 10503;
        }
        if (name.equals("Vulnerability") || name.equals("Enfeeble") || name.equals("Stun")) {
            return 729;
        }
        if (!name.contains("Miasmic")) {
            if (name.endsWith("Rush")) {
                return 1978;
            }
            if (name.endsWith("Burst")) {
                return 1979;
            }
            if (name.endsWith("Blitz")) {
                return 1978;
            }
            if (name.endsWith("Barrage")) {
                return 1979;
            }
        } else if (name.contains("Miasmic")) {
            if (name.endsWith("Rush")) {
                return 10513;
            }
            if (name.endsWith("Burst")) {
                return 10516;
            }
            if (name.endsWith("Blitz")) {
                return 10524;
            }
            if (name.endsWith("Barrage")) {
                return 10518;
            }
        }
        if (name.equals("Vengeance Other")) {
            return 4411;
        }
        return -1;
    }

    private static boolean containsElemental(String name) {
        return (name.startsWith("Wind") || name.startsWith("Water") || name.startsWith("Earth")
            || name.startsWith("Fire"));
    }

    private static boolean isGodSpell(int spell) {
        return (spell == 20 || spell == 21 || spell == 22);
    }

    public static int getSpellForAutoCastButton(Player player, int button) {
        if (player.getSettings().getMagicType() == 1) {// Modern
            switch (button) {
                case 0:// wind strike
                    return 0;
                case 1:// water strike
                    return 2;
                case 2:// earth strike
                    return 3;
                case 3:// fire strike
                    return 5;
                case 4:// wind bolt
                    return 6;
                case 5:// water bolt
                    return 9;
                case 6:// earth bolt
                    return 10;
                case 7:// fire bolt
                    return 11;
                case 8:// wind blast
                    return 13;
                case 9:// water blast
                    return 14;
                case 10:// earth blast
                    return 18;
                case 11:// fire blast
                    return 19;
                case 12:// wind wave
                    return 23;
                case 13:// water wave
                    return 24;
                case 14:// earth wave
                    return 26;
                case 15:// fire wave
                    return 29;
            }
        } else if (player.getSettings().getMagicType() == 2) {// Ancients
            switch (button) {
                case 93:// Smoke rush
                    return 35;
                case 145:// Shadow rush
                    return 36;
                case 51:// Blood rush
                    return 37;
                case 7:// Ice rush
                    return 38;
                case 119:// Smoke burst
                    return 40;
                case 171:// Shadow burst
                    return 41;
                case 71:// Blood burst
                    return 42;
                case 29:// Ice burst
                    return 43;
                case 106:// Smoke blitz
                    return 45;
                case 158:// Shadow blitz
                    return 46;
                case 62:// Blood blitz
                    return 47;
                case 18:// Ice blitz
                    return 48;
                case 132:// Smoke barrage
                    return 50;
                case 184:// Shadow barrage
                    return 51;
                case 82:// Blood barrage
                    return 52;
                case 40:// Ice barrage
                    return 53;
            }
        }
        return -1;
    }

    public static int getSpellMaxHit(Mob mob, Mob target, int spell, int wep) {
        Player player = null;
        if (mob.isPlayer()) {
            player = (Player) mob;
        }
        int damage = getMaxHit(mob, spell, wep);
        if (spell == 20 || spell == 21 || spell == 22) {
            if (player != null) {
                if (player.getCombatState().isCharged()) {
                    damage = 30;
                }
            }
        }
        if (target.isPlayer()) {
        } else if (target.isNPC()) {
        }
        damage = (damage > -1 ? NumberUtils.random(damage) : damage);
        return damage;
    }

    public static int getMaxHit(Mob mob, int spell, int wep) {
        double damage = SPELL_MAX_HIT[spell];
        return (int) damage;
    }

    public static boolean freezeTarget(final Mob attacker, final Mob target, boolean halve, int spell,
                                       int damage) {
        int time = (FREEZE_TIMERS[spell]);
        int index = getSpellIndex(attacker, spell);
        if (target.getAttributes().isSet("stopActions")) {
            return false;
        }
        if (index == 8 || index == 16 || index == 30) {
            if (halve) {
                time /= 2;
            }
        }
        if (target.getCombatState().isFreezable()) {
            if (target.isPlayer()) {
                ((Player) target).getFrames().sendMessage("You have been frozen!");
            }
            target.getCombatState().end(1);
            target.getCombatState().setFreezable(false);
            target.getCombatState().setFrozen(true);
            final int finalTimer = time;
            target.submitTickable(new Tickable(0) {
                int count = finalTimer;

                @Override
                public void execute() {
                    if (!attacker.getPosition().isWithinRadius(target.getCentreLocation(), 11)) {
                        target.getCombatState().setFrozen(false);
                        target.submitTickable(new Tickable(7) {
                            @Override
                            public void execute() {
                                this.stop();
                                target.getCombatState().setFreezable(true);
                            }
                        });
                        this.stop();
                        return;
                    }
                    if (count > 0) {
                        count--;
                    } else if (count == 0) {
                        this.stop();
                        target.getCombatState().setFrozen(false);
                        World.getWorld().submit(new Tickable(6) {
                            @Override
                            public void execute() {
                                this.stop();
                                target.getCombatState().setFreezable(true);
                            }
                        });
                    }
                }
            });
        }
        return true;
    }

    public static int getSpellIndex(Mob attacker, int spell) {
        if (attacker.isNPC()) {
            return -1;
        }
        Player player = (Player) attacker;
        if (player.getSettings().getMagicType() == 1) {// Modern
            switch (spell) {
                case 1:// Wind Strike
                    return 0;
                case 2:// Confuse
                    return 1;
                case 4:// Water Strike
                    return 2;
                case 6:// Earth strike
                    return 3;
                case 7:// Weaken
                    return 4;
                case 8:// Fire Strike
                    return 5;
                case 10:// Wind Bolt
                    return 6;
                case 11:// Curse
                    return 7;
                case 12:// Bind
                    return 8;
                case 14:// Water Bolt
                    return 9;
                case 17:// Earth Bolt
                    return 10;
                case 20:// Fire Bolt
                    return 11;
                case 22:// Crumble undead
                    return 12;
                case 24:// Wind blast
                    return 13;
                case 27:// Water blast
                    return 14;
                case 29:// Iban blast
                    return 15;
                case 30:// Snare
                    return 16;
                case 31:// Magic dart
                    return 17;
                case 33:// Earth blast
                    return 18;
                case 38:// Fire blast
                    return 19;
                case 41:// Saradomin strike
                    return 20;
                case 42:// Claws of guthix
                    return 21;
                case 43:// Flames of zamorak
                    return 22;
                case 45:// Wind wave
                    return 23;
                case 48:// Water wave
                    return 24;
                case 50:// Vulnerability
                    return 25;
                case 52:// Earth wave
                    return 26;
                case 53:// Enfeeble
                    return 27;
                case 54:// Teleother lumby
                    return 28;
                case 55:// Fire wave
                    return 29;
                case 56:// Entangle
                    return 30;
                case 57:// Stun
                    return 31;
                case 59:// Teleother fally
                    return 32;
                case 60:// Teleport block
                    return 33;
                case 62:// Teleother cammy
                    return 34;
            }
        } else if (player.getSettings().getMagicType() == 2) {// Ancient
            switch (spell) {
                case 8:// Smoke rush
                    return 35;
                case 12:// Shadow rush
                    return 36;
                case 4:// Blood rush
                    return 37;
                case 0:// Ice rush
                    return 38;
                case 16:// Miasmic rush
                    return 39;
                case 10:// Smoke burst
                    return 40;
                case 14:// Shadow burst
                    return 41;
                case 6:// Blood burst
                    return 42;
                case 2:// Ice burst
                    return 43;
                case 18:// Miasmic burst
                    return 44;
                case 9:// Smoke blitz
                    return 45;
                case 13:// Shadow blitz
                    return 46;
                case 5:// Blood blitz
                    return 47;
                case 1:// Ice blitz
                    return 48;
                case 17:// Miasmic blitz
                    return 49;
                case 11:// Smoke barrage
                    return 50;
                case 15:// Shadow barrage
                    return 51;
                case 7:// Blood barrage
                    return 52;
                case 3:// Ice barrage
                    return 53;
                case 19:// Miasmic barrage
                    return 54;
            }
        } else if (player.getSettings().getMagicType() == 3) {// Lunar
            switch (spell) {
                case 19:// Venge other
                    return 55;
            }
        }
        return -1;
    }

    public static Hits.Hit executeTeleBlock(Player player, final Mob target) {
        boolean halve = false;
        if (target.isPlayer()) {
            if (((Player) target).getPrayers().isPrayingMagic()) {
                halve = true;
            }
        }
        return new Hits.Hit(player, halve ? 0 : 1);
    }
}
