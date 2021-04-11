package com.paragon464.gameserver.model.entity.mob.player;

import com.google.common.primitives.Ints;
import com.paragon464.gameserver.model.content.ExpCounter;
import com.paragon464.gameserver.model.content.LevelUp;

import java.util.HashMap;
import java.util.Set;

import static com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags.UpdateFlag.APPEARANCE;
import static com.paragon464.gameserver.model.entity.mob.player.SkillConstants.COMBAT_EXP_MULTIPLIER;
import static com.paragon464.gameserver.model.entity.mob.player.SkillConstants.EXPERIENCE_FOR_LEVEL;
import static com.paragon464.gameserver.model.entity.mob.player.SkillConstants.MAXIMUM_EXPERIENCE;
import static com.paragon464.gameserver.model.entity.mob.player.SkillConstants.NON_COMBAT_EXP_MULTIPLIER;

public class Skills {

    /**
     * A hashmap containing all available skills.
     */
    private final HashMap<SkillType, Skill> skills = new HashMap<>();

    /**
     * The player that this class belongs to.
     */
    private final Player player;

    /**
     * The constructor for this class.
     *
     * @param player The player whose skills instance this is.
     */
    public Skills(Player player) {
        SkillType.CACHED_VALUES.forEach(skillType -> skills.put(skillType, new Skill()));
        this.player = player;
    }

    /**
     * Sets a skill for a player based on the specified skill type, experience, and level after all effects have been applied.
     *
     * @param skill        The skill type.
     * @param currentLevel The level after all temporary effects.
     * @param experience   The experience to be set.
     */
    public void setSkill(final SkillType skill, final int currentLevel, final double experience) {
        skills.get(skill).setCurrentLevel(currentLevel);
        skills.get(skill).setExperience(experience);
        player.getFrames().sendSkillLevel(skill);
        player.getUpdateFlags().flag(APPEARANCE);
    }

    /**
     * Gets the total experience of all the skills combined.
     *
     * @return The total experience of all the skills combined.
     */
    public long getTotalExperience() {
        long totalExperience = 0;

        for (SkillType skillType : skills.keySet()) {
            totalExperience += getExperience(skillType);
        }
        return totalExperience;
    }

    /**
     * Gets a skill level based on the amount of specified experience.
     *
     * @param experience The amount of experience to calculate a skill level from.
     * @return A skill level based on the amount of experience specified.
     */

    /**
     * Gets the amount of skill experience in the specified skill.
     *
     * @param skillType The skill whose XP is to be returned.
     * @return The amount of skill experience in the specified skill.
     */
    public double getExperience(final SkillType skillType) {
        return skills.get(skillType).getExperience();
    }

    /**
     * Adds skill experience to a player in the specified skill type.
     *
     * @param skillType  The skill type whose experience is to be adjusted.
     * @param experience The experience to be added.
     */
    public void addExperience(final SkillType skillType, double experience) {
        int multiplier = NON_COMBAT_EXP_MULTIPLIER;
        if (skillType.ordinal() <= SkillType.MAGIC.ordinal())
            multiplier = COMBAT_EXP_MULTIPLIER;
        multiplier *= player.getAttributes().getInt("xp_multiplier");
        experience *= multiplier;
        ExpCounter.showGainedExp(player, skillType.ordinal(), experience);
        setExperience(skillType, getExperience(skillType) + experience);
    }

    /**
     * Sets a player's experience in the specified skill type.
     *
     * @param skill      The skill type whose experience is to be adjusted.
     * @param experience The experience to be set.
     */
    public void setExperience(final SkillType skill, double experience) {
        final int currentLevel = getCurrentLevel(skill);
        final int newLevel = getLevelForExperience(experience);
        final int level = getLevel(skill);

        if (experience > MAXIMUM_EXPERIENCE)
            experience = MAXIMUM_EXPERIENCE;
        skills.get(skill).setExperience(experience);
        if (newLevel > level) {
            if (currentLevel < newLevel) {
                incrementCurrentLevel(skill, Math.abs(newLevel - level));
            }
            LevelUp.send(player, skill);
        } else if (newLevel < level) {
            setCurrentLevel(skill, newLevel);
        }

        player.getFrames().sendSkillLevel(skill);
        player.getUpdateFlags().flag(APPEARANCE);
    }

    /**
     * Gets the skill level after all temporary effects have been applied.
     *
     * @param skillType The skill whose level is to be returned.
     * @return The specified skill's level post-buffs/debuffs.
     */
    public int getCurrentLevel(final SkillType skillType) {
        return skills.get(skillType).getCurrentLevel();
    }

    /**
     * Gets the correct skill level for the specified amount of experience.
     *
     * @param experience The experience to use when calculating the level.
     * @return The correct skill level for the specified amount of experience.
     */
    public static int getLevelForExperience(final double experience) {
        for (int level = 1; level < 99; level++) {
            if (experience < EXPERIENCE_FOR_LEVEL[level + 1]) {
                return level;
            }
        }
        return 99;
    }

    /**
     * Gets the cached base-level ('Level For Experience' or level pre-buffs/debuffs).
     *
     * @param skillType The skill whose level is to be returned.
     * @return The specified skill's level prior to any temporary effects.
     */
    public int getLevel(final SkillType skillType) {
        return skills.get(skillType).getLevel();
    }

