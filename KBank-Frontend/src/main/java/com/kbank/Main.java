package com.kbank;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            @SuppressWarnings("unused")
            Class driver = Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            clearScreen();

            System.out.println("======== kBank ========");
            System.out.println("1. Create account");
            System.out.println("2. View balance");
            System.out.println("3. Manage overdraft");
            System.out.println("4. Withdraw money");
            System.out.println("5. Delete your account");
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
                case 2:
                    viewBalance();
                    break;
                case 3:
                    manageOverdraft();
                    break;
                case 4:
                    withdrawMoney();
                    break;
                case 5:
                    deleteAccount();
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
            ArrayList<Object[]> rows = DB.get("SELECT count(*) FROM postcodes WHERE postcode = '" + postcode + "';");
            if ((Long) rows.get(0)[0] == 0) {
                System.out.println("This is not a valid postcode.");
                postcode = null;
            }
        }
        System.out.print("Phone Number [no international numbers, no spaces]: ");
        String phoneNo = getValidLine(0);
        System.out.print("Email:        ");
        String email = getValidLine("@");
        BigDecimal balance = new BigDecimal(-1);
        System.out.print("How much is your initial balance? [0-1000]\n£");
        while (balance.compareTo(BigDecimal.ZERO) < 0 || balance.compareTo(new BigDecimal(1000)) > 0) {
            while (!scanner.hasNextBigDecimal()) if (scanner.hasNext()) {
                scanner.nextLine();
            }
            balance = scanner.nextBigDecimal();

            if (balance.compareTo(BigDecimal.ZERO) < 0 || balance.compareTo(new BigDecimal(1000)) > 0) {
                System.out.print("Your balance amount must be between £0 and £1000\n£");
            }
        }
        scanner.nextLine();

        ArrayList<String> queries = new ArrayList<String>();
        queries.add("INSERT INTO customers(firstName, lastName, dateOfBirth, gender, address, phoneNumber, email, postcode) VALUES('" + firstName + "', '" + lastName + "', '" + dob + "', '" + gender + "', '" + address + "', '" + phoneNo + "', '" + email + "', '" + postcode + "');");
        queries.add("INSERT INTO accounts(balance) VALUES(" + balance + ");");
        int[] results = DB.send(queries.toArray(new String[queries.size()]), true);
        queries.clear();
        int customerID = results[0];
        int accountNo = results[1];
        queries.add("INSERT INTO customers_accounts(customerID, accountNumber) VALUES(" + customerID + ", " + accountNo + ");");
        DB.send(queries.toArray(new String[queries.size()]), false);

        System.out.println("\nYour account has been created.");
        System.out.println("Customer ID   : " + customerID);
        System.out.println("Account Number: " + accountNo);
    }

    private static void viewBalance() {
        System.out.print("Account Number: ");
        Long accountNo = Long.parseLong(getValidLine(0));
        ArrayList<Object[]> rows = DB.get("SELECT concat(c.firstName, ' ', c.lastName) as name, a.balance, a.accountNumber FROM accounts a " +
                "JOIN customers_accounts ca ON a.accountNumber = ca.accountNumber " +
                "JOIN customers c ON ca.customerID = c.id " +
                "WHERE a.accountNumber = '" + accountNo + "'");
        if (rows.size() == 0) {
            System.out.println("Your account could not be found.");
            return;
        }
        Object[] row = rows.get(0);
        String name = (String) row[0];
        BigDecimal balance = (BigDecimal) row[1];
        accountNo = (Long) row[2];
        System.out.println("\n========= YOUR ACCOUNT =========");
        System.out.println("Name:       " + name);
        System.out.println("Balance:    " + balance);
        System.out.println("Account No: " + accountNo);
        System.out.println("================================");
    }

    private static void withdrawMoney() {
        System.out.print("Account Number: ");
        Long accountNo = Long.parseLong(getValidLine(0));
        ArrayList<Object[]> rows = DB.get("SELECT concat(c.firstName, ' ', c.lastName) as name, a.balance, a.overdraft FROM accounts a " +
                "JOIN customers_accounts ca ON a.accountNumber = ca.accountNumber " +
                "JOIN customers c ON ca.customerID = c.id " +
                "WHERE a.accountNumber = '" + accountNo + "'");

        if (rows.size() == 0) {
            System.out.println("Your account could not be found.");
            return;
        }

        Object[] row = rows.get(0);
        String name = (String) row[0];
        BigDecimal balance = (BigDecimal) row[1];
        BigDecimal overdraft = (BigDecimal) row[2];

        System.out.println("Hello, " + name + "!");
        System.out.println("You have £" + balance + " in your account (£" + overdraft + " agreed overdraft)");

        BigDecimal maxWithdrawal = balance.add(overdraft);
        if (maxWithdrawal.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("You cannot withdraw any money from your account.");
            return;
        }
        if (maxWithdrawal.compareTo(new BigDecimal(1000)) > 1000) {
            maxWithdrawal = new BigDecimal(1000);
        }
        System.out.println("You can withdraw up to £" + maxWithdrawal);

        System.out.print("\nEnter amount to withdraw: £");
        float withdrawal = 0;
        while (withdrawal == 0) {
            withdrawal = Float.parseFloat(getValidLine(0.1f));
            if (withdrawal < 0 || new BigDecimal(withdrawal).compareTo(maxWithdrawal) > 0) {
                withdrawal = 0;
                System.out.print("That's too much. Enter an amount from £0.00 - £" + maxWithdrawal + "\n£");
            }
        }

        int rowsAffected = DB.send("UPDATE accounts SET balance = balance - " + withdrawal + " WHERE accountNumber = " + accountNo, false);
        if (rowsAffected == 0) {
            System.out.println("Your withdrawal could not be processed. Please try again later.");
        } else {
            System.out.println("You have withdrawn £" + withdrawal + " from your account.");
        }
    }

    private static void deleteAccount() {
        System.out.println("Are you sure that you want to delete your account? Type 'yes or 'no'");
        String confirmation = getValidLine();
        if ("yes".startsWith(confirmation.toLowerCase())) {
            System.out.println("To confirm that you wish to permanently delete your account, please enter your account number.");
            System.out.println("Account Number: ");
            Long accountNumber = Long.parseLong(getValidLine(0));
            int rowsAffected = DB.send("DELETE FROM customers_accounts WHERE accountNumber = '" + accountNumber + "';", false);
            if (rowsAffected == 0) {
                System.out.println("Your account couldn't be deleted as you entered an invalid Account Number");
                return;
            }
            System.out.println("Your account " + accountNumber + " has been successfully deleted");
        } else if ("no".startsWith(confirmation.toLowerCase())) {
            System.out.println("You have cancelled the deletion of your account.");
        } else {
            System.out.println("Please enter either yes or no");
            deleteAccount();
        }
    }

    public static String getValidLine() {
        return getValidLine(String.class);
    }

    public static String getValidLine(Object sample) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+$"); // string
        if (sample instanceof Integer) {
            pattern = Pattern.compile("^[0-9]+$"); // int
        }
        if (sample instanceof Float) {
            pattern = Pattern.compile("^[0-9]+(.[0-9]+)?$"); // int
        }
        if (sample instanceof String && ((String) sample).equals("@")) { // email
            pattern = Pattern.compile("^[a-zA-Z0-9@.]+$");
        }
        if (sample instanceof String && ((String) sample).equals("1 Street")) { // address
            pattern = Pattern.compile("^[a-zA-Z0-9,. \\-]+$");
        }

        String input = null;
        while (input == null) {
            input = scanner.nextLine();
            if (input == null || !pattern.matcher(input).matches()) {
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

    private static void manageOverdraft() {
        System.out.print("Account Number:");
        int accountNo = Integer.parseInt(getValidLine(0));
        ArrayList<Object[]> rows = DB.get("SELECT overdraft FROM accounts WHERE accountNumber = " + accountNo + ";");
        Object[] row = rows.get(0);
        if (((BigDecimal)row[0]).compareTo(new BigDecimal(10000)) == 0) {
            System.out.println("The overdraft limit is £10,000.");
            return;
        }
        if (((BigDecimal)row[0]).compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("This account has an overdraft of £" + row[0] + ". Would you like to increase this? Y/N");
            String reply;
            do {
                reply = getValidLine().toLowerCase();
                if ("no".startsWith(reply) && !reply.equals("")) {
                    return;
                } else if (!"yes".startsWith(reply) || reply.equals("")) {
                    System.out.println("Please enter Y or N.");
                    reply = null;
                }
            } while (reply == null);
        }
        // increase overdraft
        System.out.print("How much would you like your overdraft to be?\n£");
        float overdraft = Float.parseFloat(getValidLine(0.1f));
        if(((BigDecimal)row[0]).add(new BigDecimal(overdraft)).compareTo(new BigDecimal(10000)) < 0) {
            DB.send("UPDATE accounts SET overdraft = "+overdraft+" WHERE accountNumber = "+accountNo, false);
            System.out.println("Overdraft set successfully!");
        } else {
            System.out.println("Too much overdraft, please try agin.");
        }
    }
}