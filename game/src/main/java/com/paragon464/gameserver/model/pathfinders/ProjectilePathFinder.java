package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;

/**
 * @author Lazaro <lazaro@ziotic.com>
 */
public class ProjectilePathFinder {

    public static boolean hasLineOfSight(Region region, Position start, Position end, boolean debug) {
        Position currentTile = start;
        Directions.NormalDirection localDirection = null;
        Directions.NormalDirection localDirectionInverse = null;
        while (currentTile != end) {
            Directions.NormalDirection globalDirection = Directions.directionFor(currentTile, end);
            if (globalDirection == null) {
                return true;
            }
            Position nextTile = currentTile.transform(Directions.DIRECTION_DELTA_X[globalDirection.intValue()],
                Directions.DIRECTION_DELTA_Y[globalDirection.intValue()], 0);
            localDirection = Directions.directionFor(currentTile, nextTile);
            localDirectionInverse = Directions.directionFor(nextTile, currentTile);
            GameObject currentObject = World.getWallObject(currentTile);
            GameObject nextObject = World.getWallObject(nextTile);
            if (currentObject != null) {
                if (nextObject != null) {
                    if (!TileControl.canMove(currentTile, region, localDirection, 1, false)
                        || !TileControl.canMove(nextTile, region, localDirectionInverse, 1, false))
                        break;
                } else {
                    if (!TileControl.canMove(currentTile, region, localDirection, 1, false)
                        || !TileControl.canMove(nextTile, region, localDirectionInverse, 1, false))
                        break;
                }
            } else if (nextObject != null) {
                if (!TileControl.canMove(currentTile, region, localDirection, 1, false)
                    || !TileControl.canMove(nextTile, region, localDirectionInverse, 1, false))
                    break;
            }
            if (TileControl.canMove(currentTile, region, localDirection, 1, false) && TileControl.canMove(currentTile, region, localDirectionInverse, 1, false)) {
                currentTile = nextTile;
            } else {
                boolean solid = (World.getMask(nextTile.getX(), nextTile.getY(), nextTile.getZ()) & 0x20000) != 0;
                boolean solid2 = (World.getMask(currentTile.getX(), currentTile.getY(), currentTile.getZ())
                    & 0x20000) != 0;
                if (!solid && !solid2) {
                    currentTile = nextTile;
                } else
                    break;
            }
        }
        return currentTile == end;
    }

    public static boolean hasLineOfSight(Mob mob, Mob target) {
        // CombatType combatType = mob.getCombatState().getCombatType();
        Region region = mob.getLastRegion();
        int z = mob.getCoverage().center().getZ();
        Position start_loc = mob.getCoverage().center();
        Position end_loc = target.getCoverage().center();
        Position currentTile = start_loc;
        Directions.NormalDirection localDirection = null;
        Directions.NormalDirection localDirectionInverse = null;
        boolean projectileCheck = true;
        while (currentTile != end_loc) {
            Directions.NormalDirection globalDirection = Directions.directionFor(currentTile, end_loc);
            if (globalDirection == null) {
                return true;
            }
            Position nextTile = currentTile.transform(Directions.DIRECTION_DELTA_X[globalDirection.intValue()],
                Directions.DIRECTION_DELTA_Y[globalDirection.intValue()], 0);
            localDirection = Directions.directionFor(currentTile, nextTile);
            localDirectionInverse = Directions.directionFor(nextTile, currentTile);
            GameObject currentObject = World.getWallObject(currentTile);
            GameObject nextObject = World.getWallObject(nextTile);
            if (projectileCheck) {
                if (currentObject != null && !currentObject.isRangable()) {
                    if (nextObject != null && !nextObject.isRangable()) {
                        if (!TileControl.canMove(currentTile, region, localDirection, 1, false)
                            || !TileControl.canMove(nextTile, region, localDirectionInverse, 1, false))
                            break;
                    } else {
                        if (!TileControl.canMove(currentTile, region, localDirection, 1, true)
                            || !TileControl.canMove(nextTile, region, localDirectionInverse, 1, false))
                            break;
                    }
                } else if (nextObject != null) {
                    if (!TileControl.canMove(currentTile, region, localDirection, 1, false)
                        || !TileControl.canMove(nextTile, region, localDirectionInverse, 1, false))
                        break;
                }
                if (TileControl.canMove(currentTile, region, localDirection, 1, false)
                    && TileControl.canMove(currentTile, region, localDirectionInverse, 1, false)) {
                    currentTile = nextTile;
                } else {
                    boolean solid = (World.getClipedOnlyMask(nextTile.getX(), nextTile.getY(), nextTile.getZ(), region)
                        & 0x20000) != 0;
                    boolean solid2 = (World.getClipedOnlyMask(currentTile.getX(), currentTile.getY(),
                        currentTile.getZ(), region) & 0x20000) != 0;
                    if (!solid && !solid2) {
                        currentTile = nextTile;
                    } else {
                        return solid && solid2;
                    }
                }
            } else {
                if (currentObject != null || nextObject != null) {
                    if (!TileControl.canMove(currentTile, region, localDirection, 1, false)
                        || !TileControl.canMove(nextTile, region, localDirectionInverse, 1, false))
                        break;
                }
                if (TileControl.canMove(currentTile, region, localDirection, 1, false)
                    && TileControl.canMove(currentTile, region, localDirectionInverse, 1, false)) {
                    currentTile = nextTile;
                } else {
                    boolean solid = (World.getClipedOnlyMask(nextTile.getX(), nextTile.getY(), nextTile.getZ(), region)
                        & 0x20000) != 0;
                    boolean solid2 = (World.getClipedOnlyMask(currentTile.getX(), currentTile.getY(),
                        currentTile.getZ(), region) & 0x20000) != 0;
                    if (!solid && !solid2) {
                        currentTile = nextTile;
                    } else {
                        break;
                    }
                }
            }
        }
        return currentTile == end_loc;
    }
}
