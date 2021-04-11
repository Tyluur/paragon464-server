package com.paragon464.gameserver.io.database.pool.impl;

import com.paragon464.gameserver.Config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class ConnectionPool {

    private static final Executor executor = Executors.newSingleThreadScheduledExecutor();

    private static final HikariCP gamePool = new HikariCP(Config.getConfig().getTable("database").to(Database.class));

    private ConnectionPool() {
    }

    public static Executor getExecutor() {
        return executor;
    }

    public static HikariCP getPool() {
        return gamePool;
    }

    public static void execute(final Runnable runnable) {
        executor.execute(runnable);
    }
}
