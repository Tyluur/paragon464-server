package com.paragon464.gameserver.io.database.table.log;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.player.Trade;
import com.paragon464.gameserver.model.entity.mob.player.TradeType;
import com.paragon464.gameserver.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class TradeTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeTable.class);

    public static void save(TradeType tradeType, final Trade trade) {
        /*ConnectionPool.execute(() -> {
            try (Connection connection = ConnectionPool.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT into paragon_player_log_" + tradeType.toString() + " VALUES (?,?,?,?,?)")) {
                statement.setInt(1, trade.getSender().getDetails().getUserId());// user_id
                statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                statement.setString(3, TextUtils.getItemLogDisplay(trade.getSentItems()));
                statement.setString(4, TextUtils.getItemLogDisplay(trade.getReceivedItems()));
                statement.setString(5, trade.getReceipient().getDetails().getName());
                statement.executeUpdate();
            } catch (SQLException e) {
                while (e != null) {
                    LOGGER.error("An error occurred whilst saving the trade log for trade: {}", trade.toString(), e);
                    e = e.getNextException();
                }
            }
        });*/
    }
}
