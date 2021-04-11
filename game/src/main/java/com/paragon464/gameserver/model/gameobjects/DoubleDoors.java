package com.paragon464.gameserver.model.gameobjects;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;

public class DoubleDoors {

    public static boolean handleDoor(final Player player, Door doorClicked) {
        int id = doorClicked.doorId;
        int x = doorClicked.doorX;
        int y = doorClicked.doorY;
        int z = doorClicked.doorZ;
        if (!doorClicked.isOpen()) {
            if (doorClicked.originalFace == 0) {
                Door lowerDoor = getDoor(id - 1, x, y - 1, z);
                Door upperDoor = getDoor(id + 1, x, y + 1, z);
                if (lowerDoor != null) {
                    changeLeftDoor(player, lowerDoor);
                    changeRightDoor(player, doorClicked);
                } else if (upperDoor != null) {
                    changeLeftDoor(player, doorClicked);
                    changeRightDoor(player, upperDoor);
                }
            } else if (doorClicked.originalFace == 1) {
                Door westDoor = getDoor(id - 1, x - 1, y, z);
                Door eastDoor = getDoor(id + 1, x + 1, y, z);
                if (westDoor != null) {
                    changeLeftDoor(player, westDoor);
                    changeRightDoor(player, doorClicked);
                } else if (eastDoor != null) {
                    changeLeftDoor(player, doorClicked);
                    changeRightDoor(player, eastDoor);
                }
            } else if (doorClicked.originalFace == 2) {
                Door lowerDoor = getDoor(id - 1, x, y + 1, z);
                Door upperDoor = getDoor(id + 1, x, y - 1, z);
                if (lowerDoor != null) {
                    changeLeftDoor(player, lowerDoor);
                    changeRightDoor(player, doorClicked);
                } else if (upperDoor != null) {
                    changeLeftDoor(player, doorClicked);
                    changeRightDoor(player, upperDoor);
                }
            } else if (doorClicked.originalFace == 3) {
                Door westDoor = getDoor(id + 1, x - 1, y, z);
                Door eastDoor = getDoor(id - 1, x + 1, y, z);
                if (westDoor != null) {
                    changeLeftDoor(player, westDoor);
                    changeRightDoor(player, doorClicked);
                } else if (eastDoor != null) {
                    changeLeftDoor(player, doorClicked);
                    changeRightDoor(player, eastDoor);
                }
            }
        } else if (doorClicked.isOpen()) {
            if (doorClicked.originalFace == 0) {
                Door westDoor = getDoor(id - 1, x - 1, y, z);
                Door upperDoor = getDoor(id + 1, x + 1, y, z);
                if (westDoor != null) {
                    changeLeftDoor(player, westDoor);
                    changeRightDoor(player, doorClicked);
                } else if (upperDoor != null) {
                    changeLeftDoor(player, doorClicked);
                    changeRightDoor(player, upperDoor);
                }
            } else if (doorClicked.originalFace == 1) {
                Door northDoor = getDoor(id - 1, x, y + 1, z);
                Door southDoor = getDoor(id + 1, x, y - 1, z);
                if (northDoor != null) {
                    changeLeftDoor(player, northDoor);
                    changeRightDoor(player, doorClicked);
                } else if (southDoor != null) {
                    changeLeftDoor(player, doorClicked);
                    changeRightDoor(player, southDoor);
                }
            } else if (doorClicked.originalFace == 2) {
                Door westDoor = getDoor(id - 1, x - 1, y, z);
                Door eastDoor = getDoor(id + 1, x, y - 1, z);
                if (westDoor != null) {
                    changeLeftDoor(player, westDoor);
                    changeRightDoor(player, doorClicked);
                } else if (eastDoor != null) {
                    changeLeftDoor(player, doorClicked);
                    changeRightDoor(player, eastDoor);
                }
            } else if (doorClicked.originalFace == 3) {
                Door northDoor = getDoor(id - 1, x, y + 1, z);
                Door southDoor = getDoor(id + 1, x, y - 1, z);
                if (northDoor != null) {
                    changeLeftDoor(player, northDoor);
                    changeRightDoor(player, doorClicked);
                } else if (southDoor != null) {
                    changeLeftDoor(player, doorClicked);
                    changeRightDoor(player, southDoor);
                }
            }
        }
        return true;
    }

    private static Door getDoor(int id, int x, int y, int z) {
        for (Door d : DoorManager.doors) {
            if (d.doorId == id) {
                if (d.doorX == x && d.doorY == y && d.doorZ == z) {
                    return d;
                }
            }
        }
        return null;
    }

