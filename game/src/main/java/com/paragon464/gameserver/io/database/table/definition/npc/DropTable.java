package com.paragon464.gameserver.io.database.table.definition.npc;

import com.google.common.collect.Sets;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.npc.drops.DropItem;
import com.paragon464.gameserver.model.entity.mob.npc.drops.NPCDrop;
import com.paragon464.gameserver.model.entity.mob.npc.drops.NPCDrops;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public final class DropTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropTable.class);

    public static void load(final int i, NPCDrop main) {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_drop_npc WHERE id_npc = ?")) {
            statement.setInt(1, i);
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                int id = result.getInt("id_npc");
                int item = result.getInt("id_item");
                double chance = result.getDouble("chance");
                int min = result.getInt("amount_minimum");
                int max = result.getInt("amount_maximum");
                boolean rare = result.getBoolean("rare");
                main.getUnique().add(new DropItem(item, min, max, chance, rare));
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the drop table for NPC ID {}!", i, e);
                e = e.getNextException();
            }
        }
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_drop_common_npc WHERE id_npc = ?")) {
            statement.setInt(1, i);
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                String generic_table_name = result.getString("table_type");
                double chance = result.getDouble("chance");
                main.getGeneric().put(generic_table_name, chance);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the common table for NPC ID {}!", i, e);
                e = e.getNextException();
            }
        }
        if (main.getUnique().size() > 0) {
            NPCDrops.definitions.put(i, main);
        }
    }

    public static void genericTables() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_drop_common ORDER BY name ASC")) {
            final ResultSet result = statement.executeQuery();
            String last_table = null;
            Set<DropItem> genericTableItems = Sets.newHashSet();
            while (result.next()) {
                String table_name = result.getString("name");
                if (!table_name.equalsIgnoreCase(last_table) && last_table != null) {
                    NPCDrops.generics.put(last_table, genericTableItems);
                    genericTableItems = Sets.newHashSet();
                }
                int item = result.getInt("id_item");
                double chance = result.getDouble("chance");
                int min = result.getInt("amount_minimum");
                int max = result.getInt("amount_maximum");
                boolean rare = result.getBoolean("rare");
                DropItem dItem = new DropItem(item, min, max, chance, rare);
                genericTableItems.add(dItem);
                last_table = table_name;
            }
            if (genericTableItems.size() > 0) {//last item
                NPCDrops.generics.put(last_table, genericTableItems);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occured whilst loading the generic drop tables!", e);
                e = e.getNextException();
            }
        }
    }
}
