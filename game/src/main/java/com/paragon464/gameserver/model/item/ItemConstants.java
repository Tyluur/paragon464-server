package com.paragon464.gameserver.model.item;

public final class ItemConstants {

    /**
     * The default item examination message.
     */
    public static final String DEFAULT_EXAMINE_MESSAGE = "That item is: %s.";

    /**
     * The maximum amount of items in a single stack.
     */
    public static final int MAX_ITEMS = 999_999_999;

    /**
     * The maximum usable item ID.
     */
    public static final int MAX_ITEM_ID = 14_939;

    /**
     * The maximum item value percentage. This is just to get checkstyle to not scream about magic numbers.
     */
    static final double MAX_VALUE_PERCENTAGE = 100.0;

    /**
     * The percentage of value that high alchemy should give per cast.
     */
    static final int HIGH_ALCH_PERCENTAGE = 60;

    /**
     * The percentage of value that low alchemy should give per cast.
     */
    static final int LOW_ALCH_PERCENTAGE = 40;

    /**
     * A default constructor to prevent instantiation.
     */
    private ItemConstants() {
    }
}
