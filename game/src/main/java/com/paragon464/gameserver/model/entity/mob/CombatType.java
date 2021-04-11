package com.paragon464.gameserver.model.entity.mob;

import com.google.common.collect.ImmutableSet;

public enum CombatType {

    MELEE,

    RANGED,

    MAGIC,

    FIRE_BREATH,

    SPECIAL;

    private static final ImmutableSet<CombatType> COMBAT_TYPES = ImmutableSet.copyOf(values());

    public static CombatType getType(final String type) {
        return getCombatTypes().stream().filter(combatType -> combatType.name().equals(type.toUpperCase())).findFirst().orElse(null);
    }

    public static ImmutableSet<CombatType> getCombatTypes() {
        return COMBAT_TYPES;
    }
}
