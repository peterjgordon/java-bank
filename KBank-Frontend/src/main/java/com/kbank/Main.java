package com.kbank;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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

            while (!scanner.hasNextInt()) if (scanner.hasNext()) scanner.nextLine();
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
        String lastName = getValidLine();
        Date dob = null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        java.util.Date today = calendar.getTime();
        System.out.print("Date of Birth [yyyy-mm-dd]: ");
        while (dob == null || dob.after(today)) {
            try {
                dob = Date.valueOf(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date! Please try again.");
            }
        }
        char gender = '\u0000';
        System.out.print("Gender:       ");
        while (gender == '\u0000') {
            String rawGender = scanner.nextLine().toLowerCase();
            if ("male".startsWith(rawGender) && !rawGender.equals("")) {
                gender = 'm';
            } else if ("female".startsWith(rawGender) && !rawGender.equals("")) {
                gender = 'f';
            } else {
                System.out.println("Please enter 'male' or 'female' for gender.");
                gender = '\u0000';
            }
        }
        System.out.print("Address [house and street]: ");
        String address = getValidLine("1 Street");
        String postcode = null;
        System.out.print("Postcode:     ");
        while (postcode == null) {
            postcode = scanner.nextLine();
            ArrayList<Object[]> rows = DB.get("SELECT count(*) FROM postcodes WHERE postcode = '"+postcode+"';");
            if ((Long)rows.get(0)[0] == 0) {
                System.out.println("This is not a valid postcode.");
                postcode = null;
            }
        }
        System.out.print("Phone Number [no international numbers, no spaces]: ");
        String phoneNo = getValidLine(0);
        System.out.print("Email:        ");
        String email = getValidLine("@");
        BigDecimal deposit = new BigDecimal(-1);
        System.out.print("How much is your initial deposit? [0-1000]\n£");
        while (deposit.compareTo(BigDecimal.ZERO) < 0 || deposit.compareTo(new BigDecimal(1000)) > 0) {
            while (!scanner.hasNextBigDecimal()) if (scanner.hasNext()) {
                scanner.nextLine();
            }
            deposit = scanner.nextBigDecimal();

            if (deposit.compareTo(BigDecimal.ZERO) < 0 || deposit.compareTo(new BigDecimal(1000)) > 0) {
                System.out.print("Your deposit amount must be between £0 and £1000\n£");
            }
        }
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

    public static String getValidLine() {
        return getValidLine(String.class);
    }

    public static String getValidLine(Object sample) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+$"); // string
        if (sample instanceof Integer) {
            pattern = Pattern.compile("^[0-9]+$"); // int
        }
        if (sample instanceof String && ((String)sample).equals("@")) { // email
            pattern = Pattern.compile("^[a-zA-Z0-9@.]+$");
        }
        if (sample instanceof String && ((String)sample).equals("1 Street")) { // address
            pattern = Pattern.compile("^[a-zA-Z0-9,. \\-]+$");
        }

        String input = null;
        while (input == null) {
            input = scanner.nextLine();
            if(input == null || !pattern.matcher(input).matches()) {
                System.out.println("Your input is invalid, please try again.");
                input = null;
            }
        }
        return input;
    }

    public static void clearScreen() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("\n");
        }
    }
}