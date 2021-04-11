package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.content.combat.data.MagicData;

public class StaffInterface extends StaffConfigs {

    /*
     * private static final int[] modernStaffs = { 1381, 1383, 1385, 1387, 1393,
     * 1395, 1397, 1399, 1401, 1403, 1405, 1407, 2415, 2416, 2417 };
     *
     * private static final int[] ancientStaffs = { 4675 };
     */

    public static void openAutoCastInterface(Player player) {
        int wep = player.getEquipment().getItemInSlot(3);
        if (player.getSettings().getMagicType() == 1) {
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 65 : 86, 319);
        } else if (player.getSettings().getMagicType() == 2) {
            if (wep == 13867) {
                player.getFrames().sendTab(player.getSettings().isInResizable() ? 65 : 86, 797);
                return;
            }
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 65 : 86, 388);
        }
    }

    public static void cancel(Player player, boolean cancelCast) {
        if (cancelCast) {
            player.getAttributes().set("autocast_spell", -1);
            resetSpellIcon(player);
            player.getFrames().sendInterfaceVisibility(90, 83, true);
            return;
        }
        if (player.getAttributes().isSet("autocastbutton")) {
            setAutoCastingSpell(player, player.getAttributes().getInt("autocastbutton"));
        } else {
            player.getAttributes().set("autocast_spell", -1);
            resetSpellIcon(player);
            Equipment.setWeapon(player, true);
            player.getFrames().sendInterfaceVisibility(90, 83, true);
        }
    }

    public static void resetSpellIcon(Player player) {
        if (player.getAttributes().isSet("autocastconfig")) {
            int config = player.getAttributes().getInt("autocastconfig");
            player.getFrames().sendInterfaceVisibility(90, config, false);
            player.getAttributes().remove("autocastconfig");
            player.getAttributes().remove("autocastbutton");
        }
    }

    public static void setAutoCastingSpell(Player player, int button) {
        int wep = player.getEquipment().getItemInSlot(3);
        boolean ancients = (player.getSettings().getMagicType() == 2);
        if (!ancients) {
            if (button == 174) {
                cancel(player, false);
                return;
            }
        } else if (ancients) {
            if (button == 20) {
                cancel(player, false);
                return;
            } else if (button == 16) {
                if (wep != 13867) {
                    cancel(player, false);
                    return;
                }
            }
        }
        resetSpellIcon(player);
        int config = -1;
        if (ancients) {
            switch (button) {
                case 40:// ice barrage
                    config = 43;
                    break;
                case 82:// blood barrage
                    config = 41;
                    break;
                case 184:// shadow barrage
                    config = 39;
                    break;
                case 132:// smoke barrage
                    config = 37;
                    break;
                case 18:// ice blitz
                    config = 35;
                    break;
                case 62:// blood blitz
                    config = 33;
                    break;
                case 158:// shadow blitz
                    config = 31;
                    break;
                case 106:// smoke blitz
                    config = 29;
                    break;
                case 29:// ice burst
                    config = 27;
                    break;
                case 71:// blood burst
                    config = 25;
                    break;
                case 171:// shadow burst
                    config = 23;
                    break;
                case 119:// smoke burst
                    config = 21;
                    break;
                case 7:// ice rush
                    config = 19;
                    break;
                case 51:// blood rush
                    config = 17;
                    break;
                case 145:// shadow rush
                    config = 15;
                    break;
                case 93:// smoke rush
                    config = 13;
                    break;
            }
        } else if (!ancients) {// Modern
            config = MODERN_CONFIGS[button];
        }
        if (config != -1) {
            player.getAttributes().set("autocastbutton", button);
            player.getAttributes().set("autocastconfig", config);
            player.getAttributes().set("autocast_spell", MagicData.getSpellForAutoCastButton(player, button));
            player.getFrames().sendInterfaceVisibility(90, 83, false);
            player.getFrames().sendInterfaceVisibility(90, config, true);
            player.getFrames().sendVarp(43, 3);
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 65 : 86, 90);
            player.getFrames().modifyText(player.getEquipment().getSlot(3).getDefinition().getName(), 90, 0);
        }
    }
}
