package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.region.Position;

/**
 * @author 'Mystic Flow
 */
public class VariablePathFinder implements PathFinder {

    public int writePathPosition = 0;
    private int z;
    private int type;
    private int walkToData;
    private int direction;
    private int sizeX;
    private int sizeY;

    @Override
    public PathState findPath(Mob mob, Object target, Position base, int srcX, int srcY, int dstX, int dstY,
                              int radius, boolean running, boolean ignoreLastStep, boolean moveNear) {
        if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || dstX >= 104 || dstY >= 104) {
            return new PathState(PathState.ROUTE_CANT_BE_REACHED);
        }
        int z = mob.getPosition().getZ();
        if (target != null && target instanceof Mob) {
            if (!TileControl.locationOccupied(mob, (Mob) target)) {
                if (srcX == dstX && srcY == dstY) {
                    return new PathState(PathState.NO_ROUTE_NEEDED);
                }
            }
        } else {
            if (srcX == dstX && srcY == dstY) {
                return new PathState(PathState.NO_ROUTE_NEEDED);
            }
        }
        Position position = new Position((base.getZoneX() - 6) << 3, (base.getZoneY() - 6) << 3, base.getZ());
        boolean foundPath = false;
        for (int xx = 0; xx < 104; xx++) {
            for (int yy = 0; yy < 104; yy++) {
                mob.via[xx][yy] = 0;
                mob.cost[xx][yy] = 99999999;
            }
        }

