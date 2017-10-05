DROP DATABASE bankData;
CREATE DATABASE bankData;
USE bankData;

CREATE TABLE customers(
    id
    firstName
    lastName
    dateOfBirth
    gender
    address
);

CREATE TABLE IF NOT EXISTS address(
    postcode varchar(8) NOT NULL PRIMARY KEY,
    country varchar(20) NOT NULL,
    town varchar(20) NOT NULL,
    county varchar(20) NOT NULL,
);

CREATE TABLE accounts(
    accountNumber varchar(8) NOT NULL PRIMARY KEY,
    balance decimal(13,2) DEFAULT 0,
    overdraft unsigned decimal(6,2) DEFAULT 0,
);

/*CREATE TABLE deposits(
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    value unsigned decimal(13,2) NOT NULL,
    accountNumber varchar(8) NOT NULL,
    transferDate date,
);

CREATE TABLE withdrawals(
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    value unsigned decimal(13,2) NOT NULL,
    accountNumber varchar(8) NOT NULL,
    transferDate date,
);*/

