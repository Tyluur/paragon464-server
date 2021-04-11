package com.paragon464.gameserver.model.entity.mob;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.pathfinders.Directions;
import com.paragon464.gameserver.model.region.MapConstants;
import com.paragon464.gameserver.model.region.Position;

import java.util.Deque;
import java.util.LinkedList;

/**
 * <p>
 * A <code>WalkingQueue</code> stores steps the client needs to walk and allows
 * this queue of steps to be modified.
 * </p>
 * <p>
 * <p>
 * The class will also process these steps when {@link #processMovement()} is
 * called. This should be called once per server cycle.
 * </p>
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class WalkingQueue {

    /**
     * The maximum size of the queue. If there are more points than this size,
     * they are discarded.
     */
    public static final int MAXIMUM_SIZE = 50;
    /**
     * The queue of waypoints.
     */
    public Deque<Point> waypoints = new LinkedList<>();
    /**
     * The mob.
     */
    private Mob mob;
    /**
     * Run toggle (button in client).
     */
    private boolean runToggled = false;
    /**
     * Run for this queue (CTRL-CLICK) toggle.
     */
    private boolean runQueue = false;

    /**
     * Creates the <code>WalkingQueue</code> for the specified
     * <code>Mob</code>.
     *
     * @param mob The mob whose walking queue this is.
     */
    public WalkingQueue(Mob mob) {
        this.mob = mob;
    }

    /**
     * Gets the run toggled flag.
     *
     * @return The run toggled flag.
     */
    public boolean isRunningToggled() {
        return runToggled;
    }

    /**
     * Sets the run toggled flag.
     *
     * @param runToggled The run toggled flag.
     */
    public void setRunningToggled(boolean runToggled) {
        this.runToggled = runToggled;
    }

    /**
     * Gets the running queue flag.
     *
     * @return The running queue flag.
     */
    public boolean isRunningQueue() {
        return runQueue;
    }

    /**
     * Sets the run queue flag.
     *
     * @param runQueue The run queue flag.
     */
    public void setRunningQueue(boolean runQueue) {
        this.runQueue = runQueue;
    }

    /**
     * Checks if any running flag is set.
     *
     * @return <code>true</code. if so, <code>false</code> if not.
     */
    public boolean isRunning() {
        return runToggled || runQueue;
    }

    /**
     * Checks if the queue is empty.
     *
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean isEmpty() {
        return waypoints.isEmpty();
    }

    /**
     * Removes the first waypoint which is only used for calculating directions.
     * This means walking begins at the correct time.
     */
    public void finish() {
        waypoints.removeFirst();
    }

    /**
     * Adds a single step to the walking queue, filling in the points to the
     * previous point in the queue if necessary.
     *
     * @param x The local x coordinate.
     * @param y The local y coordinate.
     */
    public void addStep(int x, int y) {
        /*
         * The RuneScape client will not send all the points in the queue. It
         * just sends places where the direction changes.
         *
         * For instance, walking from a route like this:
         *
         * <code> ***** * * ***** </code>
         *
         * Only the places marked with X will be sent:
         *
         * <code> X***X * * X***X </code>
         *
         * This code will 'fill in' these points and then add them to the queue.
         */

        /*
         * We need to know the previous point to fill in the path.
         */
        if (waypoints.size() == 0) {
            /*
             * There is no last point, reset the queue to add the player's
             * current position.
             */
            reset();
        }

        /*
         * We retrieve the previous point here.
         */
        Point last = waypoints.peekLast();

        /*
         * We now work out the difference between the points.
         */
        int diffX = x - last.x;
        int diffY = y - last.y;

        /*
         * And calculate the number of steps there is between the points.
         */
        int max = Math.max(Math.abs(diffX), Math.abs(diffY));
        for (int i = 0; i < max; i++) {
            /*
             * Keep lowering the differences until they reach 0 - when our route
             * will be complete.
             */
            if (diffX < 0) {
                diffX++;
            } else if (diffX > 0) {
                diffX--;
            }
            if (diffY < 0) {
                diffY++;
            } else if (diffY > 0) {
                diffY--;
            }

            /*
             * Add this next step to the queue.
             */
            addStepInternal(x - diffX, y - diffY);
        }
    }

    /**
     * Resets the walking queue so it contains no more steps.
     */
    public void reset() {
        waypoints.clear();
        waypoints.add(new Point(mob.getPosition().getX(), mob.getPosition().getY(), -1));
    }

    /**
     * Adds a single step to the queue internally without counting gaps. This
     * method is unsafe if used incorrectly so it is private to protect the
     * queue.
     *
     * @param x The x coordinate of the step.
     * @param y The y coordinate of the step.
     */
    private void addStepInternal(int x, int y) {
        /*
         * Check if we are going to violate capacity restrictions.
         */
        if (waypoints.size() >= MAXIMUM_SIZE) {
            /*
             * If we are we'll just skip the point. The player won't get a
             * complete route by large routes are not probable and are more
             * likely sent by bots to crash servers.
             */
            return;
        }

        /*
         * We retrieve the previous point (this is to calculate the direction to
         * move in).
         */
        Point last = waypoints.peekLast();

        /*
         * Now we work out the difference between these steps.
         */
        int diffX = x - last.x;
        int diffY = y - last.y;

        /*
         * And calculate the direction between them.
         */
        int dir = Directions.getMoveDirection(diffX, diffY);

        /*
         * Check if we actually move anywhere.
         */
        if (dir > -1) {
            /*
             * We now have the information to add a point to the queue! We
             * create the actual point object and add it.
             */
            waypoints.add(new Point(x, y, dir));
        }
    }

    /**
     * Processes the next player's movement.
     */
    public void processMovement() {
        /*
         * Store the teleporting flag.
         */
        boolean teleporting = mob.hasTeleportTarget();
        int lastPlane = mob.getPosition().getZ();
        /*
         * The points which we are walking to.
         */
        Point walkPoint = null, runPoint = null;

        /*
         * Checks if the player is teleporting i.e. not walking.
         */
        if (teleporting) {
            /*
             * Reset the walking queue as it will no longer apply after the
             * teleport.
             */
            reset();
            /*
             * Set the 'teleporting' flag which indicates the player is
             * teleporting.
             */
            mob.setTeleporting(true);
            /*
             * Sets the player's new position to be their target.
             */
            mob.setPosition(mob.getTeleportTarget());

            mob.updateCoverage(mob.getTeleportTarget());
            /*
             * Resets the teleport target.
             */
            mob.resetTeleportTarget();
        } else {

            /*
             * If the player isn't teleporting, they are walking (or standing
             * still). We get the next direction of movement here.
             */
            if (!stopWalking()) {
                walkPoint = getNextPoint();
            }

            /*
             * Technically we should check for running here.
             */
            if (runToggled || runQueue) {
                if (!stopRunning()) {
                    runPoint = getNextPoint();
                }
            }

            /*
             * Run energy
             */
            if (runPoint != null) {
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getSettings().decreaseRunEnergy();
                }
            } else {
                if (mob.isPlayer()) {
                    Player p = (Player) mob;
                    p.getSettings().increaseRunEnergy();
                }
            }

            /*
             * Now set the sprites.
             */
            int walkDir = walkPoint == null ? -1 : walkPoint.dir;
            int runDir = runPoint == null ? -1 : runPoint.dir;
            mob.getSprites().setSprites(walkDir, runDir);
        }
        /*
         * Check for a map region change, and if the map region has changed, set
         * the appropriate flag so the new map region packet is sent.
         */
        Position entity_position = mob.getPosition();
        Position last_region = mob.getLastKnownRegion();
        int lastMapRegionX = last_region.getZoneX();
        int lastMapRegionY = last_region.getZoneY();
        int regionX = entity_position.getZoneX();
        int regionY = entity_position.getZoneY();
        int size = (MapConstants.MAP_SIZES.get(mob.getMapSize()) >> 4) - 1;
        boolean changed = Math.abs(lastMapRegionX - regionX) >= size || Math.abs(lastMapRegionY - regionY) >= size;
        World.updateEntityRegion(mob, false);
        if (changed) {
            mob.setMapRegionChanging(true);
        } else if (mob.isPlayer() && lastPlane != mob.getPosition().getZ()) {
            ((Player) mob).setClientHasntLoadedMapRegion();
        }
    }

    public boolean stopWalking() {
        return false;
    }

    /**
     * Gets the next point of movement.
     *
     * @return The next point.
     */
    public Point getNextPoint() {
        if (stopMovement()) {
            return null;
        }
        /*
         * Take the next point from the queue.
         */
        Point p = waypoints.poll();

        /*
         * Checks if there are no more points.
         */
        if (p == null || p.dir == -1) {
            /*
             * Return <code>null</code> indicating no movement happened.
             */
            return null;
        } else {
            /*
             * Set the player's new position.
             */
            int diffX = Directions.MOVEMENT_DIRECTION_DELTA_X[p.dir];
            int diffY = Directions.MOVEMENT_DIRECTION_DELTA_Y[p.dir];
            mob.setPosition(mob.getPosition().transform(diffX, diffY, 0));
            mob.updateCoverage(mob.getPosition().transform(diffX, diffY, 0));
            /*
             * And return the direction.
             */
            return p;
        }
    }

    public boolean stopRunning() {
        return mob.getAttributes().isSet("stopActions");
    }

    public boolean stopMovement() {
        return mob.getAttributes().isSet("resting");
    }

    public boolean isMoving() {
        return mob.getSprites().getPrimarySprite() != -1 || mob.getSprites().getSecondarySprite() != -1;
    }

    /**
     * Represents a single point in the queue.
     *
     * @author Graham Edgecombe <grahamedgecombe@gmail.com>
     */
    private static class Point {

        /**
         * The x-coordinate.
         */
        private final int x;

        /**
         * The y-coordinate.
         */
        private final int y;

        /**
         * The direction to walk to this point.
         */
        private final int dir;

        /**
         * Creates a point.
         *
         * @param x   X coord.
         * @param y   Y coord.
         * @param dir Direction to walk to this point.
         */
        public Point(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }
}
