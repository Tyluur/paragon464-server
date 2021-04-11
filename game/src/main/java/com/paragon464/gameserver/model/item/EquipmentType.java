package com.paragon464.gameserver.model.item;

import com.google.common.collect.ImmutableSet;

public enum EquipmentType {

    TWO_HANDED_WEAPON,

    FULL_BODY,

    FULL_MASK,

    MASK,

    MED_HELM,

    STANDARD;

    private static final ImmutableSet<EquipmentType> EQUIPMENT_TYPES = ImmutableSet.copyOf(values());

    public static EquipmentType getType(final String type) {
        return getEquipmentTypes().stream().filter(equipmentType -> equipmentType.name().equals(type.toUpperCase())).findFirst().orElse(null);
    }

    public static ImmutableSet<EquipmentType> getEquipmentTypes() {
        return EQUIPMENT_TYPES;
    }
}
