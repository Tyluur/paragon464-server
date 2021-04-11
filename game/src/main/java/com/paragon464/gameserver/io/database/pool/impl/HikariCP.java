package com.paragon464.gameserver.io.database.pool.impl;

import com.paragon464.gameserver.io.database.pool.AbstractConnectionPool;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Calendar;

public final class HikariCP extends AbstractConnectionPool {

    private final HikariDataSource dataSource = new HikariDataSource();

    /**
     * The HikariCP constructor.
     *
     * @param database The database to bind this connection pool to.
     */
    HikariCP(Database database) {
        dataSource.setJdbcUrl("jdbc:" + database.getType().getJdbcString() + "://" + database.getAddress() + ":" + database.getPort() + "/" + database.getDatabase() + "?serverTimezone=" + Calendar.getInstance().getTimeZone().getID());
        dataSource.setUsername(database.getUsername());
        dataSource.setPassword(database.getPassword());
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        dataSource.addDataSourceProperty("maximumPoolSize", 25);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 500);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSource.addDataSourceProperty("connectionTimeout", 600000);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
