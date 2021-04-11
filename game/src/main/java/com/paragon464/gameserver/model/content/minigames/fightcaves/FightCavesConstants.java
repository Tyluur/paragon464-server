package com.paragon464.gameserver.model.content.minigames.fightcaves;

import com.paragon464.gameserver.model.item.Item;

public final class FightCavesConstants {

    /**
     * The tokkul item ID.
     */
    static final int TOKKUL_ID = 6529;
    /**
     * The tokkul reward wave modifier.
     */
    static final double TOKKUL_MODIFIER = 2.0237;
    /**
     * The fire cape item ID.
     */
    private static final int FIRE_CAPE_ID = 6570;
    /**
     * The fire cape rewarded for completion of the fight caves.
     */
    static final Item FIRE_CAPE = new Item(FIRE_CAPE_ID);

    /**
     * A default constructor to prevent instantiation.
     */
    private FightCavesConstants() {
    }
}
