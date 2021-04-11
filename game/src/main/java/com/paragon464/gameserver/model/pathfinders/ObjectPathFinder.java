package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.cache.definitions.CachedObjectDefinition;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.gameobjects.GameObject;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class ObjectPathFinder {

    /**
     * Finds a path to an object.
     *
     * @param player
     * @param obj
     * @return
     */
    public static PathState executePath(Player player, GameObject obj) {
        int walkToData = 0;
        int type = -2;
        int direction;
        if ((obj.getType() == 10 || obj.getType() == 11 || obj.getType() == 22)) {
            type = -1;
            int rotation = obj.getRotation();
            CachedObjectDefinition def = obj.getDefinition();
            walkToData = def.walkToFlag;
            if (obj.getRotation() != 0)
                walkToData = (walkToData << rotation & 0xf) + (walkToData >> 4 - rotation);
            direction = 0;
        } else {
            type = obj.getType();
            direction = obj.getRotation();
        }
        final int finalX = obj.getPosition().getX();
        final int finalY = obj.getPosition().getY();
        return player.executeVariablePath(obj, type, direction, walkToData, finalX, finalY);
    }
}
