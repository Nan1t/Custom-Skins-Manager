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

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabase implements Database {

    private Connection connection;

    public SQLiteDatabase(String url, String database, String user, String password) throws SQLException {
        try{
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + url + File.separator + database + ".db", user, password);
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Row getRow(String table, String key, Object value) {
        Row row = null;

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE LOWER("+key+") LIKE LOWER(?)");
            statement.setObject(1, value);

            ResultSet result = statement.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            if(result.next()) {
                row = new Row();
                for(int i = 1; i <= data.getColumnCount(); i++) {
                    row.addField(data.getColumnName(i), result.getObject(i));
                }
            }

            return row;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Row getRow(String table, String key1, Object value1, String key2, Object value2) {
        Row row = null;

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE LOWER("+key1+") LIKE LOWER(?) AND LOWER("+key2+") LIKE LOWER(?)");
            statement.setObject(1, value1);
            statement.setObject(2, value2);

            ResultSet result = statement.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            if(result.next()) {
                row = new Row();
                for(int i = 1; i <= data.getColumnCount(); i++) {
                    row.addField(data.getColumnName(i), result.getObject(i));
                }
            }

            return row;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Row[] getRows(String table, String key, Object value) {
        List<Row> rows = new ArrayList<Row>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE LOWER("+key+") LIKE LOWER(?)");
            statement.setObject(1, value);

            ResultSet result = statement.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            while(result.next()) {
                Row row = new Row();

                for(int j = 1; j <= data.getColumnCount(); j++) {
                    row.addField(data.getColumnName(j), result.getObject(j));
                }

                rows.add(row);
            }

            return rows.toArray(new Row[rows.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Row[] getRows(String table, String key1, Object value1, String key2, Object value2) {
        List<Row> rows = new ArrayList<Row>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE LOWER("+key1+") LIKE LOWER(?) AND LOWER("+key2+") LIKE LOWER(?)");
            statement.setObject(1, value1);
            statement.setObject(2, value2);

            ResultSet result = statement.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            while(result.next()) {
                Row row = new Row();

                for(int j = 1; j <= data.getColumnCount(); j++) {
                    row.addField(data.getColumnName(j), result.getObject(j));
                }

                rows.add(row);
            }

            return rows.toArray(new Row[rows.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Row[] getAllRows(String table) {
        List<Row> list = new ArrayList<Row>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet result = statement.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            while(result.next()) {
                Row row = new Row();

                for(int i = 1; i <= data.getColumnCount(); i++) {
                    row.addField(data.getColumnName(i), result.getObject(i));
                }

                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Row[list.size()]);
    }

    @Override
    public Row[] getRowsWithRequest(String request) {
        List<Row> list = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(request);
            ResultSet result = statement.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            while(result.next()) {
                Row row = new Row();
                for(int i = 1; i <= data.getColumnCount(); i++) {
                    row.addField(data.getColumnName(i), result.getObject(i));
                }
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Row[list.size()]);
    }

    @Override
    public void createRow(String table, Row row) {
        String cols = new String();
        String vals = new String();

        String[] keys = row.getAllFields().keySet().toArray(new String[row.getAllFields().keySet().size()]);
        Object[] values = row.getAllFields().values().toArray(new Object[row.getAllFields().values().size()]);

        for(int i = 0; i < keys.length; i++){
            if(values[i] == null){
                continue;
            }
            cols += (keys[i] + ",");
        }

        for(int i = 0; i < values.length; i++){
            if(values[i] == null){
                continue;
            }
            vals += ("'" + values[i].toString() + "',");
        }

        cols = cols.substring(0, cols.length()-1);
        vals = vals.substring(0, vals.length()-1);

        try {
            String request = "INSERT INTO " + table + "(" + cols + ") VALUES (" + vals + ");";
            PreparedStatement statement = connection.prepareStatement(request);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateRow(String table, String key, Object value, Row newRow){
        String elements = new String();

        String[] keys = newRow.getAllFields().keySet().toArray(new String[newRow.getAllFields().keySet().size()]);
        Object[] values = newRow.getAllFields().values().toArray(new Object[newRow.getAllFields().values().size()]);

        for (String k : keys) {
            elements += "`"+k+"`=?,";
        }

        elements = elements.substring(0, elements.length()-1);

        try {
            String request = "UPDATE " + table + " SET " + elements + " WHERE `" + key + "`='" + value + "'";
            PreparedStatement statement = connection.prepareStatement(request);

            for(int i = 0; i < values.length; i++){
                int index = i+1;
                statement.setObject(index, values[i]);
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getObject(String table, String column, String key, String value) {
        Object obj = null;

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + key + "=?");
            statement.setObject(1, value);

            ResultSet result = statement.executeQuery();

            if(result.next()){
                obj = result.getObject(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return obj;
    }

    @Override
    public void setObject(String table, String column, Object content, String key, Object value) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET "+column+"=? WHERE "+key+"=?");
            statement.setObject(1, content);
            statement.setObject(2, value);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setObject(String table, String column, Object content, String key1, Object value1, String key2, Object value2) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET "+column+"=? WHERE "+key1+"=? AND "+key2+"=?");
            statement.setObject(1, content);
            statement.setObject(2, value1);
            statement.setObject(3, value2);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeRow(String table, String key, String value) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + table + " WHERE " + key + "=?");
            statement.setObject(1, value);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeSQL(String request) {
        try {
            PreparedStatement statement = connection.prepareStatement(request);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsRow(String table, String key, Object value) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + key + "=?");
            statement.setObject(1, value);

            ResultSet result = statement.executeQuery();

            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
