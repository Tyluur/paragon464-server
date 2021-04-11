package com.paragon464.gameserver.io.database.table;

import java.io.IOException;
import java.sql.SQLException;

public abstract class Table<T> {

    public abstract void load(T t) throws SQLException, IOException;

    public abstract void save(T t) throws SQLException, IOException;
}
