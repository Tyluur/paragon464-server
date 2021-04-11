package com.paragon464.gameserver.model.entity.mob;

import com.paragon464.gameserver.model.pathfinders.Directions;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;
import com.paragon464.gameserver.model.region.Position;

import java.awt.*;

/**
 * @author Lazaro <lazaro@ziotic.com>
 */
public class Coverage {

    private Point lowerBound;
    private Point upperBound;
    private int z;
    private int size;

    public Coverage(final Position loc, int size) {
        this.lowerBound = new Point(loc.getX(), loc.getY());
        this.upperBound = new Point(lowerBound.x + size - 1, lowerBound.y + size - 1);
        this.z = loc.getZ();
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getLowerBoundX() {
        return lowerBound.x;
    }

    public int getLowerBoundY() {
        return lowerBound.y;
    }

    public int getUpperBoundX() {
        return upperBound.x;
    }

    public int getUpperBoundY() {
        return upperBound.y;
    }

    public void update(Directions.NormalDirection direction, int size) {
        this.size = size;
        int dx = Directions.DIRECTION_DELTA_X[direction.intValue()];
        int dy = Directions.DIRECTION_DELTA_Y[direction.intValue()];
        lowerBound.setLocation(lowerBound.x + dx, lowerBound.y + dy);
        upperBound.setLocation(upperBound.x + dx, upperBound.y + dy);
    }

    public void update(final Position loc, int size) {
        this.lowerBound = new Point(loc.getX(), loc.getY());
        this.upperBound = new Point(lowerBound.x + size - 1, lowerBound.y + size - 1);
        this.size = size;
    }

    public boolean within(final Position t) {
        return t.getX() >= lowerBound.x && t.getX() <= upperBound.x && t.getY() >= lowerBound.y
            && t.getY() <= upperBound.y;
    }

    @SuppressWarnings("incomplete-switch")
    public boolean correctCombatPosition(Mob mob, Mob partner, Coverage c, int distance, CombatType type) {
        if (intersect(c)) {
            return false;
        }
        switch (type) {
            case MELEE:
                if (size == 1 && c.size == 1) {
                    int absDX = Math.abs(lowerBound.x - c.lowerBound.x);
                    int absDY = Math.abs(lowerBound.y - c.lowerBound.y);
                    return (absDX == 0 && absDY == 1) || (absDX == 1 && absDY == 0);
                } else {
                    return touch(c);
                }
            case MAGIC:
            case RANGED:
                return center().getDistanceFrom(c.center()) <= distance
                    && ProjectilePathFinder.hasLineOfSight(mob, partner);
        }
        return false;
    }

    public boolean intersect(Coverage c) {
        return !right(c) && !left(c) && !above(c) && !under(c);
    }

    public boolean touch(Coverage c) {
        if (!intersect(c)) {
            if (right(c)) {
                if (above(c)) {
                    return c.lowerBound.x + c.size == lowerBound.x && c.lowerBound.y + c.size == lowerBound.y;
                } else if (under(c)) {
                    return c.lowerBound.x + c.size == lowerBound.x && c.lowerBound.y - 1 == lowerBound.y;
                } else {
                    return c.lowerBound.x + c.size == lowerBound.x;
                }
            } else if (left(c)) {
                if (above(c)) {
                    return lowerBound.x + size == c.lowerBound.x && lowerBound.y == c.lowerBound.y + c.size;
                } else if (under(c)) {
                    return lowerBound.x + size == c.lowerBound.x && lowerBound.y == c.lowerBound.y - 1;
                } else {
                    return lowerBound.x + size == c.lowerBound.x;
                }
            } else {
                if (above(c)) {
                    return lowerBound.y - 1 == c.upperBound.y;
                } else if (under(c)) {
                    return c.lowerBound.y - 1 == upperBound.y;
                }
            }
        }
        return false;
    }

    public Position center() {
        return new Position(lowerBound.x + (int) Math.floor(size / 2), lowerBound.y + (int) Math.floor(size / 2), z);
    }

    public boolean right(Coverage c) {
        return lowerBound.x > c.upperBound.x;
    }

    public boolean left(Coverage c) {
        return upperBound.x < c.lowerBound.x;
    }

    public boolean above(Coverage c) {
        return lowerBound.y > c.upperBound.y;
    }

    public boolean under(Coverage c) {
        return upperBound.y < c.lowerBound.y;
    }

    public boolean correctFinalFollowPosition(Coverage c) {
        return touch(c);
    }
}
