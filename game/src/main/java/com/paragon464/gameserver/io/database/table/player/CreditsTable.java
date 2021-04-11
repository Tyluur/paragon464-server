package com.paragon464.gameserver.io.database.table.player;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.io.database.table.Table;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditsTable extends Table<Player> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditsTable.class);

    @Override
    public void load(Player player) {
        // TODO: Uncomment when reimplemented.
        /*ConnectionPool.execute(() -> {
            try {
                grabCredits(player);
            } catch (SQLException e) {
                while (e != null) {
                    LOGGER.error("An error occurred whilst loading grabbing credits for player {}!", player.getDetails().getName(), e);
                    e = e.getNextException();
                }
            }
        });*/
    }

    private static void grabCredits(Player player) throws SQLException {
        // TODO: Uncomment when reimplemented.
        /*try (Connection connection = ConnectionPool.getForumPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT bdbank_money AS credits FROM xf_user WHERE user_id = ?")) {
            statement.setInt(1, player.getDetails().getUserId());

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                player.getAttributes().set("credits", resultSet.getInt("credits"));
            }
        }*/
    }

    @Override
    public void save(Player player) throws SQLException, IOException {
    }
}
