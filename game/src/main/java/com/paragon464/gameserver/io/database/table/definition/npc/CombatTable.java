package com.paragon464.gameserver.io.database.table.definition.npc;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.npc.NPCCombatDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class CombatTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CombatTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_combat_npc")) {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final NPCCombatDefinition definition = new NPCCombatDefinition();
                final int npc = resultSet.getInt("npc_id");
                final int deathTimer = resultSet.getInt("npc_time_death");
                final int animationAttack = resultSet.getInt("animation_attack");
                final int animationDefend = resultSet.getInt("animation_defend");
                final int animationDeath = resultSet.getInt("animation_death");
                final int attackSpeed = resultSet.getInt("npc_speed_attack");
                final int maxHit = resultSet.getInt("npc_max_hit");
                final int poisonMaxHit = resultSet.getInt("npc_max_hit_poison");
                final boolean aggressive = resultSet.getBoolean("npc_trait_aggressive");
                final boolean retreats = resultSet.getBoolean("npc_trait_retreats");
                final boolean poisonImmunity = resultSet.getBoolean("npc_immunity_poison");
                final double slayerExperience = resultSet.getDouble("npc_kill_reward_xp_slayer");
                final int slayerRequirement = resultSet.getInt("player_required_level_slayer");

                definition.id = npc;
                definition.attackAnim = animationAttack;
                definition.defendAnim = animationDefend;
                definition.deathAnim = animationDeath;
                definition.speed = attackSpeed;
                definition.maxHit = maxHit;
                definition.poisonMaxHit = poisonMaxHit;
                definition.aggressive = aggressive;
                definition.retreats = retreats;
                definition.poisonImmuned = poisonImmunity;
                definition.slayerXP = slayerExperience;
                definition.slayerLvl = slayerRequirement;
                definition.dienTime = deathTimer;
                NPCCombatDefinition.definitions.add(definition);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the NPC combat definition table!", e);
                e = e.getNextException();
            }
        }
    }
}
