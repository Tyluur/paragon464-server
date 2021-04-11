package com.paragon464.gameserver.io.database;

import com.paragon464.gameserver.api.XfApi;
import com.paragon464.gameserver.api.xenforo.User;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.io.database.table.Table;
import com.paragon464.gameserver.io.database.table.player.BankCountTable;
import com.paragon464.gameserver.io.database.table.player.ContainerTable;
import com.paragon464.gameserver.io.database.table.player.CreditsTable;
import com.paragon464.gameserver.io.database.table.player.PlayerTable;
import com.paragon464.gameserver.io.database.table.player.RelationshipTable;
import com.paragon464.gameserver.io.database.table.player.SkillTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.Container;
import com.paragon464.gameserver.model.area.AreaHandler;
import com.paragon464.gameserver.net.protocol.ReturnCode;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import static com.paragon464.gameserver.api.xenforo.ReturnCode.INVALID_PASSWORD;
import static com.paragon464.gameserver.api.xenforo.ReturnCode.MAX_LOGIN_ATTEMPTS;
import static com.paragon464.gameserver.api.xenforo.ReturnCode.NOT_FOUND;

public class PlayerSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerSerializer.class);

    private final Player player;
    private final Table<Player>[] tables;

    @SuppressWarnings("unchecked")
    public PlayerSerializer(Player player) {
        this.player = player;

        tables = new Table[]{
            new PlayerTable(),
            new SkillTable(),
            new RelationshipTable(),
            new BankCountTable(),
            new CreditsTable(),
            new ContainerTable() {
                @Override
                public Container getContainer() {
                    return player.getBank();
                }
            },
            new ContainerTable() {
                @Override
                public Container getContainer() {
                    return player.getInventory();
                }
            },
            new ContainerTable() {
                @Override
                public Container getContainer() {
                    return player.getEquipment();
                }
            },
        };
    }

    public boolean load() {
        ConnectionPool.execute(() -> {
            val loginIp = ((InetSocketAddress) player.getSession().getRemoteAddress()).getAddress().getHostAddress();
            val loginName = player.getDetails().getName();
            val pass = player.getDetails().getPassword();
            //val authResult = XfApi.getBoard().authenticateUser(loginName, pass, loginIp);
            val returnCode = ReturnCode.LOGIN_OK;//authResult.getReturnCode();
            if (true/*authResult.wasSuccessful() && authResult.getUser().isPresent()*/) { // temp just if (true) lmao
                //val user = authResult.getUser().get();
                player.getDetails().dummyPlayer.wrongPassword = false;
                player.getDetails().dummyPlayer.banned = false;//user.isBanned();
                setDetails(/*user*/null);
               /* try {
                    for (Table<Player> table : tables) {
                        table.load(player);
                    }
                } catch (SQLException e) {
                    while (e != null) {
                        LOGGER.error("An error occurred whilst loading player {}!", player.getDetails().getName(), e);
                        e = e.getNextException();
                    }
                } catch (IOException e) {
                    LOGGER.error("An error occurred whilst loading player {}!", player.getDetails().getName(), e);
                }*/
                World.getWorld().loadGame(player, ReturnCode.LOGIN_OK.getOpcode());
                AreaHandler.fixPlayer(player);
                World.registerPlayer(player);
            } else if (returnCode.equals(INVALID_PASSWORD) || returnCode.equals(MAX_LOGIN_ATTEMPTS)) {
                World.getWorld().loadGame(player, ReturnCode.INVALID_DETAILS.getOpcode());
            } else if (returnCode.equals(NOT_FOUND)) {
                val creationResult = XfApi.getBoard().createUser(loginName, pass);
                if (creationResult.wasSuccessful() && creationResult.getUser().isPresent()) {
                    val user = creationResult.getUser().get();
                    player.getAttributes().set("new_account_verify", true);
                    player.getDetails().dummyPlayer.wrongPassword = false;
                    player.getDetails().dummyPlayer.banned = false;
                    setDetails(user);
                    World.getWorld().loadGame(player, ReturnCode.LOGIN_OK.getOpcode());
                    AreaHandler.fixPlayer(player);
                    World.registerPlayer(player);
                } else {
                    World.getWorld().loadGame(player, ReturnCode.GENERAL_FAILURE.getOpcode());
                }
            } else {
                World.getWorld().loadGame(player, ReturnCode.GENERAL_FAILURE.getOpcode());
            }
        });
        return true;
    }

    private void setDetails(final User user) {
        player.getDetails().setUserId(1/*user.getId()*/); // temp fill with random data
        player.getDetails().setAdmin(true/*user.isAdmin()*/);
        player.getDetails().setModerator(false);//user.isModerator());
        player.getDetails().setCurrentEmail("whatever@yahoo.com");//user.getEmail());
        player.getDetails().setEmailVerified(true);//user.isValidated());
        player.getDetails().setGroup(0);//user.getGroup());
        player.getDetails().setUnreadMessages(0); // TODO: Pull notification data
    }

    public boolean save() {
        ConnectionPool.execute(() -> {
            try {
                for (Table<Player> table : tables)
                    table.save(player);
            } catch (SQLException e) {
                while (e != null) {
                    LOGGER.error("An error occurred whilst saving player {}!", player.getDetails().getName(), e);
                    e = e.getNextException();
                }
            } catch (IOException e) {
                LOGGER.error("An error occurred whilst saving player {}!", player.getDetails().getName());
            }
        });
        return true;
    }
}
