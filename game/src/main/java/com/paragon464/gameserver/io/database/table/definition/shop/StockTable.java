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

public final class StockTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockTable.class);

    public static void load() {
        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_definition_contents_shop")) {
            final ResultSet resultSet = statement.executeQuery();
            Shop shop = null;
            while (resultSet.next()) {
                final int shopId = resultSet.getInt("shop_id");
                final int itemId = resultSet.getInt("item_id");
                final int position = resultSet.getInt("position");
                final int buyPrice = resultSet.getInt("price_purchase");
                final int sellPrice = resultSet.getInt("price_sell");
                shop = ShopManager.getShop(shopId);
                if (shop == null) continue;

                shop.addToStock(new ShopItem(itemId, 1, position, buyPrice, sellPrice));
            }
            if (shop != null) {
                shop.shift();
            }
        } catch (SQLException e) {
            LOGGER.error("An error occurred whilst loading the shop stock table!", e);
        }
    }
}
