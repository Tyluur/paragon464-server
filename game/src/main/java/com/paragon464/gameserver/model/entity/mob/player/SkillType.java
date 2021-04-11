package com.paragon464.gameserver.model.entity.mob.player;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum SkillType {
    ATTACK("Attack"),
    DEFENCE("Defence"),
    STRENGTH("Strength"),
    HITPOINTS("Hitpoints"),
    RANGED("Ranged"),
    PRAYER("Prayer"),
    MAGIC("Magic"),
    COOKING("Cooking"),
    WOODCUTTING("Woodcutting"),
    FLETCHING("Fletching"),
    FISHING("Fishing"),
    FIREMAKING("Firemaking"),
    CRAFTING("Crafting"),
    SMITHING("Smithing"),
    MINING("Mining"),
    HERBLORE("Herblore"),
    AGILITY("Agility"),
    THIEVING("Thieving"),
    SLAYER("Slayer"),
    FARMING("Farming"),
    RUNECRAFTING("Runecrafting"),
    HUNTER("Hunter"),
    CONSTRUCTION("Construction"),
    SUMMONING("Summoning");

    public static List<SkillType> CACHED_VALUES = ImmutableList.copyOf(values());
    private final String displayName;

    SkillType(String displayName) {
        this.displayName = displayName;
    }

    public static SkillType fromId(final int id) {
        return CACHED_VALUES.get(id);
    }

    public static SkillType fromName(final String name) {
        return SkillType.valueOf(name.toUpperCase());
    }

    public String getDisplayName() {
        return displayName;
    }
}
