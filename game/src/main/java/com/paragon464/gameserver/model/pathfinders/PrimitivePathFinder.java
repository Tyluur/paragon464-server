package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;

import java.awt.*;

public class PrimitivePathFinder implements PathFinder {

    public static Point getNextStep(Mob mob, Position source, int toX, int toY, int height, int xLength,
                                    int yLength) {
        int baseX = source.getLocalX(), baseY = source.getLocalY();
        int moveX = 0;
        int moveY = 0;
        if (baseX - toX > 0) {
            moveX--;
        } else if (baseX - toX < 0) {
            moveX++;
        }
        if (baseY - toY > 0) {
            moveY--;
        } else if (baseY - toY < 0) {
            moveY++;
        }
        if (canMove(mob, source, baseX, baseY, baseX + moveX, baseY + moveY, height, xLength, yLength)) {
            return new Point(baseX + moveX, baseY + moveY);
        } else if (moveX != 0 && canMove(mob, source, baseX, baseY, baseX + moveX, baseY, height, xLength, yLength)) {
            return new Point(baseX + moveX, baseY);
        } else if (moveY != 0 && canMove(mob, source, baseX, baseY, baseX, baseY + moveY, height, xLength, yLength)) {
            return new Point(baseX, baseY + moveY);
        }
        return null;
    }

