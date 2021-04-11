package com.paragon464.gameserver.model.content.bonds;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class CreditBond {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditBond.class);

    /**
     * A default constructor to prevent instantiation.
     */
    private CreditBond() {
    }

    /**
     * Redeems a credit bond for the specified user.
     *
     * @param player The user redeeeming the bond.
     * @param bond   The bond item being redeemed.
     * @return {@code true} If the bond could be redeemed. Otherwise {@code false}.
     */
    public static boolean redeemBond(final Player player, final Item bond) {
        player.getFrames().sendMessage("Credit redemption temporarily disabled.");
        return true;
        // TODO: Uncomment when reimplemented.
        /*final Integer bondValue = BondConstants.BONDS.get(bond.getId());

        if (bondValue != null) {
            player.getInventory().deleteItem(bond);
            player.getInventory().refresh();
            player.getAttributes().set("credits", player.getAttributes().getInt("credits") + bondValue);

            ConnectionPool.execute(() -> {
                try (Connection connection = ConnectionPool.getForumPool().getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE xf_user SET bdbank_money = bdbank_money + ? WHERE user_id = ?;")) {
                    statement.setDouble(1, bondValue);
                    statement.setInt(2, player.getDetails().getUserId());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    while (e != null) {
                        LOGGER.error("an error occurred whilst redeeming a bond for player {}!", player.getDetails().getName(), e);
                        e = e.getNextException();
                    }
                }
            });

            player.getFrames().sendMessage(String.format(BondConstants.REDEMPTION_MESSAGE, bondValue));
            return true;
        }
        return false;*/
    }
}
