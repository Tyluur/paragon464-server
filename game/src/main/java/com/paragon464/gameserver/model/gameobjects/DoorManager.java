package com.paragon464.gameserver.model.gameobjects;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DoorManager {

    private static final Logger logger = LoggerFactory.getLogger(DoorManager.class);
    public static List<Door> doors = new ArrayList<>();

    public static boolean handleDoor(final Player player, final GameObject object) {
        Door d = getDoor(object);
        if (d == null)
            return false;
        if (d.getDoorType().equals(DoorType.DOUBLE))
            return DoubleDoors.handleDoor(player, d);
        int xAdjustment = 0, yAdjustment = 0;
        if (d.type == 0) {
            if (!d.isOpen()) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    xAdjustment = -1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    yAdjustment = 1;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    xAdjustment = 1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    yAdjustment = -1;
                }
            } else if (d.isOpen()) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    yAdjustment = 1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    xAdjustment = 1;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    yAdjustment = -1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    xAdjustment = -1;
                }
            }
        } else if (d.type == 9) {
            if (!d.isOpen()) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    xAdjustment = 1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    xAdjustment = 1;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    xAdjustment = -1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    xAdjustment = -1;
                }
            } else if (d.isOpen()) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    xAdjustment = 1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    xAdjustment = 1;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    xAdjustment = -1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    xAdjustment = -1;
                }
            }
        }
        if (xAdjustment != 0 || yAdjustment != 0) {
            Position position = new Position(d.doorX, d.doorY, d.doorZ);
            final GameObject originalObject = World.getObjectWithId(position, d.doorId);
            World.removeObject(originalObject, false);
        }
        if (d.doorX == d.originalX && d.doorY == d.originalY) {
            d.doorX += xAdjustment;
            d.doorY += yAdjustment;
        } else {
            Position position = new Position(d.doorX, d.doorY, d.doorZ);
            final GameObject originalObject = World.getObjectWithId(position, d.doorId);
            World.removeObject(originalObject, false);
            d.doorX = d.originalX;
            d.doorY = d.originalY;
        }
        /*
         * if (d.doorId == d.originalId) { if (!d.isOpen()) { d.doorId += 1; }
         * else if (d.isOpen()) { d.doorId -= 1; } } else if (d.doorId !=
         * d.originalId) { if (!d.isOpen()) { d.doorId -= 1; } else if
         * (d.isOpen()) { d.doorId += 1; } }
         */
        Position position = new Position(d.doorX, d.doorY, d.doorZ);
        GameObject o = new GameObject(position, d.doorId, d.type, getNextFace(d));
        World.spawnObject(o);
        return true;
    }

    private static Door getDoor(final GameObject object) {
        for (Door d : doors) {
            if (d.doorId == object.getId()) {
                Position objLoc = object.getPosition();
                if (d.doorX == objLoc.getX() && d.doorY == objLoc.getY() && d.doorZ == objLoc.getZ()) {
                    return d;
                }
            }
        }
        return null;
    }

    private static int getNextFace(Door d) {
        int f = d.originalFace;
        if (d.type == 0) {
            if (!d.isOpen()) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    f = 1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    f = 2;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    f = 3;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    f = 0;
                } else if (d.originalFace != d.currentFace) {
                    f = d.originalFace;
                }
            } else if (d.isOpen()) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    f = 3;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    f = 0;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    f = 1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    f = 2;
                } else if (d.originalFace != d.currentFace) {
                    f = d.originalFace;
                }
            }
        } else if (d.type == 9) {
            if (!d.isOpen()) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    f = 3;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    f = 2;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    f = 1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    f = 0;
                } else if (d.originalFace != d.currentFace) {
                    f = d.originalFace;
                }
            } else if (d.isOpen()) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    f = 3;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    f = 0;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    f = 1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    f = 2;
                } else if (d.originalFace != d.currentFace) {
                    f = d.originalFace;
                }
            }
        }
        d.currentFace = f;
        return f;
    }

    public enum DoorType {
        SINGLE, DOUBLE
    }
}
