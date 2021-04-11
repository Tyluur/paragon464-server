package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.io.database.PlayerSerializer;
import com.paragon464.gameserver.model.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;

public class AccountManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountManager.class);

    private static final Deque<Player> queuedLogins = new LinkedList<>();
    private static final Deque<Player> queuedLogouts = new LinkedList<>();

    public static void tickLogins() {
        if (!World.getWorld().isReady()) {
            return;
        }
        while (queuedLogins.peek() != null) {
            Player player = queuedLogins.poll();
            if (player.getDetails().dummyPlayer.attemptLogin) {
                if (load(player)) {
                    player.getDetails().dummyPlayer.attemptLogin = false;
                } else {//failed
                    insert_load(player);
                }
            }
        }
    }

    private static boolean load(Player player) {
        return new PlayerSerializer(player).load();
    }

    public static boolean insert_load(Player player) {
        synchronized (queuedLogins) {
            if (!queuedLogins.contains(player)) {
                queuedLogins.add(player);
                return true;
            }
        }
        return false;
    }

    public static void tickLogouts() {
        if (!World.getWorld().isReady()) {
            return;
        }
        while (queuedLogouts.peek() != null) {
            Player player = queuedLogouts.poll();
            if (player.getDetails().dummyPlayer.attemptLogout) {
                if (save(player)) {
                    player.getDetails().dummyPlayer.attemptLogout = false;
                } else {//failed
                    insert_save(player);
                }
            }
        }
    }

    private static boolean save(Player player) {
        try {
            return new PlayerSerializer(player).save();
        } catch (Exception e) {
            LOGGER.error("An error occurred whilst saving player \"{}\"!", player.getDetails().getName(), e);
            return false;
        }
    }

    public static boolean insert_save(Player player) {
        if (!queuedLogouts.contains(player)) {
            queuedLogouts.add(player);
            return true;
        }
        return false;
    }
}
