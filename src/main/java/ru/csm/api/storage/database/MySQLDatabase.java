package ru.csm.api.storage.database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabase implements Database {

    private HikariDataSource dataSource;

    public MySQLDatabase(String url, int port, String database, String user, String password) {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8");
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(60000);
        dataSource.setMaxLifetime(90000);
        dataSource.setMaximumPoolSize(4);
    }

    @Override
    public Connection getConnection() {
        try{
            return dataSource.getConnection();
        } catch (SQLException e){
            return null;
        }
    }

    @Override
    public void closeConnection(){
        dataSource.close();
    }

    @Override
    public Row getRow(String table, String key, Object value) {
        Row row = null;

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE LOWER("+key+") LIKE LOWER(?)")){
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

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE LOWER("+key1+") LIKE LOWER(?) AND LOWER("+key2+") LIKE LOWER(?)")){
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
        List<Row> rows;

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE LOWER("+key+") LIKE LOWER(?)")){
            statement.setObject(1, value);

            ResultSet result = statement.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            rows = new ArrayList<>();

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
        Row[] rows;

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE LOWER("+key1+") LIKE LOWER(?) AND LOWER("+key2+") LIKE LOWER(?)")){
            statement.setObject(1, value1);
            statement.setObject(2, value2);

            ResultSet result = statement.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            rows = new Row[result.getFetchSize()];

            int i = 0;
            while(result.next()) {
                Row row = new Row();

                for(int j = 1; j <= data.getColumnCount(); j++) {
                    row.addField(data.getColumnName(j), result.getObject(j));
                }

                rows[i] = row;
                i++;
            }

            return rows;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Row[] getAllRows(String table) {
        List<Row> list = new ArrayList<>();

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table)){
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

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(request)){
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

        String request = "INSERT INTO " + table + "(" + cols + ") VALUES (" + vals + ");";
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(request)){

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

        String request = "UPDATE " + table + " SET " + elements + " WHERE `" + key + "`='" + value + "'";
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(request)){

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

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + key + "=?")){
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
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET "+column+"=? WHERE "+key+"=?")){
            statement.setObject(1, content);
            statement.setObject(2, value);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setObject(String table, String column, Object content, String key1, Object value1, String key2, Object value2) {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET "+column+"=? WHERE "+key1+"=? AND "+key2+"=?")){
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
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + table + " WHERE " + key + "=?")){

            statement.setObject(1, value);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeSQL(String sql) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsRow(String table, String key, Object value) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM "+table+" WHERE "+key+"=?")){
            statement.setObject(1, value);
            ResultSet result = statement.executeQuery();

            return result.next();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