    public static void changeLeftDoor(Player player, Door d) {
        int xAdjustment = 0, yAdjustment = 0;
        if (!d.isOpen()) {
            if (d.originalFace == 0 && d.currentFace == 0) {
                xAdjustment = -1;
            } else if (d.originalFace == 1 && d.currentFace == 1) {
                yAdjustment = 1;
            } else if (d.originalFace == 2 && d.currentFace == 2) {
                xAdjustment = +1;
            } else if (d.originalFace == 3 && d.currentFace == 3) {
                yAdjustment = -1;
            }
        } else if (d.isOpen()) {
            if (d.originalFace == 0 && d.currentFace == 0) {
                yAdjustment = -1;
            } else if (d.originalFace == 1 && d.currentFace == 1) {
                xAdjustment = -1;
            } else if (d.originalFace == 2 && d.currentFace == 2) {
                xAdjustment = -1;
            } else if (d.originalFace == 3 && d.currentFace == 3) {
                xAdjustment = -1;
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
         * if (d.doorId == d.originalId) { if (!d.isOpen()) { d.doorId -= 1; }
         * else if (d.isOpen()) { d.doorId += 1; } } else if (d.doorId !=
         * d.originalId) { if (!d.isOpen()) { d.doorId = d.originalId; } else if
         * (d.isOpen()) { d.doorId = d.originalId; } }
         */
        Position position = new Position(d.doorX, d.doorY, d.doorZ);
        GameObject o = new GameObject(position, d.doorId, 0, getNextLeftFace(d));
        World.spawnObject(o);
    }

    public static void changeRightDoor(Player player, Door d) {
        int xAdjustment = 0, yAdjustment = 0;
        if (!d.isOpen()) {
            if (d.originalFace == 0 && d.currentFace == 0) {
                xAdjustment = -1;
            } else if (d.originalFace == 1 && d.currentFace == 1) {
                yAdjustment = 1;
            } else if (d.originalFace == 2 && d.currentFace == 2) {
                xAdjustment = +1;
            } else if (d.originalFace == 3 && d.currentFace == 3) {
                yAdjustment = -1;
            }
        } else if (d.isOpen()) {
            if (d.originalFace == 0 && d.currentFace == 0) {
                xAdjustment = 1;
            } else if (d.originalFace == 1 && d.currentFace == 1) {
                xAdjustment = -1;
            } else if (d.originalFace == 2 && d.currentFace == 2) {
                yAdjustment = -1;
            } else if (d.originalFace == 3 && d.currentFace == 3) {
                xAdjustment = -1;
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
         * if (d.doorId == d.originalId) { if (!d.isOpen()) { d.doorId -= 1; }
         * else if (d.isOpen()) { d.doorId += 1; } } else if (d.doorId !=
         * d.originalId) { if (!d.isOpen()) { d.doorId = d.originalId; } else if
         * (d.isOpen()) { d.doorId = d.originalId; } }
         */
        Position position = new Position(d.doorX, d.doorY, d.doorZ);
        GameObject o = new GameObject(position, d.doorId, 0, getNextRightFace(d));
        World.spawnObject(o);
    }

    private static int getNextLeftFace(Door d) {
        int f = d.originalFace;
        if (!d.isOpen()) {
            if (d.originalFace == 0 && d.currentFace == 0) {
                f = 3;
            } else if (d.originalFace == 1 && d.currentFace == 1) {
                f = 0;
            } else if (d.originalFace == 2 && d.currentFace == 2) {
                f = 1;
            } else if (d.originalFace == 3 && d.currentFace == 3) {
                f = 0;
            } else if (d.originalFace != d.currentFace) {
                f = d.originalFace;
            }
        } else if (d.isOpen()) {
            if (d.originalFace == 0 && d.currentFace == 0) {
                f = 1;
            } else if (d.originalFace == 1 && d.currentFace == 1) {
                f = 2;
            } else if (d.originalFace == 2 && d.currentFace == 2) {
                f = 1;
            } else if (d.originalFace == 3 && d.currentFace == 3) {
                f = 2;
            } else if (d.originalFace != d.currentFace) {
                f = d.originalFace;
            }
        }
        d.currentFace = f;
        return f;
    }

    private static int getNextRightFace(Door d) {
        int f = d.originalFace;

        if (!d.isOpen()) {
            if (d.originalFace == 0 && d.currentFace == 0) {
                f = 1;
            } else if (d.originalFace == 1 && d.currentFace == 1) {
                f = 2;
            } else if (d.originalFace == 2 && d.currentFace == 2) {
                f = 3;
            } else if (d.originalFace == 3 && d.currentFace == 3) {
                f = 2;
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
        d.currentFace = f;
        return f;
    }
}
