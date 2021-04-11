package com.paragon464.gameserver.io.database.table.definition.npc;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.npc.NPCSkills;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SkillTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_skill_npc")) {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final int npc = resultSet.getInt("npc_id");
                final int level = resultSet.getInt("npc_skill_level");
                final String skill = resultSet.getString("npc_skill_label");
                final NPCSkills npcSkills;

                if (NPCSkills.forId(npc) != null) {
                    npcSkills = NPCSkills.forId(npc);
                } else {
                    npcSkills = new NPCSkills();
                    npcSkills.setId(npc);
                    NPCSkills.definitions.add(npcSkills);
                }

                if (npcSkills != null) {
                    npcSkills.setLevel(SkillType.fromName(skill).ordinal(), level);
                }
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the NPC skill table!", e);
                e = e.getNextException();
            }
        }
    }
}
