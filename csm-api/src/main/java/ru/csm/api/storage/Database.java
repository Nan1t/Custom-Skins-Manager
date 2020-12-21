/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.api.storage;

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
