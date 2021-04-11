package com.paragon464.gameserver.model.entity.mob.player;

final class SkillConstants {

    /**
     * The maximum amount of experience obtainable in a single skill.
     */
    static final double MAXIMUM_EXPERIENCE = 200_000_000.00;

    /**
     * The experience multiplier that is applied to all non-combat skills.
     */
    static final int NON_COMBAT_EXP_MULTIPLIER = 45;

    /**
     * The experience multiplier that is applied to all combat skills.
     * This means all combat-level affecting skills, so prayer is included, but slayer is NOT.
     */
    static final int COMBAT_EXP_MULTIPLIER = 400;

    /**
     * The amount of experience required for each level.
     */
    static final double[] EXPERIENCE_FOR_LEVEL = new double[100];

    /**
     * Initializes {@code EXPERIENCE_FOR_LEVEL} array.
     */
    static {
        double points = 0, output = 0;
        for (int level = 1; level <= 99; level++) {
            EXPERIENCE_FOR_LEVEL[level] = output;
            points += Math.floor(level + 300 * Math.pow(2, level / 7.0));
            output = Math.floor(points / 4);
        }
    }

    /**
     * Preventing instantiation.
     */
    private SkillConstants() {
    }
}
