use
pnavale;

DROP TABLE IF EXISTS Encompasses;
DROP TABLE IF EXISTS Citation;
DROP TABLE IF EXISTS Possesses;
DROP TABLE IF EXISTS Comprises;
DROP TABLE IF EXISTS Vehicle;
DROP TABLE IF EXISTS Permit;
DROP TABLE IF EXISTS Space;
DROP TABLE IF EXISTS Zone;
DROP TABLE IF EXISTS ParkingLot;
DROP TABLE IF EXISTS UnivMember;
DROP TABLE IF EXISTS Visitor;
DROP TABLE IF EXISTS Driver;

CREATE TABLE ParkingLot
(
    LotName  VARCHAR(50) PRIMARY KEY,
    Address  VARCHAR(50) NOT NULL,
    NumSpace INT,
    NumZone  INT
);

CREATE TABLE Zone
(
    ZoneID  VARCHAR(2),
    LotName VARCHAR(50),
    PRIMARY KEY (ZoneID, LotName),
    FOREIGN KEY (LotName) REFERENCES ParkingLot (LotName) ON UPDATE CASCADE
);

CREATE TABLE Space
(
    SpaceNumber INT,
    SpaceType   VARCHAR(20) DEFAULT 'Regular',
    AvailStatus VARCHAR(20) NOT NULL,
    ZoneID      VARCHAR(2),
    LotName     VARCHAR(50),
    PRIMARY KEY (ZoneID, LotName, SpaceNumber),
    FOREIGN KEY (ZoneID, LotName) REFERENCES Zone (ZoneID, LotName) ON UPDATE CASCADE
);

CREATE TABLE Driver
(
    DriverID   BIGINT PRIMARY KEY,
    DriverName VARCHAR(50) NOT NULL,
    Status     VARCHAR(50) NOT NULL
);

CREATE TABLE UnivMember
(
    DriverID BIGINT PRIMARY KEY,
    FOREIGN KEY (DriverID) REFERENCES Driver (DriverID) ON UPDATE CASCADE
);

CREATE TABLE Visitor
(
    DriverID BIGINT PRIMARY KEY,
    FOREIGN KEY (DriverID) REFERENCES Driver (DriverID) ON UPDATE CASCADE
);

CREATE TABLE Permit
(
    PermitID          VARCHAR(20) PRIMARY KEY,
    PermitType        VARCHAR(50) NOT NULL,
    StartDate         DATE        NOT NULL,
    ExpDate           DATE        NOT NULL,
    ExpTime           TIME        NOT NULL,
    AssignedSpaceType VARCHAR(50) NOT NULL,
    AssignedZoneID    VARCHAR(50) NOT NULL,
    AssignedLot       VARCHAR(50) NOT NULL,
    DriverID          BIGINT      NOT NULL,
    FOREIGN KEY (DriverID) REFERENCES Driver (DriverID) ON UPDATE CASCADE
);

CREATE TABLE Possesses
(
    DriverID BIGINT,
    PermitID VARCHAR(20),
    PRIMARY KEY (DriverID, PermitID),
    FOREIGN KEY (DriverID) REFERENCES Driver (DriverID) ON UPDATE CASCADE,
    FOREIGN KEY (PermitID) REFERENCES Permit (PermitID) ON UPDATE CASCADE
);

CREATE TABLE Comprises
(
    PermitID    VARCHAR(20),
    SpaceNumber INT,
    ZoneID      VARCHAR(2),
    LotName     VARCHAR(50),
    PRIMARY KEY (PermitID, SpaceNumber, ZoneID, LotName),
    FOREIGN KEY (PermitID) REFERENCES Permit (PermitID) ON UPDATE CASCADE,
    FOREIGN KEY (ZoneID, LotName, SpaceNumber) REFERENCES Space (ZoneID, LotName, SpaceNumber) ON UPDATE CASCADE
);

CREATE TABLE Vehicle
(
    LicenseNo VARCHAR(10) PRIMARY KEY,
    Model     VARCHAR(100) NOT NULL,
    Manf      VARCHAR(50),
    Color     VARCHAR(50)  NOT NULL,
    Year      INT          NOT NULL,
    PermitID  VARCHAR(20),
    FOREIGN KEY (PermitID) REFERENCES Permit (PermitID) ON UPDATE CASCADE
);

