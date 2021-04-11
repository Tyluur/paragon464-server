package com.paragon464.gameserver.tickable.impl;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.region.MapBuilder;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.tickable.Tickable;

/**
 * A Tick which runs periodically and performs tasks such as garbage collection.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class CleanupTick extends Tickable {

    /**
     * The delay in ticks between consecutive cleanups.
     */
    public static final int CLEANUP_CYCLE_TIME = 500;

    /**
     * Creates the cleanup event to run every 5 minutes.
     */
    public CleanupTick() {
        super(CLEANUP_CYCLE_TIME);
    }

    @Override
    public void execute() {
        boolean force = (Runtime.getRuntime().freeMemory() < Config.FREE_MEMORY_MINIMUM);
        if (force) {
            skip:
            for (Region region : World.getRegions().values()) {
                for (int regionId : MapBuilder.FORCE_LOAD_REGIONS)
                    if (regionId == region.getId())
                        continue skip;
                region.unloadMap();
            }
        }
        System.gc();
        System.runFinalization();
    }
}
