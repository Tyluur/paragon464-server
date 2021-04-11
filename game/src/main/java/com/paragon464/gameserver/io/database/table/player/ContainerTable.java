package com.paragon464.gameserver.io.database.table.player;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.io.database.table.Table;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.Container;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.item.Item;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ContainerTable extends Table<Player> {

    private final Container container = getContainer();
    public final String containerName = container.getContainerName().toString();

    @Override
    public void load(Player player) throws SQLException, IOException {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_player_" + containerName + " WHERE user_id = ?")) {
            statement.setInt(1, player.getDetails().getUserId());

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final int item_id = resultSet.getInt("id");
                final int item_amount = resultSet.getInt("amount");
                final Item item = new Item(item_id, item_amount);

                final int item_slot = containerName.equals("equipment") ? Equipment.slotForString(resultSet.getString("slot")) : resultSet.getInt("slot");
                if (item_id != -1) container.set(item, item_slot, false);
            }
        }
    }

    @Override
    public void save(Player player) throws SQLException, IOException {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO paragon_player_" + containerName + " (user_id, slot, id, amount) " +
                 "VALUES (?, ?, ?, ?) ON CONFLICT (user_id, slot) DO UPDATE SET id = EXCLUDED.id, amount = EXCLUDED.amount")) {
            for (int i = 0; i < container.getSize(); i++) {
                if (container.getContainerName() == Container.ContainerName.EQUIPMENT) {
                    if (i == 6 || i == 8 || i == 11) continue;
                }
                final Item item = container.get(i);
                statement.setInt(1, player.getDetails().getUserId());

                if (containerName.equals("equipment")) {
                    statement.setString(2, Equipment.stringForSlot(i));
                } else {
                    statement.setInt(2, i);
                }
                statement.setInt(3, item != null ? item.getId() : -1);
                statement.setInt(4, item != null ? item.getAmount() : -1);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public abstract Container getContainer();
}
