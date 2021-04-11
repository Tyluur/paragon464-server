package com.paragon464.gameserver.io.database.table.definition.shop;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.shop.Shop;
import com.paragon464.gameserver.model.shop.ShopManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ShopTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_shop")) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final int id = result.getInt("id");
                final String name = result.getString("name");
                final String currency = result.getString("currency");
                final boolean generalStore = result.getBoolean("general_store");
                final boolean buysBack = result.getBoolean("buys_back");
                final Shop shop = new Shop();

                shop.setId(id);
                shop.setName(name);
                shop.setCurrency(currency);
                shop.setGeneralShop(generalStore);
                shop.setBuysBack(buysBack);
                shop.setStock(new ShopItem[40]);
                ShopManager.shop_definitions.add(shop);
            }
        } catch (SQLException e) {
            while (e != null) {
                LOGGER.error("An error occurred whilst loading the shop table!", e);
                e = e.getNextException();
            }
        }
    }
}
