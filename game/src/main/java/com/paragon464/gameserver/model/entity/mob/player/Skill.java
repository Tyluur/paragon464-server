package com.paragon464.gameserver.model.entity.mob.player;

public class Skill {

    /**
     * The cached version of the skill level prior to any temporary effects.
     */
    private int level = 1;

    /**
     * The skill level after temporary effects.
     */
    private int currentLevel = 1;

    /**
     * The skill experience.
     */
    private double experience = 0;

    /**
     * Gets the amount of experience that a player has in this skill.
     *
     * @return The amount of experience that a player has in this skill.
     */
    double getExperience() {
        return experience;
    }

    /**
     * Sets the amount of experience that a player has in this skill.
     *
     * @param experience The amount of experience to be set.
     */
    void setExperience(final double experience) {
        this.level = Skills.getLevelForExperience(experience);
        this.experience = experience;
    }

    /**
     * Gets the level a player has in this skill.
     *
     * @return The level a player has in this skill.
     */
    int getLevel() {
        return level;
    }

    /**
     * Gets the level of this skill after temporary effects.
     *
     * @return The level of this skill after temporary effects.
     */
    int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Sets the level of this skill after temporary effects.
     *
     * @param currentLevel Sets the level of this skill after temporary effects.
     */
    void setCurrentLevel(final int currentLevel) {
        this.currentLevel = currentLevel;
    }
}
