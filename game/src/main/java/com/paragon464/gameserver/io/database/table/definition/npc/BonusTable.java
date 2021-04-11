package com.paragon464.gameserver.io.database.table.definition.npc;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.npc.NPCBonuses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BonusTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BonusTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_combat_bonus_npc")) {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final int npc = resultSet.getInt("npc_id");
                final int defensiveStab = resultSet.getInt("npc_bonus_defensive_stab");
                final int defensiveSlash = resultSet.getInt("npc_bonus_defensive_slash");
                final int defensiveCrush = resultSet.getInt("npc_bonus_defensive_crush");
                final int defensiveRanged = resultSet.getInt("npc_bonus_defensive_ranged");
                final int defensiveMagic = resultSet.getInt("npc_bonus_defensive_magic");
                final int offensiveStab = resultSet.getInt("npc_bonus_offensive_stab");
                final int offensiveSlash = resultSet.getInt("npc_bonus_offensive_slash");
                final int offensiveCrush = resultSet.getInt("npc_bonus_offensive_crush");
                final int offensiveMagic = resultSet.getInt("npc_bonus_offensive_magic");
                final int offensiveRanged = resultSet.getInt("npc_bonus_offensive_ranged");
                final int offensiveAttack = resultSet.getInt("npc_bonus_offensive_attack");
                final int offensiveStrength = resultSet.getInt("npc_bonus_offensive_strength");
                final NPCBonuses bonuses = new NPCBonuses(npc);

                bonuses.defensiveStab = defensiveStab;
                bonuses.defensiveSlash = defensiveSlash;
                bonuses.defensiveCrush = defensiveCrush;
                bonuses.defensiveRanged = defensiveRanged;
                bonuses.defensiveMagic = defensiveMagic;
                bonuses.offensiveStab = offensiveStab;
                bonuses.offensiveSlash = offensiveSlash;
                bonuses.offensiveCrush = offensiveCrush;
                bonuses.offensiveMagic = offensiveMagic;
                bonuses.offensiveRanged = offensiveRanged;
                bonuses.offensiveAttack = offensiveAttack;
                bonuses.offensiveStrength = offensiveStrength;
                NPCBonuses.definitions.add(bonuses);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the NPC bonuses table!", e);
                e = e.getNextException();
            }
        }
    }
}
