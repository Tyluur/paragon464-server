package com.paragon464.gameserver.io.database.table.definition.item;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.item.ItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class WeaponTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaponTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM "
                 + "runenova_definition_item_equipment_weapon")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final double specialDrain = result.getDouble("special_energy");
                final int weaponId = result.getInt("id");
                final int interfaceId = result.getInt("id_interface");
                final int interfaceChildId = result.getInt("id_interface_child");
                final int animationWalk = result.getInt("animation_walk");
                final int animationRun = result.getInt("animation_run");
                final int animationBlock = result.getInt("animation_block");
                final int animationStand = result.getInt("animation_stand");
                final int animationAccurate = result.getInt("animation_accurate");
                final int animationAggressive = result.getInt("animation_aggressive");
                final int animationDefensive = result.getInt("animation_defensive");
                final int animationControlled = result.getInt("animation_controlled");
                final int attackSpeedDefensive = result.getInt("speed_defensive");
                final int attackSpeedAggressive = result.getInt("speed_aggressive");
                final int attackSpeedControlled = result.getInt("speed_controlled");
                final int attackSpeedAccurate = result.getInt("speed_accurate");
                final CombatType combatType = CombatType.getType(result.getString("combat_type"));
                final ItemDefinition itemDefinition = ItemDefinition.definitions.get(weaponId);

                itemDefinition.weaponDefinition = new ItemDefinition.WeaponDefinition(interfaceId, interfaceChildId, animationWalk, animationRun, animationBlock, animationStand, specialDrain,
                    animationAccurate, animationAggressive, animationDefensive, animationControlled, attackSpeedAccurate, attackSpeedAggressive, attackSpeedDefensive, attackSpeedControlled, combatType);
            }
        } catch (SQLException e) {
            LOGGER.error("An error occurred whilst loading the weapon table!", e);
        }
    }
}
