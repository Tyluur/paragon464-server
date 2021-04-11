package com.paragon464.gameserver.io.database.table.definition.map;

import com.google.common.collect.ImmutableSet;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.content.skills.Loaders;
import com.paragon464.gameserver.model.content.skills.magic.Teleport;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeleportsTable {

    private static final Logger logger = LoggerFactory.getLogger(TeleportsTable.class);

    private TeleportsTable() {

    }

    public static void load() {
        final Map<Integer, Item> runeRequirements = new HashMap<>();

        final String table1 = "runenova_definition_teleport_rune";
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table1)) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                runeRequirements.put(result.getInt("teleport_id"),
                    new Item(result.getInt("item_id"), result.getInt("item_amount")));
            }
        } catch (SQLException e) {
            while (e != null) {
                logger.error("An error occurred whilst loading table: {}", table1, e);
                e = e.getNextException();
            }
        }

        final String table2 = "runenova_definition_teleport";
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table2)) {
            final List<Teleport> teleports = new ArrayList<>();
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final var id = result.getInt("id");
                final var level = result.getInt("required_level");
                final var experience = result.getDouble("experience");
                final var location = new Position(result.getInt("x"), result.getInt("y"),
                    result.getInt("z"));
                final var runes = ImmutableSet.copyOf(runeRequirements.entrySet().stream()
                    .filter(entry -> entry.getKey() == id).map(Map.Entry::getValue).collect(Collectors.toSet()));
                teleports.add(new Teleport(level, experience, location, runes));
            }
            Loaders.Teleports.teleports = teleports;
        } catch (SQLException e) {
            while (e != null) {
                logger.error("An error occurred whilst loading table: {}", table2, e);
                e = e.getNextException();
            }
        }
    }
}
