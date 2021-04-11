package com.paragon464.gameserver.model.region;

import com.google.common.base.Objects;
import com.paragon464.gameserver.model.entity.Entity;

public class Position {
    private final int position;

    public Position(final int x, final int y) {
        this(x, y, 0);
    }

    public Position(final int x, final int y, final int z) {
        this.position = y | (x << 14) | (z << 28);
    }

    public int getLocalX() {
        return getLocalX(this);
    }

    public int getLocalX(final Position position) {
        return getX() - 8 * (position.getZoneX() - 6);
    }

    public int getX() {
        return position >> 14 & 0x3FFF;
    }

    public int getZoneX() {
        return getX() >> 3;
    }

    public int getLocalY() {
        return getLocalY(this);
    }

    public int getLocalY(final Position position) {
        return getY() - 8 * (position.getZoneY() - 6);
    }

    public int getY() {
        return position & 0x3FFF;
    }

    public int getZoneY() {
        return getY() >> 3;
    }

    public int getXInZone() {
        return getX() & 0x7;
    }

    public int getYInZone() {
        return getY() & 0x7;
    }

    public boolean isWithinRadius(final Entity actor, final int radius) {
        return isWithinRadius(actor.getPosition(), radius);
    }

    public boolean isWithinRadius(final Position center, final int radius) {
        if (getZ() != center.getZ()) {
            return false;
        }

        final int deltaX = center.getX() - getX();
        final int deltaY = center.getY() - getY();
        return Math.abs(deltaX) <= radius && Math.abs(deltaY) <= radius;
    }

    public boolean isVisibleFrom(final Position other) {
        return getZ() == other.getZ() && getDistanceFrom(other) <= MapConstants.DRAW_DISTANCE;
    }

    public int getZ() {
        return position >>> 28;
    }

    public int getDistanceFrom(final Position position) {
        return (int) Math.sqrt(Math.pow((double) (getX() - position.getX()), 2.0)
            + Math.pow((double) (getY() - position.getY()), 2.0));
    }

    public boolean isDiagonalFrom(final Position position) {
        return position.getNorthEast().equals(this)
            || position.getNorthWest().equals(this)
            || position.getSouthEast().equals(this)
            || position.getSouthWest().equals(this);
    }

    public boolean isDiagonalFrom(final Entity actor) {
        return isDiagonalFrom(actor.getPosition());
    }

    public boolean isInZone(final Position min, final Position max) {
        if (getX() < min.getX() || getX() > max.getX()) {
            return false;
        } else if (getY() < min.getY() || getY() > max.getY()) {
            return false;
        } else return getZ() >= min.getZ() && getZ() <= max.getZ();
    }

    public Position getNorth() {
        return transform(0, 1, 0);
    }

    public Position transform(final int diffX, final int diffY, final int diffZ) {
        return new Position(getX() + diffX, getY() + diffY, getZ() + diffZ);
    }

    public Position getSouth() {
        return transform(0, -1, 0);
    }

    public Position getWest() {
        return transform(-1, 0, 0);
    }

    public Position getNorthEast() {
        return transform(1, 1, 0);
    }

    public Position getSouthEast() {
        return transform(1, -1, 0);
    }

    public Position getEast() {
        return transform(1, 0, 0);
    }

    public Position getNorthWest() {
        return transform(-1, 1, 0);
    }

    public Position getSouthWest() {
        return transform(-1, -1, 0);
    }

    public String toTeleportString() {
        return getY() + "," +
            getMapX() + "," +
            getMapY() + "," +
            getXInMap() + "," +
            getYInMap();
    }

    public int getMapX() {
        return getX() >> 6;
    }

    public int getMapY() {
        return getY() >> 6;
    }

    public int getXInMap() {
        return getX() & 0x3F;
    }

    public int getYInMap() {
        return getY() & 0x3F;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getX(), getY(), getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position1 = (Position) o;
        return getX() == position1.getX()
            && getY() == position1.getY()
            && getZ() == position1.getZ();
    }

    @Override
    public String toString() {
        return "Position{" +
            "x=" + getX() +
            ", y=" + getY() +
            ", z=" + getZ() +
            ", regionId=" + getRegionId() +
            '}';
    }

    public int getRegionId() {
        return (getMapX() << 8) + getMapY();
    }
}
