package com.paragon464.gameserver.model.content.skills.magic.boltenchanting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.paragon464.gameserver.model.item.Item;

import java.util.stream.Collectors;
import java.util.stream.Stream;

enum Bolts {

    /**
     * Opal bolts (e).
     */
    OPAL(93, 879, 9236, 4, 9.0, ImmutableSet.of(new Item(564, 1), new Item(556, 2))),

    /**
     * Jade bolts (e).
     */
    JADE(98, 9335, 9237, 14, 19.0, ImmutableSet.of(new Item(564, 1), new Item(557, 2))),

    /**
     * Pearl bolts (e).
     */
    PEARL(103, 880, 9238, 24, 29.0, ImmutableSet.of(new Item(564, 1), new Item(555, 2))),

    /**
     * Topaz bolts (e).
     */
    TOPAZ(107, 9336, 9239, 29, 33.0, ImmutableSet.of(new Item(564, 1), new Item(554, 2))),

    /**
     * Sapphire bolts (e).
     */
    SAPPHIRE(111, 9337, 9240, 7, 17.5, ImmutableSet.of(new Item(564, 1), new Item(558, 1), new Item(555, 1))),

    /**
     * Emerald bolts (e).
     */
    EMERALD(115, 9338, 9241, 27, 37.0, ImmutableSet.of(new Item(561, 1), new Item(564, 1), new Item(556, 3))),

    /**
     * Ruby bolts (e).
     */
    RUBY(119, 9339, 9242, 49, 59.0, ImmutableSet.of(new Item(565, 1), new Item(564, 1), new Item(554, 5))),

    /**
     * Diamond bolts (e).
     */
    DIAMOND(123, 9340, 9243, 57, 67.0, ImmutableSet.of(new Item(563, 2), new Item(564, 1), new Item(557, 10))),

    /**
     * Dragon bolts (e).
     */
    DRAGON(127, 9341, 9244, 68, 78.0, ImmutableSet.of(new Item(566, 1), new Item(564, 1), new Item(557, 15))),

    /**
     * Onyx bolts (e).
     */
    ONYX(131, 9342, 9245, 87, 97.0, ImmutableSet.of(new Item(560, 1), new Item(564, 1), new Item(554, 20)));

    /**
     * The map of bolts. Key is the corresponding button ID and value is the bolt.
     */
    private static final ImmutableMap<Integer, Bolts> BOLTS = ImmutableMap.copyOf(Stream.of(values())
        .collect(Collectors.toMap(Bolts::getButtonId, bolts -> bolts)));
    /**
     * The interface button that corresponds to this particular bolt.
     */
    private final int buttonId;
    /**
     * The ID of the bolt prior to enchantment.
     */
    private final int unenchantedBoltId;
    /**
     * The ID of the bolt post-enchantment.
     */
    private final int enchantedBoltId;
    /**
     * The magic level required to enchant the bolts.
     */
    private final int magicLevelRequired;
    /**
     * The amount of experience granted for enchanting the bolts.
     */
    private final double experienceGranted;
    /**
     * The rune(s) required to enchant the bolts.
     */
    private final ImmutableSet<Item> requiredRunes;

    /**
     * An enchantable bolt.
     *
     * @param button        The interface button ID that corresponds to the bolt.
     * @param boltId        The ID of the bolt prior to enchantment.
     * @param enchantedId   The ID of the bolt post-enchantment.
     * @param levelRequired The magic level required to enchant the bolts.
     * @param exp           The amount of experience granted upon enchantment.
     * @param runes         The rune(s) required to enchant the bolt.
     */
    Bolts(final int button, final int boltId, final int enchantedId, final int levelRequired, final double exp, final ImmutableSet<Item> runes) {
        this.buttonId = button;
        this.unenchantedBoltId = boltId;
        this.enchantedBoltId = enchantedId;
        this.magicLevelRequired = levelRequired;
        this.experienceGranted = exp;
        this.requiredRunes = runes;
    }

    /**
     * @return The map of bolts. Key is the corresponding button ID and value is the bolt.
     */
    public static ImmutableMap<Integer, Bolts> getBolts() {
        return BOLTS;
    }

    /**
     * @return The interface button ID that corresponds to the bolt.
     */
    public int getButtonId() {
        return buttonId;
    }

    /**
     * @return The item ID of the bolt prior to enchantment.
     */
    public int getUnenchantedBoltId() {
        return unenchantedBoltId;
    }

    /**
     * @return The item ID of the bolt post-enchantment.
     */
    public int getEnchantedBoltId() {
        return enchantedBoltId;
    }

    /**
     * @return The magic level required to enchant the bolt.
     */
    public int getMagicLevelRequired() {
        return magicLevelRequired;
    }

    /**
     * @return The amount of experience granted upon enchantment.
     */
    public double getExperienceGranted() {
        return experienceGranted;
    }

    /**
     * @return The rune(s) required to enchant the bolt.
     */
    public ImmutableSet<Item> getRequiredRunes() {
        return requiredRunes;
    }
}
