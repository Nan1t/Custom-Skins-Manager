package ru.csm.api.storage.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

    public Database(String url, int port, String database, String user, String password) throws SQLException {

    }

    public abstract Connection getConnection();

    public abstract void closeConnection();

    public abstract Row getRow(String table, String key, Object value);

    public abstract Row getRow(String table, String key1, Object value1, String key2, Object value2);

    public abstract Row[] getAllRows(String table);

    public abstract Row[] getRows(String table, String key, Object value);

    public abstract Row[] getRows(String table, String key1, Object value1, String key2, Object value2);

    public abstract Row[] getRowsWithRequest(String request);

    public abstract void createRow(String table, Row row);

    public abstract void updateRow(String table, String key, Object value, Row newRow);

    public abstract Object getObject(String table, String column, String key, String value);

    public abstract void setObject(String table, String column, Object content, String key, Object value);

    public abstract void setObject(String table, String column, Object content, String key1, Object value1, String key2, Object value2);

    public abstract void removeRow(String table, String key, String value);

    public abstract void executeSQL(String request);

    public abstract boolean existsRow(String table, String key, Object value);
}
