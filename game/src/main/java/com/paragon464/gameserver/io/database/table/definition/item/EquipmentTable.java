package com.paragon464.gameserver.io.database.table.definition.item;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.item.EquipmentSlot;
import com.paragon464.gameserver.model.item.EquipmentType;
import com.paragon464.gameserver.model.item.ItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class EquipmentTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM runenova_definition_item_equipment")) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                final double magicDamage = result.getDouble("magic_damage");
                final double absorbMelee = result.getDouble("absorb_melee");
                final double absorbRanged = result.getDouble("absorb_ranged");
                final double absorbMagic = result.getDouble("absorb_magic");
                final int item = result.getInt("id");
                final int prayer = result.getInt("prayer");
                final int defensiveStab = result.getInt("defensive_stab");
                final int defensiveSlash = result.getInt("defensive_slash");
                final int defensiveCrush = result.getInt("defensive_crush");
                final int defensiveRanged = result.getInt("defensive_ranged");
                final int defensiveMagic = result.getInt("defensive_magic");
                final int defensiveSummoning = result.getInt("defensive_summoning");
                final int offensiveStab = result.getInt("offensive_stab");
                final int offensiveSlash = result.getInt("offensive_slash");
                final int offensiveCrush = result.getInt("offensive_crush");
                final int offensiveRanged = result.getInt("offensive_ranged");
                final int offensiveMagic = result.getInt("offensive_magic");
                final int offensiveStrength = result.getInt("strength_melee");
                final int offensiveRangedStrength = result.getInt("strength_ranged");
                final EquipmentSlot slot = EquipmentSlot.getSlot(result.getString("equipment_slot"));
                final EquipmentType type = EquipmentType.getType(result.getString("equipment_type"));

                final ItemDefinition itemDefinition = ItemDefinition.definitions.get(item);
                itemDefinition.equipmentDefinition = new ItemDefinition.EquipmentDefinition(defensiveStab,
                    defensiveSlash, defensiveCrush, defensiveRanged, defensiveMagic, defensiveSummoning,
                    absorbMelee, absorbRanged, absorbMagic, magicDamage, offensiveStab, offensiveSlash,
                    offensiveCrush, offensiveRanged, offensiveMagic, offensiveStrength, offensiveRangedStrength,
                    prayer, slot, type);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the equipment definition table!", e);
                e = e.getNextException();
            }
        }
    }
}
