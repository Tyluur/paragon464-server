package com.paragon464.gameserver.io.database.table.definition.npc;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCSpawns;
import com.paragon464.gameserver.model.pathfinders.Directions.NormalDirection;
import com.paragon464.gameserver.model.region.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpawnTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpawnTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_spawn_npc")) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                final int npcId = result.getInt("npc_id");
                final int posX = result.getInt("coordinate_x");
                final int posY = result.getInt("coordinate_y");
                final int posZ = result.getInt("coordinate_z");
                final int radius = result.getInt("radius");
                final NormalDirection dir = NormalDirection.forFixedStringValue(result.getString("direction"));
                final Position loc = new Position(posX, posY, posZ);
                final NPCSpawns spawn = new NPCSpawns();

                spawn.id = npcId;
                spawn.x = posX;
                spawn.y = posY;
                spawn.z = posZ;
                spawn.radius = radius;
                spawn.direction = dir;
                NPCSpawns.definitions.add(spawn);

                final NPC npc = new NPC(spawn.id);
                npc.setPosition(loc);
                npc.setSpawnPosition(loc);
                npc.setLastKnownRegion(loc);
                npc.setSpawn(spawn);
                npc.setRandomWalking(radius > 0);
                npc.setDirection(dir.intValue());
                World.getWorld().addNPC(npc);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the NPC spawn table!", e);
                e = e.getNextException();
            }
        }
    }
}
