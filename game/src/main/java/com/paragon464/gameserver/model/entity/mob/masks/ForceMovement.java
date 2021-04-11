package com.paragon464.gameserver.model.entity.mob.masks;

import com.paragon464.gameserver.model.region.Position;

public class ForceMovement {

    public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
    protected int direction;
    private Position toFirstTile;
    private Position toSecondTile;
    private int firstTileTicketDelay;
    private int secondTileTicketDelay;

    /*
     * USE: moves to firsttile firstTileTicketDelay: the delay in game tickets
     * between your tile and first tile the direction
     */
    public ForceMovement(Position toFirstTile, int firstTileTicketDelay, int direction) {
        this(toFirstTile, firstTileTicketDelay, null, 0, direction);
    }

    /*
     * USE: moves to firsttile and from first tile to second tile
     * firstTileTicketDelay: the delay in game tickets between your tile and
     * first tile secondTileTicketDelay: the delay in game tickets between first
     * tile and second tile the direction
     */
    public ForceMovement(Position toFirstTile, int firstTileTicketDelay, Position toSecondTile,
                         int secondTileTicketDelay, int direction) {
        this.toFirstTile = toFirstTile;
        this.firstTileTicketDelay = firstTileTicketDelay;
        this.toSecondTile = toSecondTile;
        this.secondTileTicketDelay = secondTileTicketDelay;
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public Position getFirstTile() {
        return toFirstTile;
    }

    public Position getSecondTile() {
        return toSecondTile;
    }

    public int getFirstTickDelay() {
        return firstTileTicketDelay;
    }

    public int getSecondTickDelay() {
        return secondTileTicketDelay;
    }
}
