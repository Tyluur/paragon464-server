package com.paragon464.gameserver.io.database.table.definition.item;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.item.ItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ItemTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM runenova_definition_item")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final boolean stackable = result.getBoolean("stackable");
                final boolean droppable = result.getBoolean("droppable");
                final boolean tradable = result.getBoolean("tradable");
                final boolean members = result.getBoolean("members_only");
                final int noteTemplate = result.getInt("id_template_note");
                final int lendTemplate = result.getInt("id_template_lend");
                final int noteId = result.getInt("id_note");
                final int lendId = result.getInt("id_lend");
                final int itemId = result.getInt("id");
                final int value = result.getInt("value");
                final String name = result.getString("name");
                final String examine = result.getString("examine");
                final double weight = result.getDouble("weight");
                ItemDefinition.definitions.put(itemId, new ItemDefinition(name, examine, itemId, noteId, noteTemplate,
                    lendId, lendTemplate, droppable, tradable, stackable, members, value, weight));
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the item definition table!", e);
                e = e.getNextException();
            }
        }
    }
}
