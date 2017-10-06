package com.kbank;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class driver = Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            clearScreen();

            System.out.println("======== kBank ========");
            System.out.println("1. Create account");
            //System.out.println("2. Log in to account");
            System.out.println("\n0. Exit");
            System.out.println("=======================");
            System.out.print("Enter your choice: ");

            while(!scanner.hasNextInt()) if(scanner.hasNext()) scanner.nextLine();
            int input = scanner.nextInt();
            scanner.nextLine(); // skip next new line after int
            clearScreen();

            switch (input) {
                case 1:
                    createAccount();
                    break;
                //case 2:
                //    viewAccount();
                //    break;
                //case 3:
                //    break;
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
        System.out.println();
        System.out.print("First name:   ");
        String firstName = getValidLine();
        System.out.print("Last name:    ");
        String lastName = scanner.nextLine();
        Date dob = null;
        while(dob==null) {
            System.out.print("Date of Birth [yyyy-mm-dd]: ");
            try {
                dob = Date.valueOf(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date! Please try again.\n");
            }
        }
        System.out.print("Gender:       ");
        String rawGender = scanner.nextLine().toLowerCase();
        char gender = 'f';
        if ("male".startsWith(rawGender)) {
            gender = 'm';
        }
        System.out.print("Address [house and street]: ");
        String address = scanner.nextLine();
        System.out.print("Postcode:     ");
        String postcode = scanner.nextLine();
        System.out.print("Phone Number: ");
        String phoneNo = scanner.nextLine();
        System.out.print("Email:        ");
        String email = scanner.nextLine();
        System.out.println("How much is your initial deposit?");
        while(!scanner.hasNextBigDecimal()) if(scanner.hasNext()) scanner.nextLine();
        BigDecimal deposit = scanner.nextBigDecimal();
        scanner.nextLine();

        ArrayList<String> queries = new ArrayList<String>();
        queries.add("INSERT INTO customers(firstName, lastName, dateOfBirth, gender, address, phoneNumber, email, postcode) VALUES('" + firstName + "', '" + lastName + "', '" + dob + "', '" + gender + "', '" + address + "', '" + phoneNo + "', '" + email + "', '" + postcode + "');");
        queries.add("INSERT INTO accounts(balance) VALUES(" + deposit + ");");
        int[] results = DB.send(queries.toArray(new String[queries.size()]), true);
        queries.clear();
        int customerID = results[0];
        int accountNo = results[1];
        queries.add("INSERT INTO customers_accounts(customerID, accountNumber) VALUES(" + customerID + ", " + accountNo + ");");
        DB.send(queries.toArray(new String[queries.size()]), false);

        System.out.println("\nYour account has been created.");
    }

    private static void viewAccount() {

    }

    private static String getValidLine() {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
        String input = null;
        while(input==null || !pattern.matcher(input).matches()) {
            input = scanner.nextLine();
        }
        return input;
    }

    public static void clearScreen() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("\n");
        }
    }
}