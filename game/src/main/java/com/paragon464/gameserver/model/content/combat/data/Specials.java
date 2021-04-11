package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.pathfinders.Directions;

public class Specials {

    public static void spearPush(final Player player, final Mob victim) {
        Directions.NormalDirection dir = Directions.directionFor(player.getPosition(), victim.getPosition());
        if (dir == null) {
            return;
        }
        boolean pushNorth = dir.equals(Directions.NormalDirection.NORTH);
        boolean pushSouth = dir.equals(Directions.NormalDirection.SOUTH);
        boolean pushEast = dir.equals(Directions.NormalDirection.EAST);
        boolean pushWest = dir.equals(Directions.NormalDirection.WEST);
        int victimX = victim.getPosition().getX(), victimY = victim.getPosition().getY();
        if (pushNorth) {
            victimY++;
        } else if (pushSouth) {
            victimY--;
        } else if (pushEast) {
            victimX++;
        } else if (pushWest) {
            victimX--;
        }
        victim.getCombatState().end(1);
        victim.executeEntityPath(victimX, victimY);
    }

    public static double getRequiredAmount(int weapon) {
        ItemDefinition def = ItemDefinition.forId(weapon);
        if (def != null) {
            ItemDefinition.WeaponDefinition weapon_def = def.weaponDefinition;
            if (weapon_def != null) {
                return weapon_def.getSpecialEnergy();
            }
        }
        return -1;
    }
}
