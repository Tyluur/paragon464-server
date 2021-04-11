package com.paragon464.gameserver.tickable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 'Mystic Flow
 */
public abstract class Tick {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tick.class);

    private int time;

    private int attempts;
    private boolean running = true;

    public Tick(int time) {
        this.time = time;
        this.attempts = 0;
    }

    public boolean run() {
        if (!running) {
            return false;
        }
        if (++attempts >= time) {
            attempts = 0;
            try {
                execute();
            } catch (Throwable e) {
                LOGGER.error("An error occurred whilst executing a tick!", e);
            }
        }
        return running;
    }

    public abstract void execute();

    public boolean isRunning() {
        return running;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int cycles) {
        this.time = cycles;
    }

    public void stop() {
        running = false;
    }

    public void start() {
        running = true;
    }
}
