package com.paragon464.gameserver.model.entity.mob.player;

final class SettingsConstants {

    /**
     * The varp that controls the toggle run button.
     */
    static final int RUN_TOGGLE_VARP = 173;

    /**
     * The maximum amount of run energy a player can have.
     */
    static final double MAX_RUN_ENERGY = 100.0;

    /**
     * The minimum amount of run energy a player can have.
     */
    static final double MIN_RUN_ENERGY = 0.00;

    /**
     * The maximum amount of energy to drain per movement.
     */
    static final double MAX_RUN_ENERGY_DRAIN_RATE = 1.40;

    /**
     * The minimum amount of energy to drain per movement.
     */
    static final double MIN_RUN_ENERGY_DRAIN_RATE = 0.45;

    /**
     * The base amount of run energy to drain per movement.
     */
    static final double BASE_RUN_ENERGY_DRAIN_RATE = 0.70;

    /**
     * The base amount of run energy to restore per tick.
     */
    static final double BASE_RUN_ENERGY_RESTORE_RATE = 0.45;

    /**
     * The amount of energy to drain per kilogram of item weight carried.
     */
    static final double RUN_ENERGY_DRAIN_PER_KG = 0.00729166;

    /**
     * The amount of energy to restore per level of agility.
     */
    static final double RUN_ENERGY_RESTORE_PER_LEVEL = 0.0077;

    /**
     * A default constructor to prevent instantiation.
     */
    private SettingsConstants() {
    }
}
