package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.region.Position;

import java.awt.*;

/**
 * @author Boobs
 */
public class Directions {

    public static final byte[] DIRECTION_DELTA_X = new byte[]{-1, 0, 1, -1, 1, -1, 0, 1};
    public static final byte[] DIRECTION_DELTA_Y = new byte[]{-1, -1, -1, 0, 0, 1, 1, 1};
    public static final byte[] MOVEMENT_DIRECTION_DELTA_X = new byte[]{-1, 0, 1, -1, 1, -1, 0, 1};
    public static final byte[] MOVEMENT_DIRECTION_DELTA_Y = new byte[]{1, 1, 1, 0, 0, -1, -1, -1};

    public static NormalDirection directionFor(Position currentPos, Position nextPos) {
        int dirX = (nextPos.getX() - currentPos.getX());
        int dirY = (nextPos.getY() - currentPos.getY());
        if (dirX < 0) {
            if (dirY < 0)
                return NormalDirection.SOUTH_WEST;
            else if (dirY > 0)
                return NormalDirection.NORTH_WEST;
            else
                return NormalDirection.WEST;
        } else if (dirX > 0) {
            if (dirY < 0)
                return NormalDirection.SOUTH_EAST;
            else if (dirY > 0)
                return NormalDirection.NORTH_EAST;
            else
                return NormalDirection.EAST;
        } else {
            if (dirY < 0)
                return NormalDirection.SOUTH;
            else if (dirY > 0)
                return NormalDirection.NORTH;
            else
                return null;
        }
    }

