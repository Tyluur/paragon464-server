package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.region.Position;

/**
 * @author 'Mystic Flow <Steven@rune-server.org>
 */
public class SizedPathFinder implements PathFinder {

    public boolean checkNPCs, checkPlayers;
    private int writePathPosition = 0;

    public SizedPathFinder() {
        this(false);
    }

    public SizedPathFinder(boolean checkNPCs) {
        this(checkNPCs, false);
    }

    public SizedPathFinder(boolean checkNPCs, boolean checkPlayers) {
        this.checkNPCs = checkNPCs;
        this.checkPlayers = checkPlayers;
    }

    @Override
    public PathState findPath(Mob mob, Object target, Position base, int srcX, int srcY, int dstX, int dstY,
                              int radius, boolean running, boolean ignoreLastStep, boolean moveNear) {
        writePathPosition = 0;
        if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || dstX >= 104 || dstY >= 104) {
            return new PathState(PathState.ROUTE_CANT_BE_REACHED);
        }
        if (srcX == dstX && srcY == dstY) {
            return new PathState(PathState.NO_ROUTE_NEEDED);
        }
        int z = mob.getPosition().getZ();
        Position position = new Position((base.getZoneX() - 6) << 3, (base.getZoneY() - 6) << 3, base.getZ());
        boolean foundPath = false;
        int size = mob.getSize();
        int flags = 0;
        for (int xx = 0; xx < 104; xx++) {
            for (int yy = 0; yy < 104; yy++) {
                mob.via[xx][yy] = 0;
                mob.cost[xx][yy] = 99999999;
            }
        }
        if (checkNPCs)
            flags |= 0x1;
        if (checkPlayers)
            flags |= 0x2;

        int curX = srcX;
        int curY = srcY;
        int attempts = 0;
        int readPosition = 0;
        check(mob, curX, curY, 99, 0);

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
            if (curX > size - 1) {
                if (mob.via[curX - 1][curY] == 0
                    && PrimitivePathFinder.canMove(mob, absX, absY, Directions.NormalDirection.WEST, size, flags)) {
                    check(mob, curX - 1, curY, WEST_FLAG, thisCost);
                }
            }
            if (curX < 104 - size) {
                if (mob.via[curX + 1][curY] == 0
                    && PrimitivePathFinder.canMove(mob, absX, absY, Directions.NormalDirection.EAST, size, flags)) {
                    check(mob, curX + 1, curY, EAST_FLAG, thisCost);
                }
            }
            if (curY > size - 1) {
                if (mob.via[curX][curY - 1] == 0
                    && PrimitivePathFinder.canMove(mob, absX, absY, Directions.NormalDirection.SOUTH, size, flags)) {
                    check(mob, curX, curY - 1, SOUTH_FLAG, thisCost);
                }
            }
            if (curY < 104 - size) {
                if (mob.via[curX][curY + 1] == 0
                    && PrimitivePathFinder.canMove(mob, absX, absY, Directions.NormalDirection.NORTH, size, flags)) {
                    check(mob, curX, curY + 1, NORTH_FLAG, thisCost);
                }
            }
            if (curX > size - 1 && curY > size - 1) {
                if (mob.via[curX - 1][curY - 1] == 0
                    && PrimitivePathFinder.canMove(mob, absX, absY, Directions.NormalDirection.SOUTH_WEST, size, flags)) {
                    check(mob, curX - 1, curY - 1, SOUTH_WEST_FLAG, thisCost);
                }
            }
            if (curX < 104 - size && curY > size - 1) {
                if (mob.via[curX + 1][curY - 1] == 0
                    && PrimitivePathFinder.canMove(mob, absX, absY, Directions.NormalDirection.SOUTH_EAST, size, flags)) {
                    check(mob, curX + 1, curY - 1, SOUTH_EAST_FLAG, thisCost);
                }
            }
            if (curX > size - 1 && curY < 104 - size) {
                if (mob.via[curX - 1][curY + 1] == 0
                    && PrimitivePathFinder.canMove(mob, absX, absY, Directions.NormalDirection.NORTH_WEST, size, flags)) {
                    check(mob, curX - 1, curY + 1, NORTH_WEST_FLAG, thisCost);
                }
            }
            if (curX < 104 - size && curY < 104 - size) {
                if (mob.via[curX + 1][curY + 1] == 0
                    && PrimitivePathFinder.canMove(mob, absX, absY, Directions.NormalDirection.NORTH_EAST, size, flags)) {
                    check(mob, curX + 1, curY + 1, NORTH_EAST_FLAG, thisCost);
                }
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
                            }
                        }
                    }
                }
                if (fullCost == 1000)
                    return new PathState(PathState.ROUTE_CANT_BE_REACHED);
            }
        }
        readPosition = 0;
        mob.queueX.set(readPosition, curX);
        mob.queueY.set(readPosition++, curY);
        int l5;
        attempts = 0;
        for (int j5 = l5 = mob.via[curX][curY]; curX != srcX || curY != srcY; j5 = mob.via[curX][curY]) {
            if (attempts++ >= mob.queueX.size()) {
                return new PathState(PathState.ROUTE_CANT_BE_REACHED);
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
        int readSize = readPosition--;
        int absX = position.getX() + mob.queueX.get(readPosition);
        int absY = position.getY() + mob.queueY.get(readPosition);
        mob.pathQueue.offer(new Position(absX, absY, z));
        Position last = null;
        for (int i = 1; i < readSize; i++) {
            readPosition--;
            absX = position.getX() + mob.queueX.get(readPosition);
            absY = position.getY() + mob.queueY.get(readPosition);
            Position dest = new Position(absX, absY, z);
            mob.pathQueue.offer(dest);
            last = dest;
        }
        return new PathState(PathState.ROUTE_FOUND, mob.pathQueue, last);
    }

    public void check(Mob mob, int x, int y, int viaDir, int thisCost) {
        mob.queueX.add(x);//TODO
        mob.queueY.add(y);//TODO
        mob.via[x][y] = viaDir;
        mob.cost[x][y] = thisCost;
        writePathPosition = writePathPosition + 1 & 0xfff;
    }
}
