package com.paragon464.gameserver.io.database.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractConnectionPool {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            logger.error("Error getting connection from connection pool!", e);
            return null;
        }
    }

    public abstract DataSource getDataSource();

    public abstract void close();
}
