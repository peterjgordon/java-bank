# kBank

A bank management system allowing users to access their accounts.

## User stories

1. As a user, I want to be able to open a new account so that I can track my money
	* Set up DB
	* Front-end to create account
	* Input validation
	* 8 digit account number - unique
	* First name, last name, gender, DoB, address, initial deposit (max £1000), account creation date
	* Current Account
2. As a user, I want to be able to access my account using my account number so that I can see my account balance.
	* Returning name, balance, account number
	* New menu option to view balance

## Loading the database

Create the database by executing `source create.sql` from the mysql shell.
