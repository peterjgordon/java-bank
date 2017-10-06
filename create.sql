DROP DATABASE IF EXISTS kbank;
CREATE DATABASE kbank;
USE kbank;

CREATE TABLE postcodes(
    postcode varchar(8) PRIMARY KEY,
    country varchar(20) NOT NULL,
    town varchar(20) NOT NULL,
    county varchar(20) NOT NULL
);

CREATE TABLE customers(
    id int AUTO_INCREMENT NOT NULL PRIMARY KEY,
    firstName varchar(20) NOT NULL,
    lastName varchar(20) NOT NULL,
    dateOfBirth date NOT NULL,
    gender char(1) NOT NULL,
    address varchar(30) NOT NULL,
    phoneNumber varchar(12) NOT NULL,
    email varchar(25) NOT NULL,
    postcode varchar(8)  NOT NULL,
    FOREIGN KEY (postcode) REFERENCES postcodes(postcode)
);

CREATE TABLE accounts(
    accountNumber int(8) ZEROFILL AUTO_INCREMENT NOT NULL PRIMARY KEY,
    balance decimal(13,2) DEFAULT 0,
    dateOfCreation date,
    overdraft decimal(7,2) DEFAULT 0 NOT NULL
);

CREATE TABLE customers_accounts(
    customerID int,
    accountNumber int(8) ZEROFILL NOT NULL,
    PRIMARY KEY (customerID, accountNumber),
    FOREIGN KEY (customerID) REFERENCES customers(id),
    FOREIGN KEY (accountNumber) REFERENCES accounts(accountNumber)
);