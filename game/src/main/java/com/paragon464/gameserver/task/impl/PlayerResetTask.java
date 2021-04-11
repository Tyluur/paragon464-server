package com.paragon464.gameserver.task.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;

/**
 * A task which resets a player after an update cycle.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class PlayerResetTask implements Runnable {

    private Player player;

    public PlayerResetTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            player.resetHits();
            player.getUpdateFlags().reset();
            if (!player.clientHasLoadedMapRegion() || player.isMapRegionChanging()) {
                player.getVariables().refreshSpawnedObjects();
                player.getVariables().refreshSpawnedItems();
            }
            if (player.isTeleporting()) {
                player.resetTeleportTarget();
                player.setTeleporting(false);
            }
            if (player.isMapRegionChanging()) {
                player.setMapRegionChanging(false);
            }
            if (!player.clientHasLoadedMapRegion()) {
                player.setClientHasLoadedMapRegion();
            }
            player.resetCachedUpdateBlock();
            player.reset();
            player.getVariables().setNextForceMovement(null);
        } catch (Exception e) {
            World.getWorld().handleError(e, player);
        }
    }
}
