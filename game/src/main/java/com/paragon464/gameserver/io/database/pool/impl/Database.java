package com.paragon464.gameserver.io.database.pool.impl;

import com.paragon464.gameserver.io.database.DatabaseType;

final class Database {
    private final int port;
    private final DatabaseType type;
    private final String address;
    private final String database;
    private final String username;
    private final String password;

    public Database(int port, DatabaseType type, String address, String database, String username, String password) {
        this.port = port;
        this.type = type;
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public DatabaseType getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
