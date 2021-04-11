package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.region.Position;

/**
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public interface PathFinder {

    int SOUTH_FLAG = 0x1, WEST_FLAG = 0x2, NORTH_FLAG = 0x4, EAST_FLAG = 0x8;

    int SOUTH_WEST_FLAG = SOUTH_FLAG | WEST_FLAG;
    int NORTH_WEST_FLAG = NORTH_FLAG | WEST_FLAG;
    int SOUTH_EAST_FLAG = SOUTH_FLAG | EAST_FLAG;
    int NORTH_EAST_FLAG = NORTH_FLAG | EAST_FLAG;

    int SOLID_FLAG = 0x20000;
    int UNKNOWN_FLAG = 0x40000000;

    PathState findPath(Mob mob, Object target, Position base, int srcX, int srcY, int dstX, int dstY,
                       int radius, boolean running, boolean ignoreLastStep, boolean moveNear);
}
