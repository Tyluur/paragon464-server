package com.paragon464.gameserver.model.content.bonds;

import com.google.common.collect.ImmutableMap;

final class BondConstants {

    /**
     * The message that gets displayed when you redeem a bond.
     */
    static final String REDEMPTION_MESSAGE = "%,d credits have successfully been added to your account.";

    /**
     * A set containing all bonds and their values.
     */
    static final ImmutableMap<Integer, Integer> BONDS = ImmutableMap.of(18635, 1000, 6542, 500);

    /**
     * A default constructor to prevent instantiation.
     */
    private BondConstants() {
    }
}
