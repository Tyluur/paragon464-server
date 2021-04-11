package com.paragon464.gameserver.model.pathfinders;

import com.paragon464.gameserver.model.region.Position;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Boobs
 */
public class PathState {

    public static final int NO_ROUTE_NEEDED = 0;
    public static final int ROUTE_CANT_BE_REACHED = 1;
    public static final int ROUTE_INCOMPLETE = 2;
    public static final int ROUTE_FOUND = 3;

    private Deque<Position> points;

    private Position dest;
    private int state = 0;
    private boolean reached = true;

    public PathState(int state) {
        this(state, null, null);
    }

    public PathState(int state, Deque<Position> points, Position dest) {
        this.state = state;
        this.points = points;
        this.dest = dest;
    }

    public PathState(Position dest) {
        this.points = new LinkedBlockingDeque<>();
        this.points.offer(dest);
        this.state = ROUTE_FOUND;
        this.dest = dest;
    }

    public Position getDest() {
        return this.dest;
    }

    public void setDest(Position dest) {
        this.dest = dest;
    }

    public Deque<Position> getPoints() {
        return this.points;
    }

    public void setPoints(Deque<Position> points) {
        this.points = points;
    }

    public void routeFailed() {
        this.state = ROUTE_CANT_BE_REACHED;
        this.reached = false;
    }

    public void routeIncomplete() {
        this.state = ROUTE_INCOMPLETE;
    }

    public void routeFound() {
        this.state = ROUTE_FOUND;
    }

    public boolean isRouteFound() {
        return state != ROUTE_CANT_BE_REACHED;
    }

    public boolean isRouteIncomplete() {
        return state == ROUTE_INCOMPLETE;
    }

    public boolean hasReached() {
        return reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }
}