CREATE TABLE Citation
(
    CNumber       VARCHAR(20) PRIMARY KEY,
    Date          DATE           NOT NULL,
    Fee           DECIMAL(10, 2) NOT NULL,
    Category      VARCHAR(20)    NOT NULL,
    PaymentStatus VARCHAR(50)    NOT NULL,
    Time          TIME           NOT NULL,
    AppealStatus  VARCHAR(50),
    LicenseNo     VARCHAR(10)    NOT NULL,
    DriverID      BIGINT,
    FOREIGN KEY (LicenseNo) REFERENCES Vehicle (LicenseNo) ON UPDATE CASCADE,
    FOREIGN KEY (DriverID) REFERENCES Driver (DriverID) ON UPDATE CASCADE
);


CREATE TABLE Encompasses
(
    LotName VARCHAR(50),
    CNumber VARCHAR(20),
    PRIMARY KEY (LotName, CNumber),
    FOREIGN KEY (LotName) REFERENCES ParkingLot (LotName) ON UPDATE CASCADE,
    FOREIGN KEY (CNumber) REFERENCES Citation (CNumber) ON UPDATE CASCADE
);

INSERT INTO Driver (DriverID, DriverName, Status)
VALUES (122765234, 'Sachin Tendulkar', 'S'),
       (266399121, 'John Clay', 'E'),
       (366399121, 'Julia Hicks', 'E'),
       (466399121, 'Ivan Garcia', 'E'),
       (7729119111, 'Sam BankmanFried', 'V'),
       (9194789124, 'Charles Xavier', 'V');


INSERT INTO UnivMember (DriverID)
VALUES (122765234),
       (266399121),
       (366399121),
       (466399121);

INSERT INTO Visitor (DriverID)
VALUES (7729119111),
       (9194789124);

INSERT INTO Permit (PermitID, PermitType, StartDate, ExpDate, ExpTime, AssignedSpaceType, AssignedZoneID, AssignedLot,
                    DriverID)
VALUES ('EIG3C', 'Commuter', '2023-01-01', '2024-01-01', '06:00:00', 'Regular', 'A', 'Poulton Deck', 466399121),
       ('EJC1R', 'Residential', '2010-01-01', '2030-01-01', '06:00:00', 'Electric', 'A', 'Poulton Deck', 266399121),
       ('EJH2C', 'Commuter', '2023-01-01', '2024-01-01', '06:00:00', 'Regular', 'A', 'Poulton Deck', 366399121),
       ('SST1R', 'Residential', '2022-01-01', '2023-09-30', '06:00:00', 'Compact Car', 'AS', 'Dan Allen Parking Deck',
        122765234),
       ('VCX1SE', 'Special Event', '2023-01-01', '2023-11-15', '06:00:00', 'Handicap', 'V', 'Partners Way Deck',
        9194789124),
       ('VSBF1C', 'Commuter', '2023-01-01', '2024-01-01', '06:00:00', 'Regular', 'V', 'Partners Way Deck', 7729119111);

-- Inserting data into Possesses
INSERT INTO Possesses (DriverID, PermitID)
VALUES (7729119111, 'VSBF1C');
INSERT INTO Possesses (DriverID, PermitID)
VALUES (9194789124, 'VCX1SE');

-- Inserting data into Vehicle
INSERT INTO Vehicle (LicenseNo, Model, Manf, Color, Year, PermitID)
VALUES ('Clay1', 'Model S', 'Tesla', 'Ultra Red', 2023, 'EJC1R'),
       ('CRICKET', 'Civic SI', 'Honda', 'Sonic Gray Pearl', 2024, 'SST1R'),
       ('Garcia1', 'Continental GT Speed', 'Bentley', 'Blue Fusion', 2024, 'EIG3C'),
       ('Hicks1', 'M2 Coupe', 'BMW', 'Zandvoort Blue', 2024, 'EJH2C'),
       ('PROFX', 'Taycan Sport Turismo', 'Porsche', 'Frozenblue Metallic', 2024, 'VCX1SE'),
       ('SBF', 'GT-R-Nismo', 'Nissan', 'Pearl White TriCoat', 2024, 'VSBF1C'),
       ('VAN-9910', 'Macan GTS', 'Porsche', 'Papaya Metallic', 2022, NULL);


-- Insert data into Citation table
INSERT INTO Citation (CNumber, Date, Fee, Category, PaymentStatus, Time, AppealStatus, LicenseNo, DriverID)
VALUES ('EP1', '2023-10-01', 30.00, 'Expired Permit', 'DUE', '08:00:00', NULL, 'CRICKET', 122765234),
       ('NP1', '2021-10-11', 40.00, 'No Permit', 'PAID', '08:00:00', NULL, 'VAN-9910', NULL);

-- Insert data into ParkingLot table
INSERT INTO ParkingLot (LotName, Address, NumSpace, NumZone)
VALUES ('Dan Allen Parking Deck', '110 Dan Allen Dr, Raleigh, NC, 27607', NULL, NULL),
       ('Partners Way Deck', '851 Partners Way, Raleigh, NC, 27606', NULL, NULL),
       ('Poulton Deck', '1021 Main Campus Dr, Raleigh, NC, 27606', NULL, NULL);

