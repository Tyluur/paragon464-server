package com.paragon464.gameserver.task;

public abstract class WorldTask implements Runnable {

    protected boolean needRemove;

    public final void stop() {
        needRemove = true;
    }
}

