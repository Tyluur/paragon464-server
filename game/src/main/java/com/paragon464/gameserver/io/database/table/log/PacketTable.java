package com.paragon464.gameserver.io.database.table.log;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class PacketTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketTable.class);

    public static void save(Player player, String packetData) {
       /* ConnectionPool.execute(() -> {
            try (Connection connection = ConnectionPool.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT into paragon_player_log_packet VALUES (?,?,?)")) {
                statement.setInt(1, player.getDetails().getUserId());
                statement.setString(2, packetData);
                statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                statement.executeUpdate();
            } catch (SQLException e) {
                while (e != null) {
                    LOGGER.error("An error occurred whilst saving packet log for player {}!", player.getDetails().getName(), e);
                    e = e.getNextException();
                }
            }
        });*/
    }
}
