package com.paragon464.gameserver.io.database.table.player;

import com.paragon464.gameserver.io.database.PlayerManager;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClansTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClansTable.class);

    public static void load(String owner) {
        ConnectionPool.execute(() -> {
            try {
                try (Connection connection = ConnectionPool.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_player_clan where user_id = ?")) {
                    statement.setInt(1, PlayerManager.getUserId(owner));
                    final ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        final int id = resultSet.getInt("user_id");
                        final String clan_name = resultSet.getString("clan_name");
                        FriendsAndIgnores.EntryRank entryRank = FriendsAndIgnores.EntryRank.forString(resultSet.getString("entry_requirement"));
                        FriendsAndIgnores.TalkRank talkRank = FriendsAndIgnores.TalkRank.forString(resultSet.getString("talk_requirement"));
                        FriendsAndIgnores.KickRank kickRank = FriendsAndIgnores.KickRank.forString(resultSet.getString("kick_requirement"));
                        FriendsAndIgnores list = new FriendsAndIgnores(null);
                        list.entryRank(entryRank, true);
                        list.setTalkRank(talkRank, true);
                        list.setKickRank(kickRank, true);
                        list.channelOwner = owner;
                        list.setChannelName(clan_name, true);
                        list.setFriendsList(PlayerManager.getFriendsList(id));
                        World.getWorld().friendLists.put(list.channelOwner, list);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred whilst loading the clans table!", e);
            }
        });
    }

    public static void load(Player player) {
        ConnectionPool.execute(() -> {
            try {
                try (Connection connection = ConnectionPool.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_player_clan where user_id = ?")) {
                    statement.setInt(1, player.getDetails().getUserId());
                    final ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        final int id = resultSet.getInt("user_id");
                        final String clan_name = resultSet.getString("clan_name");
                        FriendsAndIgnores.EntryRank entryRank = FriendsAndIgnores.EntryRank.forString(resultSet.getString("entry_requirement"));
                        FriendsAndIgnores.TalkRank talkRank = FriendsAndIgnores.TalkRank.forString(resultSet.getString("talk_requirement"));
                        FriendsAndIgnores.KickRank kickRank = FriendsAndIgnores.KickRank.forString(resultSet.getString("kick_requirement"));
                        player.getFriendsAndIgnores().entryRank(entryRank, true);
                        player.getFriendsAndIgnores().setTalkRank(talkRank, true);
                        player.getFriendsAndIgnores().setKickRank(kickRank, true);
                        player.getFriendsAndIgnores().channelOwner = PlayerManager.getUsername(id);
                        player.getFriendsAndIgnores().setChannelName(clan_name, true);
                        World.getWorld().friendLists.put(player.getDetails().getName(), player.getFriendsAndIgnores());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred whilst loading the clans table!", e);
            }
        });
    }

    public static void save(String name) throws SQLException {
        ConnectionPool.execute(() -> {
            try {
                FriendsAndIgnores list = World.getWorld().friendLists.get(name);
                try (Connection connection = ConnectionPool.getPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO paragon_player_clan (user_id, clan_name, entry_requirement, talk_requirement, kick_requirement) " +
                         "VALUES (?, ?, ?::clan_privilege, ?::clan_privilege, ?::clan_privilege) ON CONFLICT (user_id) DO UPDATE SET clan_name = EXCLUDED.clan_name, entry_requirement = EXCLUDED.entry_requirement, talk_requirement = EXCLUDED.talk_requirement, kick_requirement = EXCLUDED.kick_requirement")) {
                    statement.setInt(1, PlayerManager.getUserId(name));
                    statement.setString(2, list.getChannelName());
                    statement.setString(3, FriendsAndIgnores.EntryRank.forId(list.getEntryRank().getId()).name());
                    statement.setString(4, FriendsAndIgnores.TalkRank.forId(list.getTalkRank().getId()).name());
                    statement.setString(5, FriendsAndIgnores.KickRank.forId(list.getKickRank().getId()).name());
                    statement.executeUpdate();
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred whilst saving the clans table!", e);
            }
        });
    }
}
