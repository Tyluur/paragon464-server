package com.paragon464.gameserver.io.database;

public enum DatabaseType {
    POSTGRES("postgresql"),
    MYSQL("mysql");

    private final String jdbcString;

    DatabaseType(final String jdbcString) {
        this.jdbcString = jdbcString;
    }

    public String getJdbcString() {
        return jdbcString;
    }
}
