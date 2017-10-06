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
3. As a user, I want to be able to add an overdraft to my acount to be able to have more money
    * Overdraft limit of 10,000.
    * If they already have an overdraft, offer an increase.
4. As a user, I want to be able to withdraw money so that I can spend it on monster
    * Each withdrawal must be recorded.
    * Max withdrawal at once is 1000
    * Withdrawal costs 1 pound
    * Can't withdraw more than is already there
5. As a user, I want to be able to close my acount as I won't need it anymore
6. As a user, I want to be able to exit the system when I am done.
7. [BONUS] As a user, I want to be to login with a username/password so that I don't have to remember my account number

## Usage

1. Populate the database

```
$ mysql -uroot -ppassword
mysql> source create.sql
mysql> source insert.sql
```
2. Build and run the project

```
$ cd KBank-Frontend
$ mvn clean package
$ cd target/classes/com/kbank
$ java Main
```
However, it is recommended you should use an IDE like IntelliJ.
Use the `KBank-Frontend` folder as the project root and import as a Maven project.
