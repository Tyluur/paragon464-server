package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.region.Position;

/**
 * @author Boobs
 */
public class DefaultPathFinder implements PathFinder {

    private int writePathPosition = 0;

    @Override
    public PathState findPath(Mob mob, Object target, Position base, int srcX, int srcY, int dstX, int dstY,
                              int radius, boolean running, boolean ignoreLastStep, boolean moveNear) {
        if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || dstX >= 104 || dstY >= 104) {
            return new PathState(PathState.ROUTE_CANT_BE_REACHED);
        }
        if (srcX == dstX && srcY == dstY) {
            return new PathState(PathState.NO_ROUTE_NEEDED);
        }
        int z = mob.getPosition().getZ();
        Position position = new Position((base.getZoneX() - 6) << 3, (base.getZoneY() - 6) << 3, base.getZ());
        boolean foundPath = false;
        writePathPosition = 0;
        for (int xx = 0; xx < 104; xx++) {
            for (int yy = 0; yy < 104; yy++) {
                mob.via[xx][yy] = 0;
                mob.cost[xx][yy] = 99999999;
            }
        }
        int curX = srcX;
        int curY = srcY;
        int attempts = 0;
        int readPosition = 0;
        check(mob, curX, curY, 99, 0);
        PathState state = new PathState(PathState.ROUTE_FOUND);
        while (writePathPosition != readPosition) {
            curX = mob.queueX.get(readPosition);
            curY = mob.queueY.get(readPosition);
            readPosition = readPosition + 1 & 0xfff;
            if (curX == dstX && curY == dstY) {
                foundPath = true;
                break;
            }
            int absX = position.getX() + curX, absY = position.getY() + curY;
            int thisCost = mob.cost[curX][curY] + 1;
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
        if (!foundPath) {
            if (moveNear) {
                int fullCost = 1000;
                int thisCost = 100;
                int depth = 10;
                int xLength = mob.getSize();
                int yLength = mob.getSize();
                for (int x = dstX - depth; x <= dstX + depth; x++) {
                    for (int y = dstY - depth; y <= dstY + depth; y++) {
                        if (x >= 0 && y >= 0 && x < 104 && y < 104 && mob.cost[x][y] < 100) {
                            int diffX = 0;
                            if (x < dstX)
                                diffX = dstX - x;
                            else if (x > dstX + xLength - 1)
                                diffX = x - (dstX + xLength - 1);
                            int diffY = 0;
                            if (y < dstY)
                                diffY = dstY - y;
                            else if (y > dstY + yLength - 1)
                                diffY = y - (dstY + yLength - 1);
                            int totalCost = diffX * diffX + diffY * diffY;
                            if (totalCost < fullCost || (totalCost == fullCost && (mob.cost[x][y] < thisCost))) {
                                fullCost = totalCost;
                                thisCost = mob.cost[x][y];
                                curX = x;
                                curY = y;
                                state.setReached(false);
                            }
                        }
                    }
                }
                if (fullCost == 1000) {
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
        //mob.queueX.add(x);
        //mob.queueY.add(y);
        mob.queueX.add(writePathPosition, x);
        mob.queueY.add(writePathPosition, y);
        mob.via[x][y] = viaDir;
        mob.cost[x][y] = thisCost;
        writePathPosition = writePathPosition + 1 & 0xfff;
    }
}
