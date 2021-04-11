package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.util.NumberUtils;

/**
 * @author Killamess
 */
public class DumbPathFinder {

    public static boolean blockedNorthEast(Position loc, Mob mob) {
        return !PrimitivePathFinder.canMove(mob, loc, Directions.NormalDirection.NORTH_EAST, mob.getSize(), false);
    }

    public static boolean blockedNorthWest(Position loc, Mob mob) {
        return !PrimitivePathFinder.canMove(mob, loc, Directions.NormalDirection.NORTH_WEST, mob.getSize(), false);
    }

    public static boolean blockedSouthEast(Position loc, Mob mob) {
        return !PrimitivePathFinder.canMove(mob, loc, Directions.NormalDirection.SOUTH_EAST, mob.getSize(), false);
    }

    public static boolean blockedSouthWest(Position loc, Mob mob) {
        return !PrimitivePathFinder.canMove(mob, loc, Directions.NormalDirection.SOUTH_WEST, mob.getSize(), false);
    }

    public static void generateMovement(Mob mob) {
        Position loc = mob.getPosition();
        int dir = -1;
        if (!blockedNorth(loc, mob)) {
            dir = 0;
        } else if (!blockedEast(loc, mob)) {
            dir = 4;
        } else if (!blockedSouth(loc, mob)) {
            dir = 8;
        } else if (!blockedWest(loc, mob)) {
            dir = 12;
        }
        int random = NumberUtils.random(3);
        boolean found = false;
        if (random == 0) {
            if (!blockedNorth(loc, mob)) {
                mob.executeEntityPath(loc.getX(), loc.getY() + 1);
                found = true;
            }
        } else if (random == 1) {
            if (!blockedEast(loc, mob)) {
                mob.executeEntityPath(loc.getX() + 1, loc.getY());
                found = true;
            }
        } else if (random == 2) {
            if (!blockedSouth(loc, mob)) {
                mob.executeEntityPath(loc.getX(), loc.getY() - 1);
                found = true;
            }
        } else if (random == 3) {
            if (!blockedWest(loc, mob)) {
                mob.executeEntityPath(loc.getX() - 1, loc.getY());
                found = true;
            }
        }
        if (!found) {
            if (dir == 0) {
                mob.executeEntityPath(loc.getX(), loc.getY() + 1);
            } else if (dir == 4) {
                mob.executeEntityPath(loc.getX() + 1, loc.getY());
            } else if (dir == 8) {
                mob.executeEntityPath(loc.getX(), loc.getY() - 1);
            } else if (dir == 12) {
                mob.executeEntityPath(loc.getX() - 1, loc.getY());
            }
        }
    }

    public static boolean blockedNorth(Position loc, Mob mob) {
        return !PrimitivePathFinder.canMove(mob, loc, Directions.NormalDirection.NORTH, mob.getSize(), false);
    }

    public static boolean blockedEast(Position loc, Mob mob) {
        return !PrimitivePathFinder.canMove(mob, loc, Directions.NormalDirection.EAST, mob.getSize(), false);
    }

    public static boolean blockedSouth(Position loc, Mob mob) {
        return !PrimitivePathFinder.canMove(mob, loc, Directions.NormalDirection.SOUTH, mob.getSize(), false);
    }

    public static boolean blockedWest(Position loc, Mob mob) {
        return !PrimitivePathFinder.canMove(mob, loc, Directions.NormalDirection.WEST, mob.getSize(), false);
    }
}