-- Insert data into Zone table
INSERT INTO Zone (ZoneID, LotName)
VALUES ('A', 'Dan Allen Parking Deck'),
       ('A', 'Partners Way Deck'),
       ('A', 'Poulton Deck'),
       ('AS', 'Dan Allen Parking Deck'),
       ('AS', 'Partners Way Deck'),
       ('AS', 'Poulton Deck'),
       ('B', 'Dan Allen Parking Deck'),
       ('B', 'Partners Way Deck'),
       ('B', 'Poulton Deck'),
       ('BS', 'Dan Allen Parking Deck'),
       ('BS', 'Partners Way Deck'),
       ('BS', 'Poulton Deck'),
       ('C', 'Dan Allen Parking Deck'),
       ('C', 'Partners Way Deck'),
       ('C', 'Poulton Deck'),
       ('CS', 'Dan Allen Parking Deck'),
       ('CS', 'Partners Way Deck'),
       ('CS', 'Poulton Deck'),
       ('D', 'Dan Allen Parking Deck'),
       ('D', 'Partners Way Deck'),
       ('D', 'Poulton Deck'),
       ('DS', 'Dan Allen Parking Deck'),
       ('DS', 'Partners Way Deck'),
       ('DS', 'Poulton Deck'),
       ('V', 'Dan Allen Parking Deck'),
       ('V', 'Partners Way Deck'),
       ('V', 'Poulton Deck');


