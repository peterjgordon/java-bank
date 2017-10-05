DROP DATABASE bankData;
CREATE DATABASE bankData;
USE bankData;
CREATE TABLE accounts(
    accountNumber varchar(20) NOT NULL PRIMARY KEY,
    balance decimal(13,2) DEFAULT 0,
    overdraft unsigned decimal DEFAULT 0,
);

CREATE TABLE deposits(
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    value unsigned decimal(13,2) NOT NULL,
    accountNumber varchar(20) NOT NULL,
    transferDate date,
);

CREATE TABLE withdrawals(
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    value unsigned decimal NOT NULL,
    accountNumber varchar(20) NOT NULL,
    transferDate date,
);
