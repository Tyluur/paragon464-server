package com.paragon464.gameserver.io.database.table.definition;

import com.paragon464.gameserver.api.Api;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ApiKeyTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_api_key")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                Api.API_KEYS.add(result.getString("uuid"));
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the API keys table!", e);
                e = e.getNextException();
            }
        }
    }
}
