package ru.csm.api.storage.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {

    Connection getConnection() throws SQLException;

    void closeConnection();

    Row getRow(String table, String key, Object value);

    Row getRow(String table, String key1, Object value1, String key2, Object value2);

    Row[] getAllRows(String table);

    Row[] getRows(String table, String key, Object value);

    Row[] getRows(String table, String key1, Object value1, String key2, Object value2);

    Row[] getRowsWithRequest(String request);

    void createRow(String table, Row row);

    void updateRow(String table, String key, Object value, Row newRow);

    Object getObject(String table, String column, String key, String value);

    void setObject(String table, String column, Object content, String key, Object value);

    void setObject(String table, String column, Object content, String key1, Object value1, String key2, Object value2);

    void removeRow(String table, String key, String value);

    void executeSQL(String request);

    boolean existsRow(String table, String key, Object value);
}
