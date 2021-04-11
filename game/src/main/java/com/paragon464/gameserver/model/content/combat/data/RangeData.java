package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.item.ItemDefinition.RangedDefinition;

public class RangeData {

    public static boolean containsAmmo(Mob mob, int wep, int ammo) {
        if (mob.isNPC()) {
            return true;
        }
        Player player = (Player) mob;
        String wepName = ItemDefinition.forId(wep).getName().toLowerCase();
        final String type = wepName.endsWith("crossbow") ? "bolts"
            : wepName.equals("hand cannon") ? "cannon shots" : "arrows";
        if ((!wepName.contains("crystal") && wepName.endsWith("bow")) || wepName.endsWith("shortbow")
            || wepName.endsWith("crossbow") || wepName.equals("hand cannon")) {
            if (ammo <= 0) {
                player.getFrames().sendMessage("You have no " + type + " left in your quiver.");
                return false;
            }
        } else if (wepName.endsWith("knife") || wepName.endsWith("dart") || wepName.contains("javelin")) {
            return wep > 0;
        }
        return true;
    }

    public static boolean containsCorrectArrows(Player player, int weapon, int arrows) {
        boolean correctAmmo = false;
        ItemDefinition def = ItemDefinition.forId(weapon);
        if (def != null) {
            RangedDefinition rangedDef = def.rangedDefinition;
            if ((rangedDef == null || (rangedDef != null && rangedDef.getAmmoAllowed().size() <= 0))) {
                correctAmmo = true;
            } else {
                for (int allowed : rangedDef.getAmmoAllowed()) {
                    if (allowed == arrows) {
                        correctAmmo = true;
                    }
                }
            }
        }
        return correctAmmo;
    }

    public static boolean throwingAmmo(Player player, int item) {
        ItemDefinition def = ItemDefinition.forId(item);
        return def.rangedDefinition != null && !def.rangedDefinition.usesAmmo();
    }

    public static int getDrawBack(Player player, int weapon, int ammo) {
        ItemDefinition def = ItemDefinition.forId(weapon);
        ItemDefinition ammo_def = ItemDefinition.forId(ammo);
        if (def == null || def.getName() == null) {
            return -1;
        }
        boolean throwable = false;
        RangedDefinition rangeDef = def.rangedDefinition;
        if (rangeDef != null) {
            throwable = !rangeDef.usesAmmo();
        }
        if (throwable) {
            if (rangeDef != null) {
                return rangeDef.getDrawback();
            }
        } else {
            if (ammo_def != null) {
                rangeDef = ammo_def.rangedDefinition;
                if (rangeDef != null) {
                    return rangeDef.getDrawback();
                }
            }
        }
        return -1;
    }

    public static int getEndGfx(int bow, int ammo, boolean special, boolean last_hit) {
        if (special) {
            if (last_hit) {
                switch (bow) {
                    case 11235:// Dark bow
                        return 1100;
                }
                switch (ammo) {
                    case 9244:// Dragon.
                        return 756;
                    case 9243:// Diamond.
                        return 758;
                    case 9236: // Opal.
                        return 749;
                    case 9237: // Jade.
                        return 756;
                    case 9238: // Pearl.
                        return 750;
                    case 9239: // Topaz.
                        return 757;
                    case 9240: // Sapphire.
                        return 751;
                    case 9241: // Emerald.
                        return 752;
                    case 9242: // Ruby
                        return 754;
                    case 9245: // Onyx.
                        return 753;
                }
            }
        } else {
            switch (bow) {
                case 10034:// Chinchompa
                    return 157;
            }
        }
        return -1;
    }
}
