package com.paragon464.gameserver.io.database.table.player;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.io.database.table.Table;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SkillTable extends Table<Player> {

    @Override
    public void load(final Player player) throws SQLException, IOException {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_player_skill WHERE user_id = ?")) {
            statement.setInt(1, player.getDetails().getUserId());

            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                player.getSkills().setSkill(SkillType.fromName(result.getString("label_skill")), result.getInt("level"), result.getDouble("exp"));
            }
        }
    }

    @Override
    public void save(final Player player) throws SQLException, IOException {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO paragon_player_skill (user_id, label_skill, level, exp) VALUES (?, ?, ?, ?) " +
                 "ON CONFLICT (user_id, label_skill) DO UPDATE SET level = EXCLUDED.level, exp = EXCLUDED.exp;")) {
            for (SkillType skillType : player.getSkills().getSkillSet()) {
                statement.setInt(1, player.getDetails().getUserId());
                statement.setString(2, skillType.getDisplayName());
                statement.setInt(3, player.getSkills().getCurrentLevel(skillType));
                statement.setDouble(4, player.getSkills().getExperience(skillType));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