-- Add data into Space table
INSERT INTO Space (SpaceNumber, AvailStatus, ZoneID, LotName, SpaceType)
VALUES (16, 'Available', 'A', 'Dan Allen Parking Deck', 'Handicap'),
       (16, 'Available', 'A', 'Partners Way Deck', 'Handicap'),
       (1, 'Occupied', 'A', 'Poulton Deck', 'Regular'),
       (2, 'Available', 'A', 'Poulton Deck', 'Regular'),
       (3, 'Occupied', 'A', 'Poulton Deck', 'Regular'),
       (4, 'Available', 'A', 'Poulton Deck', 'Regular'),
       (5, 'Occupied', 'A', 'Poulton Deck', 'Electric'),
       (16, 'Available', 'A', 'Poulton Deck', 'Handicap'),
       (1, 'Available', 'AS', 'Dan Allen Parking Deck', 'Electric'),
       (2, 'Available', 'AS', 'Dan Allen Parking Deck', 'Electric'),
       (3, 'Available', 'AS', 'Dan Allen Parking Deck', 'Electric'),
       (14, 'Occupied', 'AS', 'Dan Allen Parking Deck', 'Compact Car'),
       (17, 'Available', 'AS', 'Dan Allen Parking Deck', 'Handicap'),
       (1, 'Available', 'AS', 'Partners Way Deck', 'Electric'),
       (2, 'Available', 'AS', 'Partners Way Deck', 'Electric'),
       (3, 'Available', 'AS', 'Partners Way Deck', 'Electric'),
       (14, 'Available', 'AS', 'Partners Way Deck', 'Compact Car'),
       (17, 'Available', 'AS', 'Partners Way Deck', 'Handicap'),
       (1, 'Available', 'AS', 'Poulton Deck', 'Electric'),
       (2, 'Available', 'AS', 'Poulton Deck', 'Electric'),
       (3, 'Available', 'AS', 'Poulton Deck', 'Electric'),
       (14, 'Available', 'AS', 'Poulton Deck', 'Compact Car'),
       (17, 'Available', 'AS', 'Poulton Deck', 'Handicap'),
       (1, 'Available', 'B', 'Partners Way Deck', 'Regular'),
       (2, 'Available', 'B', 'Partners Way Deck', 'Regular'),
       (3, 'Available', 'B', 'Partners Way Deck', 'Regular'),
       (4, 'Available', 'B', 'Partners Way Deck', 'Regular'),
       (5, 'Available', 'B', 'Partners Way Deck', 'Electric'),
       (4, 'Available', 'BS', 'Dan Allen Parking Deck', 'Electric'),
       (5, 'Available', 'BS', 'Dan Allen Parking Deck', 'Electric'),
       (6, 'Available', 'BS', 'Dan Allen Parking Deck', 'Electric'),
       (4, 'Available', 'BS', 'Partners Way Deck', 'Electric'),
       (5, 'Available', 'BS', 'Partners Way Deck', 'Electric'),
       (6, 'Available', 'BS', 'Partners Way Deck', 'Electric'),
       (4, 'Available', 'BS', 'Poulton Deck', 'Electric'),
       (5, 'Available', 'BS', 'Poulton Deck', 'Electric'),
       (6, 'Available', 'BS', 'Poulton Deck', 'Electric'),
       (1, 'Available', 'C', 'Dan Allen Parking Deck', 'Regular'),
       (2, 'Available', 'C', 'Dan Allen Parking Deck', 'Regular'),
       (3, 'Available', 'C', 'Dan Allen Parking Deck', 'Regular'),
       (4, 'Available', 'C', 'Dan Allen Parking Deck', 'Regular'),
       (5, 'Available', 'C', 'Dan Allen Parking Deck', 'Electric'),
       (7, 'Available', 'CS', 'Dan Allen Parking Deck', 'Compact Car'),
       (8, 'Available', 'CS', 'Dan Allen Parking Deck', 'Compact Car'),
       (18, 'Available', 'CS', 'Dan Allen Parking Deck', 'Handicap'),
       (7, 'Available', 'CS', 'Partners Way Deck', 'Compact Car'),
       (8, 'Available', 'CS', 'Partners Way Deck', 'Compact Car'),
       (18, 'Available', 'CS', 'Partners Way Deck', 'Handicap'),
       (7, 'Available', 'CS', 'Poulton Deck', 'Compact Car'),
       (8, 'Available', 'CS', 'Poulton Deck', 'Compact Car'),
       (18, 'Available', 'CS', 'Poulton Deck', 'Handicap'),
       (6, 'Available', 'D', 'Dan Allen Parking Deck', 'Regular'),
       (7, 'Available', 'D', 'Dan Allen Parking Deck', 'Regular'),
       (8, 'Available', 'D', 'Dan Allen Parking Deck', 'Regular'),
       (6, 'Available', 'D', 'Partners Way Deck', 'Regular'),
       (7, 'Available', 'D', 'Partners Way Deck', 'Regular'),
       (8, 'Available', 'D', 'Partners Way Deck', 'Regular'),
       (6, 'Available', 'D', 'Poulton Deck', 'Regular'),
       (7, 'Available', 'D', 'Poulton Deck', 'Regular'),
       (8, 'Available', 'D', 'Poulton Deck', 'Regular'),
       (9, 'Available', 'DS', 'Dan Allen Parking Deck', 'Compact Car'),
       (10, 'Available', 'DS', 'Dan Allen Parking Deck', 'Compact Car'),
       (9, 'Available', 'DS', 'Partners Way Deck', 'Compact Car'),
       (10, 'Available', 'DS', 'Partners Way Deck', 'Compact Car'),
       (9, 'Available', 'DS', 'Poulton Deck', 'Compact Car'),
       (10, 'Available', 'DS', 'Poulton Deck', 'Compact Car'),
       (11, 'Available', 'V', 'Dan Allen Parking Deck', 'Regular'),
       (12, 'Available', 'V', 'Dan Allen Parking Deck', 'Compact Car'),
       (13, 'Available', 'V', 'Dan Allen Parking Deck', 'Electric'),
       (15, 'Available', 'V', 'Dan Allen Parking Deck', 'Handicap'),
       (11, 'Occupied', 'V', 'Partners Way Deck', 'Regular'),
       (12, 'Available', 'V', 'Partners Way Deck', 'Compact Car'),
       (13, 'Available', 'V', 'Partners Way Deck', 'Electric'),
       (15, 'Occupied', 'V', 'Partners Way Deck', 'Handicap'),
       (11, 'Available', 'V', 'Poulton Deck', 'Regular'),
       (12, 'Available', 'V', 'Poulton Deck', 'Compact Car'),
       (13, 'Available', 'V', 'Poulton Deck', 'Electric'),
       (15, 'Available', 'V', 'Poulton Deck', 'Handicap');

-- Insert data into Encompasses table
INSERT INTO Encompasses (LotName, CNumber)
VALUES ('Dan Allen Parking Deck', 'NP1'),
       ('Poulton Deck', 'EP1');

-- Add data into Comprises table
INSERT INTO Comprises (PermitID, SpaceNumber, ZoneID, LotName)
VALUES ('EIG3C', 1, 'A', 'Poulton Deck'),
       ('EJC1R', 5, 'A', 'Poulton Deck'),
       ('EJH2C', 3, 'A', 'Poulton Deck'),
       ('SST1R', 14, 'AS', 'Dan Allen Parking Deck'),
       ('VCX1SE', 15, 'V', 'Partners Way Deck'),
       ('VSBF1C', 11, 'V', 'Partners Way Deck');