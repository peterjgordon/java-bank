package com.kbank;

import java.sql.*;
import java.util.ArrayList;

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

    public static ArrayList<Object[]> get(String query) {
        return get(new String[]{query}).get(0);
    }

    public static ArrayList<ArrayList<Object[]>> get(String[] queries) {
        ArrayList<ArrayList<Object[]>> results = new ArrayList<ArrayList<Object[]>>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, "root", password);
            for (int i = 0; i < queries.length; i++) {
                Statement statement = connection.createStatement();

                ResultSet result = statement.executeQuery(queries[i]);

                ArrayList<Object[]> rows = new ArrayList<Object[]>();
                int columnCount = result.getMetaData().getColumnCount();
                while(result.next()) {
                    Object[] row = new Object[columnCount];
                    for (int j = 1; i <= columnCount; i++) {
                        row[j - 1] = result.getObject(j); // Or even rs.getObject()
                    }
                    rows.add(row);
                }

                results.add(rows);
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
