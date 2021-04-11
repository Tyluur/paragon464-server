package com.paragon464.gameserver;

import com.google.common.net.InetAddresses;
import com.paragon464.gameserver.cache.Cache;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.controller.ControllerHandler;
import com.paragon464.gameserver.model.region.MapBuilder;
import com.paragon464.gameserver.net.PacketManager;
import com.paragon464.gameserver.tickable.impl.SystemUpdateTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.util.concurrent.TimeUnit;

import static com.paragon464.gameserver.Config.*;

public class Paragon {

    private static final Logger logger = LoggerFactory.getLogger(Paragon.class);

    public static void main(final String[] args) throws Exception {
        final long startTime = System.currentTimeMillis();
        SysOutOverSLF4J.registerLoggingSystem("org.apache.logging");
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

       /* logger.info("Running migrations.");
        Flyway.configure().locations("db/migration", "com/paragon464/gameserver/io/database/migration")
            .dataSource(ConnectionPool.getPool().getDataSource()).load().migrate();*/

        logger.info("Starting server.");
        Cache.init();
        final GameEngine engine = new GameEngine();
        World.getWorld().init(engine);
        PacketManager.getPacketManager().bind();
        //  Api.setupRoutes();
        //MapDataTable.load();

        try {
            new RS2Server().bind(InetAddresses.forString(BIND_ADDRESS), GAME_PORT);
            engine.start();
            GameEngine.engineScheduler.scheduleAtFixedRate(engine, 0, CYCLE_RATE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Failed to start the server.", e);
            System.exit(1);
        }

        logger.debug("Loading game definitions.");
        MapBuilder.init();
        // ShopTable.load();
        // StockTable.load();
        //ItemLoaders.init();
        // NPCLoaders.init();
        // DoorTable.load();
        // ObjectTable.load();
        // TeleportsTable.load();
        ControllerHandler.init();
        World.getWorld().setReady(true);
        final long endTime = System.currentTimeMillis();
        logger.info("Server took {} ms to launch.", endTime - startTime);
        logger.info("Ready.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (World.getWorld() == null || World.getWorld().engine == null) {
                return;
            }

            World.getWorld().getEngine().setUpdateTimer(50);
            for (final Player player : World.getWorld().getPlayers()) {
                if (player != null) {
                    player.getFrames().sendSystemUpdate(1);
                }
            }
            World.getWorld().submit(new SystemUpdateTick(1));
        }));
    }
}
