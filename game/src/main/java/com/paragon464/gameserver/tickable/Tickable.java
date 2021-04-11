package com.paragon464.gameserver.tickable;

import com.paragon464.gameserver.model.entity.mob.Mob;

/**
 * Represents a task that is executed in the future, once or periodically. Each
 * "tick" represents 0.6ms, or one game loop.
 *
 * @author Michael Bull <mikebull94@gmail.com>
 */
public abstract class Tickable {

    /*
     * What is the difference between remainingTicks and tickDelay?
     * ============================================================
     *
     * tickDelay = the delay in ticks between consecutive executions. e.g. a
     * combat event may run at 3 ticks, which mean each 3*600 ms it is executed
     *
     * remainingTicks = the number of ticks until this tickable next runs. e.g.
     * the tick delay will be 1 if it'll be ran in the next cycle.
     */

    /**
     * does this tickable belong to an mob.
     */
    protected Mob owner;

    /**
     * The amount of ticks before this event executes.
     */
    private int remainingTicks;

    /**
     * The ticks to reset to once executed if the tickable still runs.
     */
    private int tickDelay;

    /**
     * A flag which indicates if this task should be executed once immediately.
     */
    private boolean immediate;

    /**
     * The running flag.
     */
    private boolean running = true;

    /**
     * Creates a tickable with the specified amount of ticks.
     *
     * @param ticks The amount of ticks.
     */
    public Tickable(int ticks) {
        this.remainingTicks = ticks;
        this.tickDelay = ticks;
        this.immediate = false;
    }

    public Tickable(Mob owner, int ticks) {
        this.owner = owner;
        this.remainingTicks = ticks;
        this.tickDelay = ticks;
    }

    public Tickable(boolean immediate) {
        this(1, immediate);
    }

    public Tickable(int ticks, boolean immediate) {
        this.tickDelay = ticks;
        this.remainingTicks = ticks;
        this.immediate = immediate;
    }

    public Tickable(Mob owner, int ticks, boolean immediate) {
        this.owner = owner;
        this.tickDelay = ticks;
        this.remainingTicks = ticks;
        this.immediate = immediate;
    }

    /**
     * Gets the tick delay.
     *
     * @return The delay, in ticks.
     */
    public int getTickDelay() {
        return tickDelay;
    }

    /**
     * Sets the tick delay.
     *
     * @param ticks The amount of ticks to set.
     * @throws IllegalArgumentException if the delay is negative.
     */
    public void setTickDelay(int ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Tick amount must be positive.");
        }
        this.tickDelay = ticks;
    }

    /**
     * Gets the remaining ticks.
     *
     * @return The remaining ticks.
     */
    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void setRemainingTicks(int ticks) {
        if (ticks <= 0) {
            ticks = 1;
            // throw new IllegalArgumentException("Tick amount must be
            // positive.");
        }
        this.remainingTicks = ticks;
    }

    public Mob getOwner() {
        return this.owner;
    }

    public void setOwner(Mob mob) {
        this.owner = mob;
    }

    /**
     * Checks if the tick is running.
     *
     * @return <code>true</code> if the tick is still running,
     * <code>false</code> if not.
     */
    public boolean isRunning() {
        return running;
    }

    public void run() {
        running = true;
    }

    /**
     * This is executed every cycle and handles the counters and such.
     */
    public void cycle() {
        if (owner != null) {
            if (owner.isDestroyed()) {
                stop();
                return;
            }
        }
        if (running && remainingTicks-- == 0) {
            execute();
            remainingTicks = tickDelay;
        }
    }

    /**
     * Stops the tick from running in the future.
     */
    public void stop() {
        running = false;
    }

    /**
     * The enchant method is called when the tick is run. The general contract
     * of the enchant method is that it may take any action whatsoever.
     */
    public abstract void execute();

    public boolean isTickBeforeExecute() {
        int remaining = remainingTicks;
        return remaining-- == 1;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }
}
