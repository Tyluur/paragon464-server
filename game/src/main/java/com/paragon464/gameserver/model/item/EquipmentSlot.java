package com.paragon464.gameserver.model.item;

import com.google.common.collect.ImmutableSet;

public enum EquipmentSlot {

    /**
     * A placeholder slot.
     */
    NONE(-1),

    /**
     * Typically used for helmets.
     */
    HEAD(0),

    /**
     * Typically used for necklaces and amulets.
     */
    NECK(2),

    /**
     * Typically used for plate armor, dragonhide, and robe tops.
     */
    TORSO(4),

    /**
     * Typically used for capes.
     */
    BACK(1),

    /**
     * Typically used for weapons.
     */
    MAIN_HAND(3),

    /**
     * Typically used for shields.
     */
    OFF_HAND(5),

    /**
     * Typically used for gloves and bracelets.
     */
    HANDS(9),

    /**
     * Typically used for rings.
     */
    FINGERS(12),

    /**
     * Typically used for platelegs, chaps, and robe bottoms.
     */
    LEGS(7),

    /**
     * Typically used for boots and other shoes.
     */
    FEET(10),

    /**
     * Typically used for bolts and arrows.
     */
    AMMUNITION(13),

    /**
     * Used for equipped auras.
     */
    AURA(14);

    /**
     * A set containing all available equipment slots. This should typically be used over "values()".
     */
    private static final ImmutableSet<EquipmentSlot> EQUIPMENT_SLOTS = ImmutableSet.copyOf(values());
    /**
     * The slot identifier that gets sent to the client.
     */
    private final int slotId;

    /**
     * Represents an equipment slot.
     *
     * @param slot The slot ID that identifies this slot in the client.
     */
    EquipmentSlot(final int slot) {
        this.slotId = slot;
    }

    /**
     * Gets the first slot that matches the specified slot identifier.
     *
     * @param slot The slot identifier.
     * @return The first matching slot for the provided ID.
     */
    public static EquipmentSlot getSlot(final int slot) {
        return getEquipmentSlots().stream().filter(equipmentSlot -> equipmentSlot.getSlotId() == slot).findFirst().orElse(null);
    }

    /**
     * Gets a set containing all available equipment slots. This should typically be used over "values()".
     *
     * @return A set containing all available equipment slots.
     */
    public static ImmutableSet<EquipmentSlot> getEquipmentSlots() {
        return EQUIPMENT_SLOTS;
    }

    /**
     * Returns the identifying number for the slot specified.
     *
     * @return The identifying number for this slot.
     */
    public int getSlotId() {
        return slotId;
    }

    /**
     * Gets the first slot that matches the specified slot identifier.
     *
     * @param slot The slot identifier.
     * @return The first matching slot for the provided ID.
     */
    public static EquipmentSlot getSlot(final String slot) {
        return getEquipmentSlots().stream().filter(equipmentSlot -> equipmentSlot.name().equals(slot.toUpperCase())).findFirst().orElse(null);
    }
}