    /**
     * Increments the specified skill's temporary level. This is used for temporary buffs.
     *
     * @param skillType The skill whose temporary level is to be affected.
     * @param amount    The amount to increment by
     */
    public void incrementCurrentLevel(final SkillType skillType, final int amount) {
        setCurrentLevel(skillType, getCurrentLevel(skillType) + amount);
    }

    /**
     * Sets the specified skill's temporary level. This is used for temporary buffs/debuffs.
     *
     * @param skillType The skill whose temporary level is to be affected.
     * @param level     The temporary level that is to be set.
     */
    public void setCurrentLevel(final SkillType skillType, int level) {
        if (level < 0) {
            level = 0;
        }

        if (skillType == SkillType.PRAYER) {
            player.getSettings().setPrayerPoints(level);
        }

        skills.get(skillType).setCurrentLevel(level);
        player.getFrames().sendSkillLevel(skillType);
    }

    /**
     * Gets the total level of all the skills combined.
     *
     * @return The total level of all the skills combined.
     */
    public int getTotalLevel() {
        int totalLevel = 0;

        for (SkillType skillType : skills.keySet()) {
            totalLevel += getLevel(skillType);
        }
        return totalLevel;
    }

    /**
     * Increments the specified skill's temporary level by one. This is used for temporary buffs.
     *
     * @param skillType The skill whose temporary level is to be affected.
     */
    public void incrementCurrentLevel(final SkillType skillType) {
        incrementCurrentLevel(skillType, 1);
    }

    /**
     * Decrements the specified skill's temporary level by one. This used for temporary debuffs.
     *
     * @param skillType The skill whose temporary level is to be affected.
     */
    public void decrementCurrentLevel(final SkillType skillType) {
        decrementCurrentLevel(skillType, 1);
    }

    /**
     * Decrements the specified skill's temporary level. This used for temporary debuffs.
     *
     * @param skillType The skill whose temporary level is to be affected.
     * @param amount    The amount to decrement by.
     */
    public void decrementCurrentLevel(final SkillType skillType, final int amount) {
        setCurrentLevel(skillType, getCurrentLevel(skillType) - amount);
    }

    /**
     * Determines whether or not a skill is currently boosted past normal levels.
     *
     * @param skillType The skill identifier whose levels are to be checked.
     * @return Whether or not the specified skill is currently buffed.
     */
    public boolean isBuffed(final SkillType skillType) {
        return getCurrentLevel(skillType) > getLevel(skillType);
    }

    /**
     * Determines whether or not a skill is currently below normal levels.
     *
     * @param skillType The skill identifier whose levels are to be checked.
     * @return Whether or not the specified skill is currently buffed.
     */
    public boolean isDebuffed(final SkillType skillType) {
        return getCurrentLevel(skillType) > getLevel(skillType);
    }

    /**
     * Calculates the overall combat level of a player based on their combat related stats.
     *
     * @return The overall combat level, up to 126, of a player.
     */
    public int getCombatLevel() {
        int attackLevel = skills.get(SkillType.ATTACK).getLevel();
        int defenceLevel = skills.get(SkillType.DEFENCE).getLevel();
        int hitpointsLevel = skills.get(SkillType.HITPOINTS).getLevel();
        int magicLevel = skills.get(SkillType.MAGIC).getLevel();
        int prayerLevel = skills.get(SkillType.PRAYER).getLevel();
        int rangedLevel = skills.get(SkillType.RANGED).getLevel();
        int strengthLevel = skills.get(SkillType.STRENGTH).getLevel();
        double base = Ints.max(strengthLevel + attackLevel, (int) (magicLevel * 1.5), (int) (rangedLevel * 1.5));
        double combat = ((base * 1.3) + defenceLevel + hitpointsLevel + (prayerLevel / 2)) / 4;

        return (int) combat;
    }

    /**
     * Resets all temporary skill effects for all skills.
     */
    public void resetEffects() {
        getSkillSet().forEach(this::resetEffects);
    }

    /**
     * A set containing all the available skill types.
     *
     * @return The available skill types.
     */
    public Set<SkillType> getSkillSet() {
        return skills.keySet();
    }

    /**
     * Resets all temporary skill effects for a specified skill.
     *
     * @param skillType The skill whose effects are to be reset.
     */
    public void resetEffects(final SkillType skillType) {
        setCurrentLevel(skillType, getLevel(skillType));
    }

    /**
     * Resets all skill levels and skill experience back to their defaults.
     */
    public void resetSkills() {
        getSkillSet().forEach(this::resetSkill);
    }

    /**
     * Resets the skill level and skill experience of a specified skill back to its defaults.
     *
     * @param skillType The skill to be reset.
     */
    public void resetSkill(final SkillType skillType) {
        setLevel(skillType, 0);
        resetEffects(skillType);
    }

    /**
     * Sets the base-level (Level prior to any temporary effects) for the specified skill.
     * <p>
     * !! WARNING !! this also affects experience. !! WARNING !!
     * Use "setCurrentLevel()" if you only wish to adjust the temporary level.
     *
     * @param skillType The skill whose levels and experience are to be affected.
     * @param level     The level that is to be set.
     */
    public void setLevel(final SkillType skillType, int level) {
        if (level < 1) {
            level = 1;
        }
        setExperience(skillType, getExperienceForLevel(level));
    }

    /**
     * Gets the amount of experience required for a level.
     *
     * @param level The level whose required experience is to be gotten.
     * @return The required experience for the specified level.
     */
    public static double getExperienceForLevel(final int level) {
        return EXPERIENCE_FOR_LEVEL[level];
    }
}
