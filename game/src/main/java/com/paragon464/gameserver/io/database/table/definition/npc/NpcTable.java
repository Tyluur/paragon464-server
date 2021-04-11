package com.paragon464.gameserver.io.database.table.definition.npc;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.npc.NPCDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class NpcTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NpcTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_npc")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final int npc = result.getInt("npc_id");
                final int size = result.getInt("npc_tile_size");
                final int respawnTimer = result.getInt("npc_time_respawn");
                final int combatLevel = result.getInt("npc_level_combat");
                final String name = result.getString("npc_name");
                final String examine = result.getString("npc_examine_text");
                final NPCDefinition definition = new NPCDefinition();

                definition.id = npc;
                definition.name = name;
                definition.examine = examine;
                definition.size = size;
                definition.respawn = respawnTimer;
                definition.combatLevel = combatLevel;
                NPCDefinition.definitions.add(definition);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the npc table!", e);
                e = e.getNextException();
            }
        }
    }
}
