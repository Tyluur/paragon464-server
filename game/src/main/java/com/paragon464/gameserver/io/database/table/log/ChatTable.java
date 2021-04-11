package com.paragon464.gameserver.io.database.table.log;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class ChatTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatTable.class);

    public static void save(final Player player, final String message, final boolean privateMessage) {
      /*  ConnectionPool.execute(() -> {
            try (Connection connection = ConnectionPool.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT into paragon_player_log_chat VALUES (?,?,?,?)")) {
                statement.setInt(1, player.getDetails().getUserId());
                statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                statement.setBoolean(3, privateMessage);
                statement.setString(4, message);
                statement.executeUpdate();
            } catch (SQLException e) {
                while (e != null) {
                    LOGGER.error("An error occurred whilst saving a chat log!", e);
                    e = e.getNextException();
                }
            }
        });*/
    }
}