    public static boolean canMove(Mob mob, Position base, int startX, int startY, int endX, int endY, int height,
                                  int xLength, int yLength) {
        Position RSTile = new Position((base.getZoneX() - 6) << 3, (base.getZoneY() - 6) << 3, base.getZ());
        int diffX = endX - startX;
        int diffY = endY - startY;
        int max = Math.max(Math.abs(diffX), Math.abs(diffY));
        Region region = mob.getLastRegion();
        for (int ii = 0; ii < max; ii++) {
            int currentX = RSTile.getX() + (endX - diffX);
            int currentY = RSTile.getY() + (endY - diffY);
            for (int i = 0; i < xLength; i++) {
                for (int i2 = 0; i2 < yLength; i2++) {
                    if (diffX < 0 && diffY < 0) {
                        if ((World.getMask(currentX + i - 1, currentY + i2 - 1, height) & 0x128010e) != 0
                            || (World.getMask(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0
                            || (World.getMask(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY > 0) {
                        if ((World.getMask(currentX + i + 1, currentY + i2 + 1, height) & 0x12801e0) != 0
                            || (World.getMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
                            || (World.getMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
                            return false;
                        }
                    } else if (diffX < 0 && diffY > 0) {
                        if ((World.getMask(currentX + i - 1, currentY + i2 + 1, height) & 0x1280138) != 0
                            || (World.getMask(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0
                            || (World.getMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY < 0) {
                        if ((World.getMask(currentX + i + 1, currentY + i2 - 1, height) & 0x1280183) != 0
                            || (World.getMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
                            || (World.getMask(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY == 0) {
                        if ((World.getMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0) {
                            return false;
                        }
                    } else if (diffX < 0 && diffY == 0) {
                        if ((World.getMask(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0) {
                            return false;
                        }
                    } else if (diffX == 0 && diffY > 0) {
                        if ((World.getMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
                            return false;
                        }
                    } else if (diffX == 0 && diffY < 0) {
                        if ((World.getMask(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
                            return false;
                        }
                    }
                }
            }
            if (diffX < 0) {
                diffX++;
            } else if (diffX > 0) {
                diffX--;
            }
            if (diffY < 0) {
                diffY++;
            } else if (diffY > 0) {
                diffY--;
            }
        }
        return true;
    }

    public static boolean canNextStep(Mob mob, Position source, int toX, int toY, int height, int xLength,
                                      int yLength) {
        int baseX = source.getLocalX(), baseY = source.getLocalY();
        int moveX = 0;
        int moveY = 0;
        if (baseX - toX > 0) {
            moveX--;
        } else if (baseX - toX < 0) {
            moveX++;
        }
        if (baseY - toY > 0) {
            moveY--;
        } else if (baseY - toY < 0) {
            moveY++;
        }
        if (canMove(mob, source, baseX, baseY, baseX + moveX, baseY + moveY, height, xLength, yLength)) {
            return true;
        } else if (moveX != 0 && canMove(mob, source, baseX, baseY, baseX + moveX, baseY, height, xLength, yLength)) {
            return true;
        } else return moveY != 0 && canMove(mob, source, baseX, baseY, baseX, baseY + moveY, height, xLength, yLength);
    }

    public static boolean canMove(Mob mob, Position source, Directions.NormalDirection dir) {
        return canMove(mob, source, dir, 1, false);
    }

    public static boolean canMove(Mob mob, Position source, Directions.NormalDirection dir, int size,
                                  boolean npcCheck) {
        return canMove(mob, source.getX(), source.getY(), dir, size, npcCheck ? 0x1 : 0);
    }

    public static boolean canMove(Mob mob, int x, int y, Directions.NormalDirection dir, int size, int checkType) {
        int z = mob.getPosition().getZ();
        Region region = mob.getLastRegion();
        boolean checkingNPCs = (checkType == 1);
        if (dir == null) {
            return true;
        }
        if (mob.isNPC()) {
            if (((NPC) mob).getId() == 750) {
                return true;
            }
        }
        switch (dir) {
            case WEST:
                for (int k = y; k < y + size; k++) {
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, x - 1, k, z))
                        return false;
                    if ((World.getMask(x - 1, k, z) & 0x1280108) != 0)
                        return false;
                }
                break;
            case EAST:
                for (int k = y; k < y + size; k++) {
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, x + size, k, z))
                        return false;
                    if ((World.getMask(x + size, k, z) & 0x1280180) != 0)
                        return false;
                }
                break;
            case SOUTH:
                for (int i = x; i < x + size; i++) {
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, i, y - 1, z))
                        return false;
                    if ((World.getMask(i, y - 1, z) & 0x1280102) != 0)
                        return false;
                }
                break;
            case NORTH:
                for (int i = x; i < x + size; i++) {
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, i, y + size, z))
                        return false;
                    if ((World.getMask(i, y + size, z) & 0x1280120) != 0)
                        return false;
                }
                break;
            case SOUTH_WEST:
                for (int i = x; i < x + size; i++) {
                    int s = World.getMask(i, y - 1, z);
                    int w = World.getMask(i - 1, y, z);
                    int sw = World.getMask(i - 1, y - 1, z);
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, i - 1, y - 1, z))
                        return false;
                    if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
                        return false;
                }
                for (int k = y; k < y + size; k++) {
                    int s = World.getMask(x, k - 1, z);
                    int w = World.getMask(x - 1, k, z);
                    int sw = World.getMask(x - 1, k - 1, z);
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, x - 1, k - 1, z))
                        return false;
                    if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
                        return false;
                }
                break;
            case SOUTH_EAST:
                for (int i = x; i < x + size; i++) {
                    int s = World.getMask(i, y - 1, z);
                    int e = World.getMask(i + 1, y, z);
                    int se = World.getMask(i + 1, y - 1, z);
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, i + 1, y - 1, z))
                        return false;
                    if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
                        return false;
                }
                for (int k = y; k < y + size; k++) {
                    int s = World.getMask(x + size - 1, k - 1, z);
                    int e = World.getMask(x + size, k, z);
                    int se = World.getMask(x + size, k - 1, z);
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, x + 1, k - 1, z))
                        return false;
                    if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
                        return false;
                }
                break;
            case NORTH_WEST:
                for (int i = x; i < x + size; i++) {
                    int n = World.getMask(i, y + size, z);
                    int w = World.getMask(i - 1, y + size - 1, z);
                    int nw = World.getMask(i - 1, y + size, z);
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, i - 1, y + size, z))
                        return false;
                    if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
                        return false;
                }
                for (int k = y; k < y + size; k++) {
                    int n = World.getMask(x, y, z);
                    int w = World.getMask(x - 1, y, z);
                    int nw = World.getMask(x - 1, y + 1, z);
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, x - 1, y + 1, z))
                        return false;
                    if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
                        return false;
                }
                break;
            case NORTH_EAST:
                for (int i = x; i < x + size; i++) {
                    int n = World.getMask(i, y + size, z);
                    int e = World.getMask(i + 1, y + size - 1, z);
                    int ne = World.getMask(i + 1, y + size, z);
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, i + 1, y + size, z))
                        return false;
                    if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
                        return false;
                }
                for (int k = y; k < y + size; k++) {
                    int n = World.getMask(x + size - 1, k + 1, z);
                    int e = World.getMask(x + size, k, z);
                    int ne = World.getMask(x + size, k + 1, z);
                    if (checkingNPCs && TileControl.getSingleton().locationOccupied(mob, x + size, k + 1, z))
                        return false;
                    if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
                        return false;
                }
                break;
        }
        return true;
    }

    public static boolean canMove(Mob mob, Position source, Directions.NormalDirection dir, boolean npcCheck) {
        return canMove(mob, source, dir, 1, npcCheck);
    }

    @Override
    public PathState findPath(Mob mob, Object target, Position base, int srcX, int srcY, int dstX, int dstY,
                              int radius, boolean running, boolean ignoreLastStep, boolean moveNear) {
        return findPath(mob, base, srcX, srcY, dstX, dstY, radius, running, ignoreLastStep, moveNear, false);
    }

    public PathState findPath(Mob mob, Position base, int srcX, int srcY, int dstX, int dstY, int radius,
                              boolean running, boolean ignoreLastStep, boolean moveNear, boolean nullOnFail) {
        if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || srcX >= 104 || srcY >= 104) {
            return null;
        }
        if (srcX == dstX && srcY == dstY) {
            return null;
        }
        Position position = new Position((base.getZoneX() - 6) << 3, (base.getZoneY() - 6) << 3, base.getZ());
        Position current = new Position(position.getX() + srcX, position.getY() + srcY, position.getZ());
        Position end = new Position(position.getX() + dstX, position.getY() + dstY, position.getZ());
        while (current != end) {
            Directions.NormalDirection nextDirection = Directions.directionFor(current, end);
            if (nextDirection != null && canMove(mob, current, nextDirection, mob.getSize(), false)) {
                current = current.transform(Directions.DIRECTION_DELTA_X[nextDirection.intValue()],
                    Directions.DIRECTION_DELTA_Y[nextDirection.intValue()], 0);
                Position point = new Position(current.getX(), current.getY(), current.getZ());
                mob.pathQueue.offer(point);
            } else {
                break;
            }
        }
        return new PathState(PathState.ROUTE_FOUND);
    }
}
