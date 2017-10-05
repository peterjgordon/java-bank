package com.kbank;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static String password;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        password = args[0];

        try {
            Class driver = Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            clearScreen();

            System.out.println("======== kBank ========");
            System.out.println("1. Create account");
            System.out.println("\n0. Exit");
            System.out.println("=======================");
            System.out.print("Enter your choice: ");

            int input = scanner.nextInt();
            scanner.nextLine(); // skip next new line after int
            clearScreen();

            switch (input) {
                case 1:
                    createAccount();
                    break;
                //case 2:
                //    break;
                case 3:
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Please input a valid option.");
                    break;
            }

            System.out.print("\nPress enter to return to the main menu...");
            scanner.nextLine();
        }

    }

    private static void createAccount() {

        System.out.println("SELECTION");
        System.out.println();
        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last name:  ");
        String lastName = scanner.nextLine();
        System.out.print("Date of Birth [yyyy-mm-dd]: ");
        Date dob = Date.valueOf(scanner.nextLine());
        System.out.print("Gender: ");
        String rawGender = scanner.nextLine().toLowerCase();
        char gender = 'f';
        if("male".startsWith(rawGender)) {
            gender = 'm';
        }
        System.out.print("Address [house and street]: ");
        String address = scanner.nextLine();
        System.out.print("Postcode: ");
        String postcode = scanner.nextLine();
        System.out.print("Phone Number: ");
        String phoneNo = scanner.nextLine();
        System.out.print("Email:        ");
        String email = scanner.nextLine();
        System.out.println("How much is your initial deposit?");
        BigDecimal deposit = scanner.nextBigDecimal();
        scanner.nextLine();

        ArrayList<String> queries = new ArrayList<String>();
        queries.add("INSERT INTO customers(firstName, lastName, dateOfBirth, gender, address, phoneNumber, email, postcode) VALUES('"+firstName+"', '"+lastName+"', '"+dob+"', '"+gender+"', '"+address+"', '"+phoneNo+"', '"+email+"', '"+postcode+"'); SELECT LAST_INSERT_ID();");
        queries.add("INSERT INTO accounts(balance) VALUES(" + deposit + ");");
        int[] results = sendToDB(queries.toArray(new String[queries.size()]), true);
        queries.clear();
        int customerID = results[0];
        int accountNo = results[1];
        queries.add("INSERT INTO customers_accounts(customerID, accountNumber) VALUES(" + customerID + ", " + accountNo + ");");
        sendToDB(queries.toArray(new String[queries.size()]), false);

        System.out.println("SELECTION");
        System.out.println("\nYour account has been created.");
    }

    public static int sendToDB(String query, boolean returnID) {
        return sendToDB(new String[]{query}, returnID)[0];
    }

    public static int[] sendToDB(String[] queries, boolean returnID) {
        int[] toReturn = new int[queries.length];
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/kbank", "root", password);
            for (int i = 0; i < queries.length; i++) {
                Statement statement = connection.createStatement();
                toReturn[i] = statement.executeUpdate(queries[i]);
                System.out.println("RETURNID="+returnID);
                if(returnID) {
                    System.out.println("SELECTION");
                    ResultSet results = statement.executeQuery("SELECT last_insert_id() FROM accounts;");
                    results.next();
                    toReturn[i] = results.getInt(0);
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

    public static ResultSet getFromDB(String query) {
        return getFromDB(new String[]{query})[0];
    }

    public static ResultSet[] getFromDB(String[] queries) {
        ResultSet[] results = new ResultSet[queries.length];
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/kbank", "root", password);
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

    public static void clearScreen() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("\n");
        }
    }
}