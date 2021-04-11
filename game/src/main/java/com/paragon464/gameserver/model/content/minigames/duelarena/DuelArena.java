package com.paragon464.gameserver.model.content.minigames.duelarena;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.util.NumberUtils;

public class DuelArena {

    public static final Position DUEL_LOBBY = new Position(3367, 3276, 0);
    public static final Area[] OBSTACLE_AREA = new Area[]{
        new Area(new Position(3367, 3246, 0), new Position(3385, 3256, 0)),
        new Area(new Position(3336, 3227, 0), new Position(3355, 3237, 0)),
        new Area(new Position(3367, 3208, 0), new Position(3386, 3218, 0))};
    public static final Area[] NORMAL_AREA = new Area[]{
        new Area(new Position(3337, 3245, 0), new Position(3355, 3256, 0)),
        new Area(new Position(3366, 3227, 0), new Position(3386, 3237, 0)),
        new Area(new Position(3337, 3207, 0), new Position(3354, 3218, 0))};
    protected static final int[] RULE_CONFIGS = {1, 2, 16, 32, 64, 128, 256, 512, 1024, 4096, 8192, 16384, 32768,
        65536, 131072, 262144, 524288, 2097152, 8388608, 16777216, 67108864, 134217728, 268435456};
    protected static final int[] DUELING_BUTTON_IDS = {124, 144, 132, 145, 126, 148, 127, 149, 125, 150, 129, 151, 130,
        152, 131, 153, 154, 155, 156, 157, 159, 158, 200, 201, 202, 204, 205, 206, 207, 210, 209, 208, 203};
    protected static final int[] RULE_IDS = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11,
        12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
    protected static final int[] BEFORE_THE_DUEL_STARTS_CHILD_IDS = {138, 118, 119, 126, 127};
    protected static final int[] DURING_THE_DUEL_CHILD_IDS = {129, 130, 131, 132, 134, 135, 136, 137, 139, 140, 141};
    protected static final String[] BEFORE_THE_DUEL_STARTS = {"Some user items will be taken off",
        "Boosted stats will be restored", "Existing prayers will be stopped", "", ""};
    protected static final String[] RULE_STRINGS = {"You cannot forfeit the duel", "You cannot move",
        "You cannot use ranged attacks", "You cannot use melee attacks", "You cannot use magic attacks",
        "You cannot use drinks", "You cannot use food", "You cannot use prayer",
        "There will be obstacles in the arena.", "There will be fun weapons", "You cannot use special attacks."};

    public static Position getNextToPlayerPosition(Region region, Position loc) {
        int x = loc.getX();
        int y = loc.getY();
        int z = 0;
        Position up = new Position(x, y + 1, z);
        Position down = new Position(x, y - 1, z);
        Position left = new Position(x - 1, y, z);
        Position right = new Position(x + 1, y, z);
        if (World.getMask(up.getX(), up.getY(), 0) == 0)
            return up;
        else if (World.getMask(down.getX(), down.getY(), 0) == 0)
            return down;
        else if (World.getMask(left.getX(), left.getY(), 0) == 0)
            return left;
        else
            return right;
    }

    public static Position getRandomArenaPosition(Region region, boolean obstacles, int index) {
        return randomPosition(region, randomPositionSpawn(obstacles)[index]);
    }

    public static Position randomPosition(Region region, Area area) {
        Position finalPosition = new Position(
            area.getSouthWestCorner().getX()
                + NumberUtils.random(area.getNorthEastCorner().getX() - area.getSouthWestCorner().getX()),
            area.getSouthWestCorner().getY()
                + NumberUtils.random(area.getNorthEastCorner().getY() - area.getSouthWestCorner().getY()),
            area.getSouthWestCorner().getZ());
        while (World.getMask(finalPosition.getX(), finalPosition.getY(), finalPosition.getZ()) != 0)
            finalPosition = new Position(
                area.getSouthWestCorner().getX()
                    + NumberUtils.random(area.getNorthEastCorner().getX() - area.getSouthWestCorner().getX()),
                area.getSouthWestCorner().getY()
                    + NumberUtils.random(area.getNorthEastCorner().getY() - area.getSouthWestCorner().getY()),
                area.getSouthWestCorner().getZ());
        return finalPosition;
    }

    public static Area[] randomPositionSpawn(boolean obstacles) {
        if (obstacles)
            return OBSTACLE_AREA;
        else
            return NORMAL_AREA;
    }

    public static class Area {

        private Position southWestCorner;
        private Position northEastCorner;

        public Area(Position southWestCorner, Position northEastCorner) {
            this.southWestCorner = southWestCorner;
            this.northEastCorner = northEastCorner;
        }

        public Position getSouthWestCorner() {
            return southWestCorner;
        }

        public Position getNorthEastCorner() {
            return northEastCorner;
        }
    }
}
