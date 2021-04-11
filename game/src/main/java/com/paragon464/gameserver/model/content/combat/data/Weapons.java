package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.AttackVars;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.item.ItemDefinition.WeaponDefinition;

public class Weapons {

    public static int distance(Mob mob) {
        if (mob.isNPC()) {
            return 1;
        }
        return 1;
    }

    public static int speed(Mob mob, int weapon) {
        if (mob.isNPC()) {
            if (((NPC) mob).getCombatDefinition() == null) {
                return 6;
            }
            return ((NPC) mob).getCombatDefinition().getAttackSpeed();
        }
        if (weapon == -1) {
            return 4;
        }
        Player player = (Player) mob;
        AttackVars attack = player.getAttackVars();
        ItemDefinition def = ItemDefinition.forId(weapon);
        WeaponDefinition wepDef = def.weaponDefinition;
        if (wepDef == null)
            return 10;
        if (attack.getSkill().name().equalsIgnoreCase("range")) {
            switch (attack.getStyle().name()) {
                case "RANGE_ACCURATE":
                    return wepDef.getSpeedAccurate();
                case "RANGE_RAPID":
                    return wepDef.getSpeedAggressive();
                case "RANGE_DEFENSIVE":
                    return wepDef.getSpeedDefensive();
            }
        }
        switch (attack.getSkill().name()) {
            case "ACCURATE":
                return wepDef.getSpeedAccurate();
            case "AGGRESSIVE":
                return wepDef.getSpeedAggressive();
            case "DEFENSIVE":
                return wepDef.getSpeedDefensive();
            case "CONTROLLED":
                return wepDef.getSpeedControlled();
        }
        return 10;
    }
}
