package com.paragon464.gameserver.io.database.table.definition.map;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.region.Map;
import com.paragon464.gameserver.model.region.Mapdata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class MapDataTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapDataTable.class);

    private static final String TABLE_NAME = "paragon_map_keys";

    public static void load() {
        LOGGER.debug("Loading mapdata.");
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME)) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final int region_id = result.getInt("region_id");
                final int key1 = result.getInt("key_1");
                final int key2 = result.getInt("key_2");
                final int key3 = result.getInt("key_3");
                final int key4 = result.getInt("key_4");
                final Map map = new Map();
                map.region = region_id;
                map.data[0] = key1;
                map.data[1] = key2;
                map.data[2] = key3;
                map.data[3] = key4;
                Mapdata.MAPS.put(region_id, map);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading table: {}", TABLE_NAME, e);
                e = e.getNextException();
            }
        }
    }
}
