package com.paragon464.gameserver.model.item.grounditem;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.tickable.Tickable;

import static com.paragon464.gameserver.model.item.ItemConstants.MAX_ITEMS;
import static com.paragon464.gameserver.model.item.grounditem.GroundItemConstants.STANDARD_ITEM_DISAPPEAR_TIMER;
import static com.paragon464.gameserver.model.item.grounditem.GroundItemConstants.STANDARD_ITEM_PUBLIC_TIMER;
import static com.paragon464.gameserver.model.item.grounditem.GroundItemConstants.UNOWNED_ITEM_PUBLIC_TIMER;
import static com.paragon464.gameserver.model.item.grounditem.GroundItemConstants.UNTRADABLE_ITEM_DISSAPEAR_TIMER;

public final class GroundItemManager {

    /**
     * A default constructor to prevent instantiation.
     */
    private GroundItemManager() {
    }

    /**
     * Adds a ground item to the region in which the ground item is located.
     *
     * @param groundItem The ground item that is to be added to the game.
     */
    public static void registerGroundItem(final GroundItem groundItem) {
        final boolean tradable = groundItem.getDefinition().isTradable();
        final Player player = groundItem.getOwner().orElse(null);

        if (player == null && !tradable) {
            return;
        }

        if (groundItem.getDefinition().isStackable() && player != null) {
            final GroundItem existingItem = World.getRegion(groundItem).getGroundItem(groundItem.getId(),
                groundItem.getPosition(), player);
            if (existingItem != null /*&& !existingItem.isPubliclyVisible() */ && existingItem.getAmount() + groundItem.getAmount() <= MAX_ITEMS) {
                final GroundItem newItem = new GroundItem(existingItem.getId(), existingItem.getAmount() + groundItem.getAmount(),
                    existingItem.getOwner().orElse(null), existingItem.getPosition(), existingItem.isVisible());
                World.getRegion(groundItem).replaceGroundItem(existingItem, newItem);
                player.getFrames().clearGroundItem(existingItem);
                player.getFrames().sendGroundItem(existingItem);
                return;
            }
        }

        int stageTimer;
        if (tradable) {
            stageTimer = STANDARD_ITEM_PUBLIC_TIMER;
        } else {
            stageTimer = UNTRADABLE_ITEM_DISSAPEAR_TIMER;
        }

        if (player == null) {
            stageTimer = UNOWNED_ITEM_PUBLIC_TIMER;
        } else {
            player.getFrames().sendGroundItem(groundItem);
        }

        World.getRegion(groundItem).getGroundItems().add(groundItem);
        World.getWorld().submit(new Tickable(stageTimer) {
            @Override
            public void execute() {
                if (World.getRegion(groundItem).getGroundItems().contains(groundItem)) {
                    if (tradable) {
                        publiclyDisplayGroundItem(groundItem);
                    } else {
                        removeGroundItem(groundItem);
                    }
                }
                this.stop();
            }
        });
    }

    /**
     * Displays a ground item to all players in its region.
     *
     * @param groundItem The ground item to be displayed.
     */
    private static void publiclyDisplayGroundItem(final GroundItem groundItem) {
        groundItem.setVisible(true);
        for (Player player : World.getSurroundingPlayers(groundItem.getPosition())) {
            if (player == null || player == groundItem.getOwner().orElse(null)) {
                continue;
            }

            player.getFrames().sendGroundItem(groundItem);
        }

        World.getWorld().submit(new Tickable(STANDARD_ITEM_DISAPPEAR_TIMER) {
            @Override
            public void execute() {
                if (World.getRegion(groundItem).getGroundItems().contains(groundItem)) {
                    removeGroundItem(groundItem);
                }
            }
        });
    }

    /**
     * Removes a ground item from a region and clears it from player view.
     *
     * @param groundItem The ground item to be removed.
     */
    private static void removeGroundItem(final GroundItem groundItem) {
        World.getRegion(groundItem).getGroundItems().remove(groundItem);
        for (Player player : World.getSurroundingPlayers(groundItem.getPosition())) {
            if (player == null) {
                continue;
            }
            player.getFrames().clearGroundItem(groundItem);
        }
    }

    /**
     * Attempts to pick up a ground item from the floor.
     *
     * @param groundItem The ground item that is being picked up.
     * @param player     The player that is picking up the ground item.
     */
    public static void pickupGroundItem(final GroundItem groundItem, final Player player) {
        if (!World.getRegion(groundItem).getGroundItems().contains(groundItem)) {
            return;
        }

        if (player.getInventory().addItem(groundItem)) {
            player.getInventory().refresh();
            removeGroundItem(groundItem);
        }
    }
}
