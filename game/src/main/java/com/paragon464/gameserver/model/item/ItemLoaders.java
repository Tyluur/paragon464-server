package com.paragon464.gameserver.model.item;

import com.paragon464.gameserver.io.database.table.definition.item.AmmoTable;
import com.paragon464.gameserver.io.database.table.definition.item.EquipmentTable;
import com.paragon464.gameserver.io.database.table.definition.item.ItemTable;
import com.paragon464.gameserver.io.database.table.definition.item.RangedWeaponTable;
import com.paragon464.gameserver.io.database.table.definition.item.SkillRequirementTable;
import com.paragon464.gameserver.io.database.table.definition.item.WeaponTable;

public class ItemLoaders {

    /**
     * Initializes all item data.
     */
    public static void init() {
        ItemTable.load();
        EquipmentTable.load();
        WeaponTable.load();
        RangedWeaponTable.load();
        SkillRequirementTable.load();
        AmmoTable.load();
    }
}
