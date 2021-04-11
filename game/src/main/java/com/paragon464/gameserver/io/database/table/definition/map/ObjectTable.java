package com.paragon464.gameserver.io.database.table.definition.map;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.region.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ObjectTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectTable.class);

    public static List<GameObject> deletedObjects = new ArrayList<>();
    //TODO - better way?

    public static GameObject getDeleted(GameObject check, int region) {
        for (GameObject obj : deletedObjects) {
            if (check.getId() == obj.getId()) {
                if (check.getPosition().equals(obj.getPosition())) {
                    return obj;
                }
            }
        }
        return null;
    }

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_spawn_object")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final boolean deleteObject = result.getBoolean("delete");
                final int facingDirection = result.getInt("face");
                final int objectId = result.getInt("id_object");
                final int type = result.getInt("type");
                final int posX = result.getInt("pos_x");
                final int posY = result.getInt("pos_y");
                final int posZ = result.getInt("pos_z");
                final Position position = new Position(posX, posY, posZ);
                final GameObject object = new GameObject(position, objectId, type, facingDirection);
                if (deleteObject) {
                    deletedObjects.add(object);
                } else {
                    World.spawnObject(object);
                }
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the object spawn table!", e);
                e = e.getNextException();
            }
        }
    }
}
