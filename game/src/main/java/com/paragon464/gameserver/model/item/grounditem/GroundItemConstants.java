package com.paragon464.gameserver.model.item.grounditem;

final class GroundItemConstants {

    /**
     * The amount of ticks until an untradable is removed from the game.
     */
    static final int UNTRADABLE_ITEM_DISSAPEAR_TIMER = 300;

    /**
     * The amount of ticks—not including the additional public timer—until a standard, tradable item is removed from the game.
     */
    static final int STANDARD_ITEM_DISAPPEAR_TIMER = 250;

    /**
     * The amount of ticks until a standard, tradable item becomes visible to all players in the region.
     */
    static final int STANDARD_ITEM_PUBLIC_TIMER = 100;

    /**
     * The amount of ticks until an unowned item becomes visible to all players in the region.
     */
    static final int UNOWNED_ITEM_PUBLIC_TIMER = 0;

    /**
     * A default constructor to prevent instantiation.
     */
    private GroundItemConstants() {
    }
}
