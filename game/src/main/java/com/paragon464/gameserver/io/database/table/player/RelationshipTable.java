package com.paragon464.gameserver.io.database.table.player;

import com.paragon464.gameserver.io.database.PlayerManager;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.io.database.table.Table;
import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public final class RelationshipTable extends Table<Player> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipTable.class);

    public static void deleteRelation(Player player, String peer) {
        ConnectionPool.execute(() -> {
            try (Connection connection = ConnectionPool.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM paragon_player_relationship WHERE user_id = ? AND peer_id = ?")) {
                final int user_id = PlayerManager.getUserId(peer);
                statement.setInt(1, player.getDetails().getUserId());
                statement.setInt(2, user_id);
                statement.executeUpdate();
            } catch (SQLException e) {
                while (e != null) {
                    LOGGER.error("An error occurred whilst deleting relation for {}:{}!", player.getDetails().getName(), peer, e);
                    e = e.getNextException();
                }
            }
        });
    }

    @Override
    public void load(Player player) throws SQLException, IOException {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_player_relationship WHERE user_id = ?")) {
            statement.setInt(1, player.getDetails().getUserId());

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final String username = PlayerManager.getUsername(resultSet.getInt("peer_id"));
                final String relationshipStatus = resultSet.getString("status_relationship");
                FriendsAndIgnores.ClanRank rank = FriendsAndIgnores.ClanRank.forString(relationshipStatus);
                if (relationshipStatus.equalsIgnoreCase("IGNORED")) {
                    player.getFriendsAndIgnores().getIgnoresList().add(username);
                } else {
                    player.getFriendsAndIgnores().getFriendsList().put(username, rank);
                }
            }
            ClansTable.load(player);
            player.getFriendsAndIgnores().refresh();
        }
    }

    @Override
    public void save(Player player) throws SQLException, IOException {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO paragon_player_relationship (user_id, peer_id, status_relationship) " +
                 "VALUES (?, ?, ?::relationship_type) ON CONFLICT (user_id, peer_id) DO UPDATE SET status_relationship = EXCLUDED.status_relationship")) {
            for (Map.Entry<String, FriendsAndIgnores.ClanRank> relation : player.getFriendsAndIgnores().getFriendsList().entrySet()) {
                final int friend_id = PlayerManager.getUserId(relation.getKey());
                if (friend_id != -1) {
                    statement.setInt(1, player.getDetails().getUserId());
                    statement.setInt(2, friend_id);
                    statement.setString(3, relation.getValue().name());
                    statement.addBatch();
                }
            }

            for (String relation : player.getFriendsAndIgnores().getIgnoresList()) {
                final int friend_id = PlayerManager.getUserId(relation);
                if (friend_id != -1) {
                    statement.setInt(1, player.getDetails().getUserId());
                    statement.setInt(2, friend_id);
                    statement.setString(3, "Ignored");
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        }
    }
}
