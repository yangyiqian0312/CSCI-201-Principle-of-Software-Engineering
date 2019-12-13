DROP DATABASE IF EXISTS FinalProject;
CREATE DATABASE FinalProject;
USE FinalProject;
CREATE TABLE USER (
    userID int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    userName VARCHAR(50) UNIQUE NOT NULL,
    passWord VARCHAR(50) NOT NULL
);

CREATE TABLE RECORD (
    recordID int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    userID int(11) NOT NULL,
    numPlayed int(11) NOT NULL,
    numWin int(11) NOT NULL,
    numLoss int(11) NOT NULL,
    FOREIGN KEY (userID) REFERENCES USER(userID)
);

INSERT INTO USER (userName, passWord) VALUES ('Guest','guest');