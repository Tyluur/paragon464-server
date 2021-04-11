package com.paragon464.gameserver.model.content.itemsets;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;

import java.util.HashMap;
import java.util.Map;

public final class ItemSets {

    /**
     * A map containing all of the available item sets.
     */
    private static final Map<Integer, ItemSet> ITEM_SETS = new HashMap<>();

    static {
        for (final ItemSet types : ItemSet.values()) {
            ITEM_SETS.put(types.getId(), types);
        }
    }

    /**
     * A default constructor to prevent instantiation.
     */
    private ItemSets() {
    }

    /**
     * Opens an item set.
     *
     * @param player The player who is performing the action.
     * @param item   The item that the player is attempting to open.
     * @return {@code true} if the set was opened. Otherwise, {@code false}.
     */
    public static boolean open(final Player player, final Item item) {
        final ItemSet set = ITEM_SETS.get(item.getId());

        if (set == null) {
            return false;
        }

        final int invSlots = set.getItems().length;
        final boolean acceptable = player.getInventory().freeSlots() >= invSlots - 1;

        if (!acceptable) {
            player.getFrames().sendMessage("You need " + invSlots + " available inventory slots to open this.");
            return false;
        }

        player.getInventory().deleteItem(item);
        for (Item items : set.getItems()) {
            player.getInventory().addItem(items);
        }

        player.getInventory().refresh();
        return true;
    }
}
