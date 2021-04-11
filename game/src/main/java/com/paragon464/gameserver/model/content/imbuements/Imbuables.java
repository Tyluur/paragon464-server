package com.paragon464.gameserver.model.content.imbuements;

import com.google.common.collect.ImmutableSet;
import com.paragon464.gameserver.model.item.Item;

import java.util.Optional;
import java.util.Set;

public enum Imbuables {

    /**
     * Gold Ring (i).
     */
    GOLD_RING(1635, 15009),

    /**
     * Sapphire Ring (i).
     */
    SAPPHIRE_RING(1637, 15010),

    /**
     * Emerald Ring (i).
     */
    EMERALD_RING(1639, 15011),

    /**
     * Ruby Ring (i).
     */
    RUBY_RING(1641, 15012),

    /**
     * Diamond Ring (i).
     */
    DIAMOND_RING(1643, 15013),

    /**
     * Dragonstone Ring (i).
     */
    DRAGONSTONE_RING(1645, 15014),

    /**
     * Onyx Ring (i).
     */
    ONYX_RING(6575, 15017),

    /**
     * Lunar Ring (i).
     */
    LUNAR_RING(9104, 15015),

    /**
     * Ring of Charos (a)(i).
     */
    CHAROS_RING(6465, 15016),

    /**
     * Berserker Ring (i).
     */
    BERSERKER_RING(6737, 15220),

    /**
     * Warrior Ring (i).
     */
    WARRIOR_RING(6735, 15020),

    /**
     * Archers' Ring (i).
     */
    ARCHERS_RING(6731, 15019),

    /**
     * Seers' Ring (i).
     */
    SEERS_RING(6733, 15018);

    private static final Set<Imbuables> imbuables = ImmutableSet.copyOf(values());
    private final int originalId;
    private final int replacementId;

    Imbuables(int originalId, int replacementId) {
        this.originalId = originalId;
        this.replacementId = replacementId;
    }

    public static Optional<Imbuables> imbuableForItems(final Item usedItem, final Item usedWith) {
        return imbuables.stream().filter(imbuable -> imbuable.originalId == usedItem.getId() || imbuable.originalId == usedWith.getId()).findFirst();
    }

    public int getOriginalId() {
        return originalId;
    }

    public int getReplacementId() {
        return replacementId;
    }
}