    public static RunningDirection runningDirectionFor(int dirX, int dirY) {
        switch (dirX) {
            case -2:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS_WW;
                    case -1:
                        return RunningDirection.S_WW;
                    case 0:
                        return RunningDirection.WW;
                    case 1:
                        return RunningDirection.N_WW;
                    case 2:
                        return RunningDirection.NN_WW;
                }
                return null;
            case -1:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS_W;
                    case 2:
                        return RunningDirection.NN_W;
                }
                return null;
            case 0:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS;
                    case 2:
                        return RunningDirection.NN;
                }
                return null;
            case 1:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS_E;
                    case 2:
                        return RunningDirection.NN_E;
                }
                return null;
            case 2:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS_EE;
                    case -1:
                        return RunningDirection.S_EE;
                    case 0:
                        return RunningDirection.EE;
                    case 1:
                        return RunningDirection.N_EE;
                    case 2:
                        return RunningDirection.NN_EE;
                }
                return null;
        }
        return null;
    }

    public static RunningDirection runningDirectionFor(int curX, int curY, int dstX, int dstY) {
        int dirX = dstX - curX;
        int dirY = dstY - curX;
        switch (dirX) {
            case -2:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS_WW;
                    case -1:
                        return RunningDirection.S_WW;
                    case 0:
                        return RunningDirection.WW;
                    case 1:
                        return RunningDirection.N_WW;
                    case 2:
                        return RunningDirection.NN_WW;
                }
                return null;
            case -1:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS_W;
                    case 2:
                        return RunningDirection.NN_W;
                }
                return null;
            case 0:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS;
                    case 2:
                        return RunningDirection.NN;
                }
                return null;
            case 1:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS_E;
                    case 2:
                        return RunningDirection.NN_E;
                }
                return null;
            case 2:
                switch (dirY) {
                    case -2:
                        return RunningDirection.SS_EE;
                    case -1:
                        return RunningDirection.S_EE;
                    case 0:
                        return RunningDirection.EE;
                    case 1:
                        return RunningDirection.N_EE;
                    case 2:
                        return RunningDirection.NN_EE;
                }
                return null;
        }
        return null;
    }

    public static NormalDirection directionFor(int curX, int curY, int dstX, int dstY) {
        int dirX = dstX - curX;
        int dirY = dstY - curX;
        if (dirX < 0) {
            if (dirY < 0)
                return NormalDirection.SOUTH_WEST;
            else if (dirY > 0)
                return NormalDirection.NORTH_WEST;
            else
                return NormalDirection.WEST;
        } else if (dirX > 0) {
            if (dirY < 0)
                return NormalDirection.SOUTH_EAST;
            else if (dirY > 0)
                return NormalDirection.NORTH_EAST;
            else
                return NormalDirection.EAST;
        } else {
            if (dirY < 0)
                return NormalDirection.SOUTH;
            else if (dirY > 0)
                return NormalDirection.NORTH;
            else
                return null;
        }
    }

    /**
     * Calculates the direction between the two tiles.
     *
     * @param currentPos The current point.
     * @param nextPos    The next point.
     * @return The direction in which the next point is.
     */
    public static NormalDirection directionForr(Position currentPos, Position nextPos) {
        return directionFor(new Point(currentPos.getX(), currentPos.getY()), new Point(nextPos.getX(), nextPos.getY()));
    }

    public static NormalDirection directionFor(Point currentPos, Point nextPos) {
        int dirX = (int) (nextPos.getX() - currentPos.getX());
        int dirY = (int) (nextPos.getY() - currentPos.getY());
        if (dirX < 0) {
            if (dirY < 0)
                return NormalDirection.SOUTH_WEST;
            else if (dirY > 0)
                return NormalDirection.NORTH_WEST;
            else
                return NormalDirection.WEST;
        } else if (dirX > 0) {
            if (dirY < 0)
                return NormalDirection.SOUTH_EAST;
            else if (dirY > 0)
                return NormalDirection.NORTH_EAST;
            else
                return NormalDirection.EAST;
        } else {
            if (dirY < 0)
                return NormalDirection.SOUTH;
            else if (dirY > 0)
                return NormalDirection.NORTH;
            else
                return null;
        }
    }

    /**
     * Checks if a clipping flag is between the mob and the victim.
     *
     * @param mob   The mob.
     * @param other The victim.
     * @return {@code True} if not, {@code false} if so.
     */
    public static boolean isWallInPath(Mob mob, Mob other) {
        if (mob.isNPC()) {
            int id = ((NPC) mob).getId();
            if (id == 749 || id == 750) {
                return false;
            }
        }
        if (other.isNPC()) {
            int id = ((NPC) other).getId();
            if (id == 749 || id == 750) {
                return false;
            }
        }
        int dirX = other.getPosition().getX() - mob.getPosition().getX();
        int dirY = other.getPosition().getY() - mob.getPosition().getY();
        return !PrimitivePathFinder.canMove(mob, mob.getPosition(), Directions.directionFor(dirX, dirY), false);
    }

    public static NormalDirection directionFor(int dirX, int dirY) {
        if (dirX < 0) {
            if (dirY < 0)
                return NormalDirection.SOUTH_WEST;
            else if (dirY > 0)
                return NormalDirection.NORTH_WEST;
            else
                return NormalDirection.WEST;
        } else if (dirX > 0) {
            if (dirY < 0)
                return NormalDirection.SOUTH_EAST;
            else if (dirY > 0)
                return NormalDirection.NORTH_EAST;
            else
                return NormalDirection.EAST;
        } else {
            if (dirY < 0)
                return NormalDirection.SOUTH;
            else if (dirY > 0)
                return NormalDirection.NORTH;
            else
                return null;
        }
    }

    public static final int getMoveDirection(int xOffset, int yOffset) {
        if (xOffset < 0) {
            if (yOffset < 0)
                return 5;
            else if (yOffset > 0)
                return 0;
            else
                return 3;
        } else if (xOffset > 0) {
            if (yOffset < 0)
                return 7;
            else if (yOffset > 0)
                return 2;
            else
                return 4;
        } else {
            if (yOffset < 0)
                return 6;
            else if (yOffset > 0)
                return 1;
            else
                return -1;
        }
    }

    public enum RunningDirection {
        EE(8), N_EE(10), N_WW(9), NN(13), NN_E(14), NN_EE(15), NN_W(12), NN_WW(11), S_EE(6), S_WW(5), SS(2), SS_E(
            3), SS_EE(4), SS_W(1), SS_WW(0), WW(7);
        private int dir;

        RunningDirection(int dir) {
            this.dir = dir;
        }

        public int intValue() {
            return dir;
        }

        public int npcIntValue() {
            throw new UnsupportedOperationException("The GNP protocol does not support 2 step running directions!");
        }

        @Override
        public String toString() {
            return "[run] [dir=" + dir + ", type=" + super.toString() + "]";
        }
    }

    public enum NormalDirection {
        EAST(4, 2), NORTH(6, 0), NORTH_EAST(7, 1), NORTH_WEST(5, 7), SOUTH(1, 4), SOUTH_EAST(2, 3), SOUTH_WEST(0,
            5), WEST(3, 6);

        private int dir;
        private int npcDir;

        NormalDirection(int dir, int npcDir) {
            this.dir = dir;
            this.npcDir = npcDir;
        }

        public static NormalDirection forStringValue(String value) {
            switch (value) {
                case "NORTH":
                    return NORTH;
                case "SOUTH":
                    return SOUTH;
                case "EAST":
                    return EAST;
                case "WEST":
                    return WEST;
                case "SOUTH_WEST":
                    return SOUTH_WEST;
                case "SOUTH_EAST":
                    return SOUTH_EAST;
                case "NORTH_WEST":
                    return NORTH_WEST;
                case "NORTH_EAST":
                    return NORTH_EAST;
            }
            return null;
        }

        public static NormalDirection forFixedStringValue(String value) {
            switch (value) {
                case "North":
                    return NORTH;
                case "South":
                    return SOUTH;
                case "East":
                    return EAST;
                case "West":
                    return WEST;
                case "South-West":
                    return SOUTH_WEST;
                case "South-East":
                    return SOUTH_EAST;
                case "North-West":
                    return NORTH_WEST;
                case "North-East":
                    return NORTH_EAST;
            }
            return null;
        }

        public static NormalDirection forIntValue(int value) {
            switch (value) {
                case 0:
                    return SOUTH_WEST;
                case 1:
                    return SOUTH;
                case 2:
                    return SOUTH_EAST;
                case 3:
                    return WEST;
                case 4:
                    return EAST;
                case 5:
                    return NORTH_WEST;
                case 6:
                    return NORTH;
                case 7:
                    return NORTH_EAST;
            }
            return null;
        }

        public static NormalDirection forNpcDirValue(int value) {
            switch (value) {
                case 0:
                    return NORTH;
                case 1:
                    return NORTH_EAST;
                case 2:
                    return EAST;
                case 3:
                    return SOUTH_EAST;
                case 4:
                    return SOUTH;
                case 5:
                    return SOUTH_WEST;
                case 6:
                    return WEST;
                case 7:
                    return NORTH_WEST;
            }
            return null;
        }

        public int intValue() {
            return dir;
        }

        public int npcIntValue() {
            return npcDir;
        }

        public String getString() {
            switch (this) {
                case NORTH:
                    return "North";
                case SOUTH:
                    return "South";
                case EAST:
                    return "East";
                case WEST:
                    return "West";
                case SOUTH_WEST:
                    return "South-West";
                case SOUTH_EAST:
                    return "South-East";
                case NORTH_WEST:
                    return "North-West";
                case NORTH_EAST:
                    return "North-East";
            }
            return null;
        }

        @Override
        public String toString() {
            return "[walk] [dir=" + dir + ", type=" + super.toString() + "]";
        }

        public String stringValue() {
            return super.toString();
        }
    }
}
