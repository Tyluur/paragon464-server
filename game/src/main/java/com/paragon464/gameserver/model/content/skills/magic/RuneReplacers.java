package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;

import java.util.Set;

public class RuneReplacers {

    public static final int[] COMBO_RUNES = {
        12850, // elemental rune
        12851, //catalytic rune
        4694, // Steam rune
        4695, // Mist rune
        4696, // Dust rune
        4697, // Smoke rune
        4698, // Mud rune
        4699, // Lava rune
    };

    public static final int[][] COMBO_REPLACERS = {
        {554, 555, 556, 557},//all elementals
        {558, 559, 562, 560, 565, 561, 564, 563, 566, 9075},//mind,body,chaos,death,blood,nature,cosmetic,law,soul,astral
        {554, 555}, // fires,    waters
        {555, 556}, // waters, airs
        {557, 556}, // earths, airs
        {554, 556}, // fires, airs
        {557, 555}, // earths, waters
        {554, 557},// fires, earths
    };

    public static final int[] RUNE_REPLACER = {1387, // Staff of fire.
        1381, // Staff of air.
        1383, // Staff of water.
        1385, // Staff of earth.
        1393, // Fire battlestaff.
        1395, // Water battlestaff.
        1397, // Air battlestaff.
        1399, // Earth battlestaff.
        1401, // Mystic fire staff.
        1403, // Mystic water staff.
        1405, // Mystic air staff.
        1407, // Mystic earth staff.
        3053, // Lava battlestaff.
        3053, // Lava battlestaff.
        3054, // Mystic lava staff.
        3054, // Mystic lava staff.
        6562, // Mud battlestaff.
        6562, // Mud battlestaff.
        6563, // Mystic mud staff.
        6563, // Mystic mud staff.
        11736, // Steam battlstaff.
        11736, // Steam battlstaff.
        11738, // Mystic steam staff.
        11738, // Mystic steam staff.
    };

    protected static final int[] REPLACABLE_RUNES = {554, // Fire rune.
        556, // Air rune.
        555, // Water rune.
        557, // Earth rune.
        554, // Fire rune.
        555, // Water rune.
        556, // Air rune.
        557, // Earth rune.
        554, // Fire rune.
        555, // Water rune.
        556, // Air rune.
        557, // Earth rune.
        557, // Earth rune.
        554, // Fire rune.
        557, // Earth rune.
        554, // Fire rune.
        555, // Water rune.
        557, // Earth rune.
        555, // Water rune.
        557, // Earth rune.
        554, // Fire rune.
        555, // Water rune.
        554, // Fire rune.
        555, // Water rune.
    };

    public static boolean hasEnoughRunes(final Player player, final Set<? extends Item> runes, boolean sendMessage) {
        return hasEnoughRunes(player, runes.toArray(new Item[0]), sendMessage);
    }

    public static boolean hasEnoughRunes(Player player, Item[] runes, boolean sendMessage) {
        Item runeToCheck = null;
        for (Item rune : runes) {
            if (checkForStaffs(player, rune.getId()) != -1) {
                int comboRuneId = checkForComboRune(player, rune.getId());
                if (comboRuneId != -1) {
                    runeToCheck = new Item(comboRuneId, rune.getAmount());
                } else {
                    runeToCheck = rune;
                }
                if (!player.getInventory().hasItemAmount(runeToCheck.getId(), runeToCheck.getAmount())) {
                    if (sendMessage) {
                        String runeName = runeToCheck.getDefinition().getName();
                        player.getFrames().sendMessage("You do not have enough " + runeName + "s to cast this spell.");
                    }
                    return false;
                }
            }
        }
        return true;
    }

    protected static int checkForStaffs(Player p, int rune) {
        for (int i = 0; i < RUNE_REPLACER.length; i++) {
            if (p.getEquipment().getItemInSlot(3) == RUNE_REPLACER[i]) {
                if (rune == REPLACABLE_RUNES[i]) {
                    rune = -1;
                    break;
                }
            }
        }
        return rune;
    }

    public static int checkForComboRune(Player player, int rune) {
        for (int i = 0; i < COMBO_RUNES.length; i++) {
            if (player.getInventory().hasItem(COMBO_RUNES[i])) {
                for (Integer runes : COMBO_REPLACERS[i]) {
                    if (rune == runes.intValue()) {
                        return COMBO_RUNES[i];
                    }
                }
            }
        }
        return -1;
    }

    public static boolean deleteRunes(final Player player, final Set<? extends Item> runes) {
        return deleteRunes(player, runes.toArray(new Item[0]));
    }

    public static boolean deleteRunes(Player player, Item[] runes) {
        for (Item rune : runes) {
            Item runeToCheck = new Item(rune.getId(), rune.getAmount());
            if (checkForStaffs(player, rune.getId()) != -1) {
                int comboRuneId = checkForComboRune(player, runeToCheck.getId());
                if (comboRuneId != -1) {
                    runeToCheck = new Item(comboRuneId, runeToCheck.getAmount());
                }
                if (!player.getInventory().deleteItem(runeToCheck)) {
                    return false;
                }
            }
        }
        player.getInventory().refresh();
        return true;
    }
}
