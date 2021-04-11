package com.paragon464.gameserver.io.database.table.definition.item;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.item.ItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class AmmoTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmmoTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM "
                 + "runenova_definition_item_equipment_weapon_ranged_usableammo")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final ItemDefinition itemDefinition = ItemDefinition.definitions.get(result.getInt("id"));
                itemDefinition.rangedDefinition.getAmmoAllowed().add(result.getInt("id_ammo"));
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the usable ammo table!", e);
                e = e.getNextException();
            }
        }
    }
}
