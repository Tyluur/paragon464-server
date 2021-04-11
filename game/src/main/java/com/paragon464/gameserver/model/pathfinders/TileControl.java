package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;

/**
 * @author Killamess
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class TileControl {

    private static TileControl singleton = null;

    public static int calculateDistance(Mob mobA, Mob mobB) {
        Position[] pointsA = getHoveringTiles(mobA);
        Position[] pointsB = getHoveringTiles(mobB);
        int lowestCount = 16;
        int distance = 16;
        for (Position pointA : pointsA) {
            for (Position pointB : pointsB) {
                if (pointA.equals(pointB)) {
                    return 0;
                }
                distance = calculateDistance(pointA, pointB);
                if (distance < lowestCount) {
                    lowestCount = distance;
                }
            }
        }

        return lowestCount;
    }

    public static Position[] getHoveringTiles(Mob mob) {
        return getHoveringTiles(mob, mob.getPosition());
    }

    public static int calculateDistance(Position pointA, Position pointB) {
        int offsetX = Math.abs(pointA.getX() - pointB.getX());
        int offsetY = Math.abs(pointA.getY() - pointB.getY());
        return offsetX > offsetY ? offsetX : offsetY;
    }

    public static Position[] getHoveringTiles(Mob mob, Position position) {
        if (mob.hoverTiles == null) {
            mob.hoverTiles = new Position[1];
        }
        if (mob.hoverTiles.length == 1) {
            mob.hoverTiles[0] = position;
        } else {
            int offset = 0;
            for (int x = 0; x < mob.getSize(); x++) {
                for (int y = 0; y < mob.getSize(); y++) {
                    mob.hoverTiles[(offset++)] = new Position(position.getX() + x, position.getY() + y, position.getZ());
                }
            }
        }
        return mob.hoverTiles;
    }

    public static boolean isWithinRadius(Mob mob, Mob other, int distance) {
        Position myClosestTile = closestTileOf(mob.getPosition(), other.getPosition(), mob.getSize(),
            mob.getSize());
        Position theirClosestTile = closestTileOf(other.getPosition(), mob.getPosition(), other.getSize(),
            other.getSize());
        return myClosestTile.getDistanceFrom(theirClosestTile) <= distance;
    }

    public static Position closestTileOf(final Position from, final Position to, int width, int height) {
        if (width < 2 && height < 2) {
            return from;
        }
        Position position = null;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position loc = new Position(from.getX() + x, from.getY() + y, from.getZ());
                if (position == null || loc.getDistanceFrom(to) < position.getDistanceFrom(to)) {
                    position = loc;
                }
            }
        }
        return position;
    }

    public static boolean locationOccupied(final Mob mob, final Mob other) {
        int firstSize = mob.getSize();
        int secondSize = other.getSize();
        int x = mob.getPosition().getX();
        int y = mob.getPosition().getY();
        int vx = other.getPosition().getX();
        int vy = other.getPosition().getY();
        for (int i = x; i < x + firstSize; i++) {
            for (int j = y; j < y + firstSize; j++) {
                if (i >= vx && i < secondSize + vx && j >= vy && j < secondSize + vy) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canMove(Mob mob, Directions.NormalDirection dir, int size, boolean npcCheck) {
        return PrimitivePathFinder.canMove(mob, mob.getPosition(), dir, size, npcCheck);
    }

    public static boolean canMove(final Position position, Region region, Directions.NormalDirection dir, int size, boolean checkType) {
        if (dir == null) {
            return true;
        }
        switch (dir) {
            case WEST:
                for (int k = position.getY(); k < position.getY() + size; k++) {
                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(getX() - 1, k,
                 getZ())) return false;*/

                    if ((World.getMask(position.getX() - 1, k, position.getZ()) & 0x1280108) != 0)
                        return false;
                }
                break;
            case EAST:
                for (int k = position.getY(); k < position.getY() + size; k++) {
                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(getX() + size, k,
                 getZ())) return false;*/

                    if ((World.getMask(position.getX() + size, k, position.getZ()) & 0x1280180) != 0)
                        return false;
                }
                break;
            case SOUTH:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(i, getY() - 1,
                 getZ())) return false;*/

                    if ((World.getMask(i, position.getY() - 1, position.getZ()) & 0x1280102) != 0)
                        return false;
                }
                break;
            case NORTH:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(i, getY() + size,
                 getZ())) return false;*/

                    if ((World.getMask(i, position.getY() + size, position.getZ()) & 0x1280120) != 0)
                        return false;
                }
                break;
            case SOUTH_WEST:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    int s = World.getMask(i, position.getY() - 1, position.getZ());
                    int w = World.getMask(i - 1, position.getY(), position.getZ());
                    int sw = World.getMask(i - 1, position.getY() - 1, position.getZ());

                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(i - 1, getY() - 1,
                 getZ())) return false;*/

                    if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
                        return false;
                }
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    int s = World.getMask(position.getX(), k - 1, position.getZ());
                    int w = World.getMask(position.getX() - 1, k, position.getZ());
                    int sw = World.getMask(position.getX() - 1, k - 1, position.getZ());

                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(getX() - 1, k - 1,
                 getZ())) return false;*/

                    if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
                        return false;
                }
                break;
            case SOUTH_EAST:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    int s = World.getMask(i, position.getY() - 1, position.getZ());
                    int e = World.getMask(i + 1, position.getY(), position.getZ());
                    int se = World.getMask(i + 1, position.getY() - 1, position.getZ());

                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(i + 1, getY() - 1,
                 getZ())) return false;*/

                    if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
                        return false;
                }
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    int s = World.getMask(position.getX() + size - 1, k - 1, position.getZ());
                    int e = World.getMask(position.getX() + size, k, position.getZ());
                    int se = World.getMask(position.getX() + size, k - 1, position.getZ());

                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(getX() + 1, k - 1,
                 getZ())) return false;*/

                    if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
                        return false;
                }
                break;
            case NORTH_WEST:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    int n = World.getMask(i, position.getY() + size, position.getZ());
                    int w = World.getMask(i - 1, position.getY() + size - 1, position.getZ());
                    int nw = World.getMask(i - 1, position.getY() + size, position.getZ());

                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(i - 1, getY() + size,
                 getZ())) return false;*/

                    if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
                        return false;
                }
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    int n = World.getMask(position.getX(), position.getY(), position.getZ());
                    int w = World.getMask(position.getX() - 1, position.getY(), position.getZ());
                    int nw = World.getMask(position.getX() - 1, position.getY() + 1, position.getZ());

                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(getX() - 1, getY() + 1,
                 getZ())) return false;*/

                    if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
                        return false;
                }
                break;
            case NORTH_EAST:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    int n = World.getMask(i, position.getY() + size, position.getZ());
                    int e = World.getMask(i + 1, position.getY() + size - 1, position.getZ());
                    int ne = World.getMask(i + 1, position.getY() + size, position.getZ());
                  /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(i + 1, getY() + size,
                 getZ())) return false;*/

                    if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
                        return false;
                }
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    int n = World.getMask(position.getX() + size - 1, k + 1, position.getZ());
                    int e = World.getMask(position.getX() + size, k, position.getZ());
                    int ne = World.getMask(position.getX() + size, k + 1, position.getZ());

                 /*if (checkType &&
                 TileControl.getSingleton().locationOccupied(getX() + size, k + 1,
                 getZ())) return false;*/

                    if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
                        return false;
                }
                break;
        }
        return true;
    }

    public static boolean isDiagonal(Mob source, Mob target) {
        Position l = source.getPosition();
        Position l2 = target.getPosition();
        if (l.getSouthEast().equals(l2)) {
            return true;
        }
        if (l.getSouthWest().equals(l2)) {
            return true;
        }
        if (l.getNorthEast().equals(l2)) {
            return true;
        }
        return l.getNorthWest().equals(l2);
    }

    /**
     * @param position         The base position.
     * @param comparedPosition The position to check.
     * @return {@code true} if {@code comparedPosition} is diagonal from {@code position}.
     */
    public static boolean isDiagonal(final Position position, final Position comparedPosition) {
        return position.getSouthEast().equals(comparedPosition) || position.getSouthWest().equals(comparedPosition)
            || position.getNorthEast().equals(comparedPosition) || position.getNorthWest().equals(comparedPosition);
    }

    public static NPC npcInPath(final Position position, Mob mob, Directions.NormalDirection dir, int size) {
        if (dir == null) {
            return null;
        }
        switch (dir) {
            case WEST:
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, position.getX() - 1, k, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                break;
            case EAST:
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, position.getX() + size, k, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                break;
            case SOUTH:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, i, position.getY() - 1, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                break;
            case NORTH:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, i, position.getY() + size, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                break;
            case SOUTH_WEST:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, i - 1, position.getY() - 1, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, position.getX() - 1, k - 1, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                break;
            case SOUTH_EAST:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, i + 1, position.getY() - 1, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, position.getX() + 1, k - 1, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                break;
            case NORTH_WEST:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, i - 1, position.getY() + size, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, position.getX() - 1, position.getY() + 1, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                break;
            case NORTH_EAST:
                for (int i = position.getX(); i < position.getX() + size; i++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, i + 1, position.getY() + size, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                for (int k = position.getY(); k < position.getY() + size; k++) {
                    NPC npc = TileControl.getSingleton().npcOnTile(mob, position.getX() + size, k + 1, position.getZ());
                    if (npc != null) {
                        return npc;
                    }
                }
                break;
        }
        return null;
    }

    public NPC npcOnTile(Mob mob, int x, int y, int z) {
        for (Region region : mob.getMapRegions()) {
            for (int j = region.getNPCS().size() - 1; j >= 0; j--) {
                NPC npc = region.getNPCS().get(j);
                Position[] npcPositions = getOccupiedLocations(npc);
                if (npcPositions != null) {
                    for (Position locs : npcPositions) {
                        if (locs.getX() == x && locs.getY() == y && locs.getZ() == z) {
                            return npc;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static TileControl getSingleton() {
        if (singleton == null) {
            singleton = new TileControl();
        }
        return singleton;
    }

    public Position[] getOccupiedLocations(Mob mob) {
        return mob.hoverTiles;
    }

    public void setOccupiedLocation(Mob mob, Position[] positions) {
        if ((mob == null) || (positions == null))
            return;
        mob.hoverTiles = positions;
    }

    public boolean locationOccupied(int x, int y, int z) {
        Position pos = new Position(x, y, z);
        for (NPC npc : World.getSurroundingNPCS(pos)) {
            if (npc == null) {
                continue;
            }
            Position[] npcPositions = getOccupiedLocations(npc);
            if (npcPositions != null) {
                for (Position locs : npcPositions) {
                    if (locs != null) {
                        if (locs.getX() == x && locs.getY() == y && locs.getZ() == z) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*public static boolean standingOn(Mob mob, Mob other) {
        int firstSize = mob.getSize();
        int secondSize = other.getSize();
        int x = mob.getPosition().getX();
        int y = mob.getPosition().getY();
        int vx = other.getPosition().getX();
        int vy = other.getPosition().getY();
        for (int i = x; i < x + firstSize; i++) {
            for (int j = y; j < y + firstSize; j++) {
                if (i >= vx && i < secondSize + vx && j >= vy && j < secondSize + vy) {
                    return true;
                }
            }
        }
        return false;
    }*/

    public boolean locationOccupied(Mob mob, int x, int y, int z) {
        for (NPC npc : World.getSurroundingNPCS(mob.getPosition())) {
            if (npc == null) {
                continue;
            }
            Position[] npcPositions = getOccupiedLocations(npc);
            if (npcPositions != null) {
                for (Position locs : npcPositions) {
                    if (locs != null) {
                        if (locs.getX() == x && locs.getY() == y && locs.getZ() == z) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean locationOccupied(Position[] positions, Mob mob) {
        if (positions == null || mob == null) {
            return true;
        }

        for (Region region : mob.getMapRegions()) {
            for (int j = region.getNPCS().size() - 1; j >= 0; j--) {
                NPC npc = region.getNPCS().get(j);
                if ((mob.isNPC()) && ((npc == null) || (npc == mob))) {
                    continue;
                }
                Position[] npcPositions = getOccupiedLocations(npc);
                if (npcPositions != null) {
                    for (Position loc : positions) {
                        for (Position loc2 : npcPositions) {
                            if (loc.getX() == loc2.getX() && loc.getY() == loc2.getY() && loc.getZ() == loc2.getZ()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
