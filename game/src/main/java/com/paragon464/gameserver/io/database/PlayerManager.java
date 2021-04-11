package com.paragon464.gameserver.io.database;

import com.paragon464.gameserver.api.XfApi;
import com.paragon464.gameserver.api.xenforo.User;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);

    public static int getUserId(String name) {
        val result = XfApi.getBoard().getUser(name).getUser();
        return result.map(User::getId).orElse(-1);
    }

    public static Map<String, FriendsAndIgnores.ClanRank> getFriendsList(int user_id) {
        final Map<String, FriendsAndIgnores.ClanRank> friends = new HashMap<>(200);
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_player_relationship WHERE user_id = ?")) {
            statement.setInt(1, user_id);

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final String username = PlayerManager.getUsername(resultSet.getInt("peer_id"));
                final String relationshipStatus = resultSet.getString("status_relationship");
                FriendsAndIgnores.ClanRank rank = FriendsAndIgnores.ClanRank.forString(relationshipStatus);
                if (!relationshipStatus.equalsIgnoreCase("IGNORED")) {
                    friends.put(username, rank);
                }
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("an error occurred whilst getting friends list for player ID \"{}\"!", user_id, e);
                e = e.getNextException();
            }
        }
        return friends;
    }

    public static String getUsername(int user_id) {
        //val result = XfApi.getBoard().getUser(user_id).getUser(); // temp return random id
        return "Test";//result.map(User::getName).orElse(null);
    }
}