        int curX = srcX;
        int curY = srcY;
        int requestX = dstX;
        int requestY = dstY;
        int attempts = 0;
        int readPosition = 0;
        check(mob, curX, curY, 99, 0);
        PathState state = new PathState(PathState.ROUTE_FOUND);
        while (writePathPosition != readPosition) {
            curX = mob.queueX.get(readPosition);
            curY = mob.queueY.get(readPosition);
            readPosition = readPosition + 1 & 0xfff;
            if (type == -2 && curX == dstX && curY == dstY) {
                requestX = curX;
                requestY = curY;
                foundPath = true;
                break;
            }
            int absX = position.getX() + curX, absY = position.getY() + curY;
            int thisCost = mob.cost[curX][curY] + 1;
            if (type != -1) {
                if (type == 0 || type == 1 || type == 2 || type == 3 || type == 9) {
                    if (reachedObject(mob, dstX, dstY, absX, absY, curX, curY, type, direction)) {
                        requestX = curX;
                        requestY = curY;
                        foundPath = true;
                        break;
                    }
                } else {
                    if (reachedObject2(mob, dstX, dstY, absX, absY, curX, curY, type, direction)) {
                        requestX = curX;
                        requestY = curY;
                        foundPath = true;
                        break;
                    }
                }
            } else {
                if (canInteract(mob, target, dstX, dstY, absX, absY, curX, curY, sizeX, sizeY, walkToData)) {
                    requestX = curX;
                    requestY = curY;
                    foundPath = true;
                    break;
                }
            }
            if (curY > 0 && mob.via[curX][curY - 1] == 0 && (World.getMask(absX, absY - 1, z) & 0x1280102) == 0) {
                check(mob, curX, curY - 1, SOUTH_FLAG, thisCost);
            }
            if (curX > 0 && mob.via[curX - 1][curY] == 0 && (World.getMask(absX - 1, absY, z) & 0x1280108) == 0) {
                check(mob, curX - 1, curY, WEST_FLAG, thisCost);
            }
            if (curY < 103 && mob.via[curX][curY + 1] == 0 && (World.getMask(absX, absY + 1, z) & 0x1280120) == 0) {
                check(mob, curX, curY + 1, NORTH_FLAG, thisCost);
            }
            if (curX < 103 && mob.via[curX + 1][curY] == 0 && (World.getMask(absX + 1, absY, z) & 0x1280180) == 0) {
                check(mob, curX + 1, curY, EAST_FLAG, thisCost);
            }
            if (curX > 0 && curY > 0 && mob.via[curX - 1][curY - 1] == 0
                && (World.getMask(absX - 1, absY - 1, z) & 0x128010e) == 0
                && (World.getMask(absX - 1, absY, z) & 0x1280108) == 0
                && (World.getMask(absX, absY - 1, z) & 0x1280102) == 0) {
                check(mob, curX - 1, curY - 1, SOUTH_WEST_FLAG, thisCost);
            }
            if (curX > 0 && curY < 103 && mob.via[curX - 1][curY + 1] == 0
                && (World.getMask(absX - 1, absY + 1, z) & 0x1280138) == 0
                && (World.getMask(absX - 1, absY, z) & 0x1280108) == 0
                && (World.getMask(absX, absY + 1, z) & 0x1280120) == 0) {
                check(mob, curX - 1, curY + 1, NORTH_WEST_FLAG, thisCost);
            }
            if (curX < 103 && curY > 0 && mob.via[curX + 1][curY - 1] == 0
                && (World.getMask(absX + 1, absY - 1, z) & 0x1280183) == 0
                && (World.getMask(absX + 1, absY, z) & 0x1280180) == 0
                && (World.getMask(absX, absY - 1, z) & 0x1280102) == 0) {
                check(mob, curX + 1, curY - 1, SOUTH_EAST_FLAG, thisCost);
            }
            if (curX < 103 && curY < 103 && mob.via[curX + 1][curY + 1] == 0
                && (World.getMask(absX + 1, absY + 1, z) & 0x12801e0) == 0
                && (World.getMask(absX + 1, absY, z) & 0x1280180) == 0
                && (World.getMask(absX, absY + 1, z) & 0x1280120) == 0) {
                check(mob, curX + 1, curY + 1, NORTH_EAST_FLAG, thisCost);
            }
        }
        curX = requestX;
        curY = requestY;
        if (!foundPath) {
            if (moveNear) {
                int fullCost = 1000;
                int thisCost = 100;
                int range = 10;
                for (int x = dstX - range; x <= dstX + range; x++) {
                    for (int y = dstY - range; y <= dstY + range; y++) {
                        if (x >= 0 && y >= 0 && x < 104 && y < 104 && mob.cost[x][y] < 100) {
                            int diffX = 0;
                            if (x < dstX)
                                diffX = dstX - x;
                            else if (x > dstX + sizeX - 1)
                                diffX = x - (dstX + sizeX - 1);
                            int diffY = 0;
                            if (y < dstY)
                                diffY = dstY - y;
                            else if (y > dstY + sizeY - 1)
                                diffY = y - (dstY + sizeY - 1);
                            int distance = diffX * diffX + diffY * diffY;
                            if (distance < fullCost || (distance == fullCost && (mob.cost[x][y] < thisCost))) {
                                fullCost = distance;
                                thisCost = mob.cost[x][y];
                                curX = x;
                                curY = y;
                                foundPath = true;
                                state.setReached(false);
                            }
                        }
                    }
                }
                if (!foundPath) {
                    state.routeFailed();
                    return state;
                }
            }
        }
        readPosition = 0;
        mob.queueX.set(readPosition, curX);
        mob.queueY.set(readPosition++, curY);
        int l5;
        attempts = 0;
        for (int j5 = l5 = mob.via[curX][curY]; curX != srcX || curY != srcY; j5 = mob.via[curX][curY]) {
            if (attempts++ > mob.queueX.size()) {
                state.routeFailed();
                return state;
            }
            if (j5 != l5) {
                l5 = j5;
                mob.queueX.set(readPosition, curX);
                mob.queueY.set(readPosition++, curY);
            }
            if ((j5 & WEST_FLAG) != 0) {
                curX++;
            } else if ((j5 & EAST_FLAG) != 0) {
                curX--;
            }
            if ((j5 & SOUTH_FLAG) != 0) {
                curY++;
            } else if ((j5 & NORTH_FLAG) != 0) {
                curY--;
            }
        }
        int size = readPosition--;
        int absX = position.getX() + mob.queueX.get(readPosition);
        int absY = position.getY() + mob.queueY.get(readPosition);
        mob.pathQueue.offer(new Position(absX, absY, z));
        Position last = null;
        for (int i = 1; i < size; i++) {
            readPosition--;
            absX = position.getX() + mob.queueX.get(readPosition);
            absY = position.getY() + mob.queueY.get(readPosition);
            Position dest = new Position(absX, absY, z);
            mob.pathQueue.offer(dest);
            last = dest;
        }
        state.routeFound();
        state.setPoints(mob.pathQueue);
        state.setDest(last);
        return state;
    }

    public void check(Mob mob, int x, int y, int viaDir, int thisCost) {
        mob.queueX.add(x);
        mob.queueY.add(y);
        mob.via[x][y] = viaDir;
        mob.cost[x][y] = thisCost;
        writePathPosition = writePathPosition + 1 & 0xfff;
    }

    public boolean reachedObject(Mob mob, int dstX, int dstY, int absX, int absY, int curX, int curY, int type,
                                 int direction) {
        if (curX == dstX && curY == dstY)
            return true;
        int clipping = World.getMask(absX, absY, z);
        if (type == 0)
            if (direction == 0) {
                if (curX == dstX - 1 && curY == dstY)
                    return true;
                if (curX == dstX && curY == dstY + 1 && (clipping & 0x2c0120) == 0)
                    return true;
                if (curX == dstX && curY == dstY - 1 && (clipping & 0x2c0102) == 0)
                    return true;
            } else if (direction == 1) {
                if (curX == dstX && curY == dstY + 1)
                    return true;
                if (curX == dstX - 1 && curY == dstY && (clipping & 0x2c0108) == 0)
                    return true;
                if (curX == dstX + 1 && curY == dstY && (clipping & 0x2c0180) == 0)
                    return true;
            } else if (direction == 2) {
                if (curX == dstX + 1 && curY == dstY)
                    return true;
                if (curX == dstX && curY == dstY + 1 && (clipping & 0x2c0120) == 0)
                    return true;
                if (curX == dstX && curY == dstY - 1 && (clipping & 0x2c0102) == 0)
                    return true;
            } else if (direction == 3) {
                if (curX == dstX && curY == dstY - 1)
                    return true;
                if (curX == dstX - 1 && curY == dstY && (clipping & 0x2c0108) == 0)
                    return true;
                if (curX == dstX + 1 && curY == dstY && (clipping & 0x2c0180) == 0)
                    return true;
            }
        if (type == 2)
            if (direction == 0) {
                if (curX == dstX - 1 && curY == dstY)
                    return true;
                if (curX == dstX && curY == dstY + 1)
                    return true;
                if (curX == dstX + 1 && curY == dstY && (clipping & 0x2c0180) == 0)
                    return true;
                if (curX == dstX && curY == dstY - 1 && (clipping & 0x2c0102) == 0)
                    return true;
            } else if (direction == 1) {
                if (curX == dstX - 1 && curY == dstY && (clipping & 0x2c0108) == 0)
                    return true;
                if (curX == dstX && curY == dstY + 1)
                    return true;
                if (curX == dstX + 1 && curY == dstY)
                    return true;
                if (curX == dstX && curY == dstY - 1 && (clipping & 0x2c0102) == 0)
                    return true;
            } else if (direction == 2) {
                if (curX == dstX - 1 && curY == dstY && (clipping & 0x2c0108) == 0)
                    return true;
                if (curX == dstX && curY == dstY + 1 && (clipping & 0x2c0120) == 0)
                    return true;
                if (curX == dstX + 1 && curY == dstY)
                    return true;
                if (curX == dstX && curY == dstY - 1)
                    return true;
            } else if (direction == 3) {
                if (curX == dstX - 1 && curY == dstY)
                    return true;
                if (curX == dstX && curY == dstY + 1 && (clipping & 0x2c0120) == 0)
                    return true;
                if (curX == dstX + 1 && curY == dstY && (clipping & 0x2c0180) == 0)
                    return true;
                if (curX == dstX && curY == dstY - 1)
                    return true;
            }
        if (type == 9) {
            if (curX == dstX && curY == dstY + 1 && (clipping & 0x20) == 0)
                return true;
            if (curX == dstX && curY == dstY - 1 && (clipping & 2) == 0)
                return true;
            if (curX == dstX - 1 && curY == dstY && (clipping & 8) == 0)
                return true;
            return curX == dstX + 1 && curY == dstY && (clipping & 0x80) == 0;
        }
        return false;
    }

    public boolean reachedObject2(Mob mob, int dstX, int dstY, int absX, int absY, int curX, int curY, int type,
                                  int direction) {
        if (curX == dstX && curY == dstY)
            return true;
        int clipping = World.getMask(absX, absY, z);
        if (type == 6 || type == 7) {
            if (type == 7)
                direction = direction + 2 & 3;
            if (direction == 0) {
                if (curX == dstX + 1 && curY == dstY && (clipping & 0x80) == 0)
                    return true;
                if (curX == dstX && curY == dstY - 1 && (clipping & 2) == 0)
                    return true;
            } else if (direction == 1) {
                if (curX == dstX - 1 && curY == dstY && (clipping & 8) == 0)
                    return true;
                if (curX == dstX && curY == dstY - 1 && (clipping & 2) == 0)
                    return true;
            } else if (direction == 2) {
                if (curX == dstX - 1 && curY == dstY && (clipping & 8) == 0)
                    return true;
                if (curX == dstX && curY == dstY + 1 && (clipping & 0x20) == 0)
                    return true;
            } else if (direction == 3) {
                if (curX == dstX + 1 && curY == dstY && (clipping & 0x80) == 0)
                    return true;
                if (curX == dstX && curY == dstY + 1 && (clipping & 0x20) == 0)
                    return true;
            }
        }
        if (type == 8) {
            if (curX == dstX && curY == dstY + 1 && (clipping & 0x20) == 0)
                return true;
            if (curX == dstX && curY == dstY - 1 && (clipping & 2) == 0)
                return true;
            if (curX == dstX - 1 && curY == dstY && (clipping & 8) == 0)
                return true;
            return curX == dstX + 1 && curY == dstY && (clipping & 0x80) == 0;
        }
        return false;
    }

    private boolean canInteract(Mob mob, Object target, int dstX, int dstY, int absX, int absY, int curX,
                                int curY, int sizeX, int sizeY, int walkToData) {
        if ((walkToData & 0x80000000) != 0) {
            if (curX == dstX && curY == dstY) {
                return false;
            }
        }
        int maxX = (dstX + sizeX) - 1;
        int maxY = (dstY + sizeY) - 1;
        int clipping = World.getMask(absX, absY, z);
        if (target != null && target instanceof Mob) {
            if (curX >= dstX && maxX >= curX && dstY <= curY && maxY >= curY) {
                if (!TileControl.locationOccupied(mob, (Mob) target)) {
                    return true;
                }
            }
        } else {
            if (curX >= dstX && maxX >= curX && dstY <= curY && maxY >= curY) {
                return true;
            }
        }
        if (curX == dstX - 1 && curY >= dstY && curY <= maxY && (clipping & 8) == 0 && (walkToData & 8) == 0) {
            return true;
        }
        if (curX == maxX + 1 && curY >= dstY && curY <= maxY && (clipping & 0x80) == 0 && (walkToData & 2) == 0) {
            return true;
        }
        return curY == dstY - 1 && curX >= dstX && curX <= maxX && (clipping & 2) == 0 && (walkToData & 4) == 0
            || curY == maxY + 1 && curX >= dstX && curX <= maxX && (clipping & 0x20) == 0
            && (walkToData & 1) == 0;
    }

    public void setType(int var) {
        this.type = var;
    }

    public void setWalkToData(int var) {
        this.walkToData = var;
    }

    public void setDirection(int var) {
        this.direction = var;
    }

    public void setSizeX(int var) {
        this.sizeX = var;
    }

    public void setSizeY(int var) {
        this.sizeY = var;
    }
}
