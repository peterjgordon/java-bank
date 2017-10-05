package com.kbank;

import java.sql.*;

public class Main {

    public static void main(String[] args) {
        String password = args[0];
        try {
            Class driver = Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:://localhost/demo", "root", password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM persons;");
            while(resultSet.next()) {
                String name = resultSet.getString("name");
                System.out.println(name);

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}