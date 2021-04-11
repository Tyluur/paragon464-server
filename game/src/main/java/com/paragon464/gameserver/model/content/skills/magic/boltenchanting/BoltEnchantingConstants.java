package com.paragon464.gameserver.model.content.skills.magic.boltenchanting;

final class BoltEnchantingConstants {

    /**
     * The message that appears when you don't have enough bolts.
     */
    static final String NOT_ENOUGH_BOLTS = "You need at least %d %s to use this spell.";

    /**
     * The message the appears when you don't have enough space to enchant the bolts.
     */
    static final String NOT_ENOUGH_SPACE = "You need at least %d free slots to use this spell.";

    /**
     * The message that appears when your current magic level is too low to cast the spell.
     */
    static final String MAGIC_LEVEL_TOO_LOW = "You need a magic level of at least %d to enchant these bolts.";

    /**
     * The amount of bolts that are enchanted per cast.
     */
    static final int BOLT_AMOUNT = 10;

    /**
     * Bolt enchantment graphical effect.
     */
    static final int ENCHANT_GFX = 759;

    /**
     * Bolt enchantment animation.
     */
    static final int ENCHANT_ANIMATION = 4462;

    /**
     * A default constructor to prevent instantiation.
     */
    private BoltEnchantingConstants() {
    }
}
