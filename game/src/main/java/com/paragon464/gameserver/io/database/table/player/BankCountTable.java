package com.paragon464.gameserver.io.database.table.player;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.io.database.table.Table;
import com.paragon464.gameserver.model.entity.mob.player.Player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankCountTable extends Table<Player> {

    @Override
    public void load(Player player) throws SQLException, IOException {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_player_bank_count WHERE user_id = ?")) {
            statement.setInt(1, player.getDetails().getUserId());

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int tab_1 = resultSet.getInt("tab_1");
                int tab_2 = resultSet.getInt("tab_2");
                int tab_3 = resultSet.getInt("tab_3");
                int tab_4 = resultSet.getInt("tab_4");
                int tab_5 = resultSet.getInt("tab_5");
                int tab_6 = resultSet.getInt("tab_6");
                int tab_7 = resultSet.getInt("tab_7");
                int tab_8 = resultSet.getInt("tab_8");
                int tab_9 = resultSet.getInt("tab_9");
                player.getVariables().tab1items = tab_1;
                player.getVariables().tab2items = tab_2;
                player.getVariables().tab3items = tab_3;
                player.getVariables().tab4items = tab_4;
                player.getVariables().tab5items = tab_5;
                player.getVariables().tab6items = tab_6;
                player.getVariables().tab7items = tab_7;
                player.getVariables().tab8items = tab_8;
                player.getVariables().tab9items = tab_9;
            }
        }
    }

    @Override
    public void save(Player player) throws SQLException, IOException {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO paragon_player_bank_count (user_id, tab_1, tab_2, tab_3, tab_4, tab_5, tab_6, tab_7, tab_8, tab_9) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET tab_1 = EXCLUDED.tab_1, tab_2 = EXCLUDED.tab_2, tab_3 = EXCLUDED.tab_3, tab_4 = EXCLUDED.tab_4, tab_5 = EXCLUDED.tab_5, tab_6 = EXCLUDED.tab_6, tab_7 = EXCLUDED.tab_7, tab_8 = EXCLUDED.tab_8, tab_9 = EXCLUDED.tab_9")) {
            statement.setInt(1, player.getDetails().getUserId());
            statement.setInt(2, player.getVariables().tab1items);
            statement.setInt(3, player.getVariables().tab2items);
            statement.setInt(4, player.getVariables().tab3items);
            statement.setInt(5, player.getVariables().tab4items);
            statement.setInt(6, player.getVariables().tab5items);
            statement.setInt(7, player.getVariables().tab6items);
            statement.setInt(8, player.getVariables().tab7items);
            statement.setInt(9, player.getVariables().tab8items);
            statement.setInt(10, player.getVariables().tab9items);
            statement.executeUpdate();
        }
    }
}
