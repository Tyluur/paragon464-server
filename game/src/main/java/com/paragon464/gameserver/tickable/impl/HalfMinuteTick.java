package com.paragon464.gameserver.tickable.impl;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.tickable.Tickable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HalfMinuteTick extends Tickable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HalfMinuteTick.class);

    public HalfMinuteTick() {
        super(50);
    }

    @Override
    public void execute() {
       /* ConnectionPool.execute(() -> {
            try (Connection connection = ConnectionPool.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT 1;")) {
                statement.executeQuery();
            } catch (SQLException e) {
                while (e != null) {
                    LOGGER.error("An error occurred whilst pinging the game database!", e);
                    e = e.getNextException();
                }
            }
        });*/
        for (final Player player : World.getWorld().getPlayers()) {
            if (player == null)
                continue;
            player.getSettings().increaseSpecialAmount(10);
        }
    }
}
