package com.paragon464.gameserver.api;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.io.database.PlayerManager;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.io.database.table.definition.ApiKeyTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;

public class Api {

    private static final Logger LOGGER = LoggerFactory.getLogger(Api.class);

    public static HashSet<String> API_KEYS = new HashSet<>();

    static {
        LOGGER.info("Starting webserver.");
        port(Config.WEBSERVER_PORT);
        ApiKeyTable.load();
    }

    public static void setupRoutes() {
        post("/api/v1/player/:username/item", (req, res) -> {
            final String key = req.queryParams("key");
            if (!API_KEYS.contains(key))
                halt(401, "Invalid API key.");
            final String username = req.params(":username");
            final int itemId = Integer.parseInt(req.queryParams("id"));
            final int amount = Integer.parseInt(req.queryParams("amount"));
            final Item item = new Item(itemId, amount);
            final Player player = World.getWorld().getPlayerByName(username);

            if (player != null && !player.isDestroyed()) {
                player.getBank().addItem(item);
                return "Item added successfully.";
            }
            res.status(404);
            return "Player not found.";
        });

        post("/api/v1/player/:username/vote", (req, res) -> {
            final String key = req.queryParams("key");
            LOGGER.debug("Received a post request on {}.", req.pathInfo());
            LOGGER.trace("Attempting to authenticate with API key: {}", key);
            if (!API_KEYS.contains(key)) {
                LOGGER.trace("Unable to authenticate against the provided API key.");
                halt(401, "Invalid API key.");
            }
            final String username = req.params(":username");
            final Player player = World.getWorld().getPlayerByName(username);
            if (player != null && !player.isDestroyed()) {
                player.getAttributes().addInt("vote_points", 5);
                player.getFrames().sendMessage("5 vote points were added to your account.");
                player.getFrames().sendMessage("Your total vote points can be viewed in the quest tab.");
                return "Vote successfully added.";
            } else {
                final int id = PlayerManager.getUserId(username);
                if (id != -1) {
                    ConnectionPool.execute(() -> {
                        try (Connection connection = ConnectionPool.getPool().getConnection();
                             PreparedStatement statement = connection.prepareStatement("UPDATE paragon_player SET vote_points = vote_points + 5 WHERE user_id = ?")) {
                            statement.setInt(1, id);
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            LOGGER.error("Failed to update vote points for player ID {}!", id, e);
                        }
                    });
                    return "Vote successfully added.";
                }
            }
            return "Was not able to vote.";
        });

        post("/api/v1/player/:username/add/credits", (req, res) -> {
            res.status(503);
            res.body("Endpoint temporarily unavailable.");
            return res;
            // TODO: Uncomment when reimplemented.
            /*final String key = req.queryParams("key");
            final int amount = Integer.parseInt(req.queryParams("credits"));
            if (!API_KEYS.contains(key))
                halt(401, "Invalid API key.");
            final String username = req.params(":username");
            final Player player = World.getWorld().getPlayerByName(username);

            if (player != null && !player.isDestroyed()) {
                player.getAttributes().addInt("credits", amount);
                player.getFrames().sendMessage("" + amount + " credits were added to your account.");
                player.getFrames().sendMessage("Your total credits can be viewed in the quest tab.");
                return "Credits were successfully added.";
            }
            return "Player is offline or does not exist.";*/
        });
    }
}
