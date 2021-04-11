package com.paragon464.gameserver.io.database.table.definition.item;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.item.ItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class RangedWeaponTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RangedWeaponTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM "
                 + "runenova_definition_item_equipment_weapon_ranged")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final boolean usesAmmo = result.getBoolean("uses_ammo");
                final int projectile = result.getInt("id_projectile");
                final int drawGraphic = result.getInt("id_animation");
                final int item = result.getInt("id");
                final ItemDefinition itemDefinition = ItemDefinition.definitions.get(item);

                itemDefinition.rangedDefinition = new ItemDefinition.RangedDefinition(projectile, drawGraphic, usesAmmo);
            }
        } catch (SQLException e) {
            LOGGER.error("An error occurred whilst loading the ranged weapon table!", e);
        }
    }
}
