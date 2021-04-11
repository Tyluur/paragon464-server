package com.paragon464.gameserver.model.content.itemsets;

import com.paragon464.gameserver.model.item.Item;

enum ItemSet {

    /**
     * Dwarf Cannon Set.
     */
    CANNON(11967, new Item[]{new Item(6, 1), new Item(8, 1),
        new Item(10, 1), new Item(12, 1)}),

    /**
     * Bronze Armour Set - Platelegs.
     */
    BRONZE(11814, new Item[]{new Item(1155, 1), new Item(1117, 1),
        new Item(1075, 1), new Item(1189, 1)}),

    /**
     * Iron Armour Set - Platelegs.
     */
    IRON(11818, new Item[]{new Item(1153, 1), new Item(1115, 1),
        new Item(1067, 1), new Item(1191, 1)}),

    /**
     * Steel Armour Set - Platelegs.
     */
    STEEL(11822, new Item[]{new Item(1157, 1), new Item(1119, 1),
        new Item(1069, 1), new Item(1193, 1)}),

    /**
     * Black Armour Set - Platelegs.
     */
    BLACK(11826, new Item[]{new Item(1165, 1), new Item(1125, 1),
        new Item(1077, 1), new Item(1195, 1)}),

    /**
     * Mithril Armour Set - Platelegs.
     */
    MITHRIL(11830, new Item[]{new Item(1159, 1), new Item(1121, 1),
        new Item(1071, 1), new Item(1197, 1)}),

    /**
     * Initiate Armour Set - Platelegs.
     */
    INITIATE_HARNESS(9668, new Item[]{new Item(5574, 1), new Item(5575, 1),
        new Item(5576, 1)}),

    /**
     * Adamant Armour Set - Platelegs.
     */
    ADAMANT(11834, new Item[]{new Item(1161, 1), new Item(1123, 1),
        new Item(1073, 1), new Item(1199, 1)}),

    /**
     * Proselyte Armour Set - Platelegs.
     */
    PROSELYTE_HARNESS(9666, new Item[]{new Item(9672, 1), new Item(9674, 1),
        new Item(9676, 1)}),

    /**
     * Proselyte Armour Set - Tassets (Plateskirt).
     */
    PROSELYTE_HARNESS_SK(9670, new Item[]{new Item(9672, 1), new Item(9674, 1),
        new Item(9678, 1)}),

    /**
     * Rune Armour Set - Platelegs.
     */
    RUNE(11838, new Item[]{new Item(1163, 1), new Item(1127, 1),
        new Item(1079, 1), new Item(1201, 1)}),

    /**
     * Rockshell Armour Set.
     */
    ROCKSHELL(11942, new Item[]{new Item(6128, 1), new Item(6129, 1),
        new Item(6130, 1)}),

    /**
     * Dragon Armour Set - Platelegs, chainbody.
     */
    DRAGON(11842, new Item[]{new Item(11335, 1), new Item(3140, 1),
        new Item(4087, 1), new Item(1187, 1)}),

    /**
     * Dragon Armour Set - Plateskirt, chainbody.
     */
    DRAGON_SK(11844, new Item[]{new Item(11335, 1), new Item(3140, 1),
        new Item(4585, 1), new Item(1187, 1)}),

    /**
     * Dragon Armour Set - Platelegs, platebody.
     */
    DRAGON_PLATEBODY(14529, new Item[]{new Item(11335, 1), new Item(14479, 1),
        new Item(4087, 1), new Item(1187, 1)}),

    /**
     * Dragon Armour Set - Plateskirt, platebody.
     */
    DRAGON_PLATEBODY_SK(14531, new Item[]{new Item(11335, 1), new Item(14479, 1),
        new Item(4585, 1), new Item(1187, 1)}),

    /**
     * Splitbark Armour Set.
     */
    SPLITBARK(11876, new Item[]{new Item(3385, 1), new Item(3387, 1),
        new Item(3389, 1)}),

    /**
     * Blue Mystic Robe Set.
     */
    BLUE_MYSTIC(11872, new Item[]{new Item(4089, 1), new Item(4091, 1),
        new Item(4093, 1), new Item(4095, 1), new Item(4097, 1)}),

    /**
     * Dagon'hai Robe Set.
     */
    DAGONHAI(14525, new Item[]{new Item(14499, 1), new Item(14497, 1),
        new Item(144501, 1)}),

    /**
     * Green Dragonhide Set.
     */
    GREEN_DHIDE(11864, new Item[]{new Item(1135, 1), new Item(1099, 1),
        new Item(1065, 1)}),

    /**
     * Blue Dragonhide Set.
     */
    BLUE_DHIDE(11866, new Item[]{new Item(2499, 1), new Item(2493, 1),
        new Item(2487, 1)}),

    /**
     * Red Dragonhide Set.
     */
    RED_DHIDE(11868, new Item[]{new Item(2501, 1), new Item(2495, 1),
        new Item(2489, 1)}),

    /**
     * Black Dragonhide Set.
     */
    BLACK_DHIDE(11870, new Item[]{new Item(2503, 1), new Item(2497, 1),
        new Item(2491, 1)});

    /**
     * The Item ID of the set container.
     */
    private final int id;

    /**
     * The set contents.
     */
    private final Item[] items;

    /**
     * An openable item set.
     *
     * @param setId    The item ID of the set container.
     * @param contents The contents of the set.
     */
    ItemSet(final int setId, final Item[] contents) {
        this.id = setId;
        this.items = contents;
    }

    /**
     * Gets the item ID of the set.
     *
     * @return The ID of the set container.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the contents of a set.
     *
     * @return The items that are to be unpacked upon opening the container.
     */
    public Item[] getItems() {
        return items;
    }
}
