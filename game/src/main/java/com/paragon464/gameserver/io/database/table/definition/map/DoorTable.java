package com.paragon464.gameserver.io.database.table.definition.map;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.gameobjects.Door;
import com.paragon464.gameserver.model.gameobjects.DoorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DoorTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoorTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_object_doors")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final int facingDirection = result.getInt("face");
                final int doorId = result.getInt("door_id");
                final int posX = result.getInt("pos_x");
                final int posY = result.getInt("pos_y");
                final int posZ = result.getInt("pos_z");
                final int type = result.getInt("type");
                final boolean open = result.getBoolean("open");
                final boolean doubleDoor = result.getBoolean("double_door");
                final Door door = new Door(doorId, posX, posY, posZ, facingDirection, type, open);

                door.setDoorType(doubleDoor ? DoorManager.DoorType.DOUBLE : DoorManager.DoorType.SINGLE);
                DoorManager.doors.add(door);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the door table!", e);
                e = e.getNextException();
            }
        }
    }
}
