package com.paragon464.gameserver.io.database.table.definition.item;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.item.ItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SkillRequirementTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillRequirementTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM "
                 + "paragon_definition_combat_requirements_item")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final int skill = SkillType.fromName(result.getString("skill")).ordinal();
                final int level = result.getInt("level");
                final int itemId = result.getInt("id");
                final ItemDefinition itemDefinition = ItemDefinition.definitions.get(itemId);

                itemDefinition.skill_requirements.put(skill, level);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the item combat-requirements table!", e);
                e = e.getNextException();
            }
        }
    }
}
