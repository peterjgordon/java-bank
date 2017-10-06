package com.kbank;

import java.sql.*;

public class DB {
    private static String url = "jdbc:mysql://localhost/kbank?useSSL=false";
    private static String password = "password";

    public static int send(String query, boolean returnID) {
        return send(new String[]{query}, returnID)[0];
    }

    public static int[] send(String[] queries, boolean returnID) {
        int[] toReturn = new int[queries.length];
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, "root", password);
            for (int i = 0; i < queries.length; i++) {
                Statement statement = connection.createStatement();
                toReturn[i] = statement.executeUpdate(queries[i]);
                if (returnID) {
                    ResultSet results = statement.executeQuery("SELECT last_insert_id() AS lastID;");
                    results.next();
                    toReturn[i] = Integer.parseInt(results.getString("lastID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return toReturn;
    }

    public static ResultSet get(String query) {
        return get(new String[]{query})[0];
    }

    public static ResultSet[] get(String[] queries) {
        ResultSet[] results = new ResultSet[queries.length];
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, "root", password);
            for (int i = 0; i < queries.length; i++) {
                Statement statement = connection.createStatement();
                results[i] = statement.executeQuery(queries[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
}
