import org.apache.ibatis.jdbc.Null;
import org.apache.ibatis.jdbc.SQL;

import javax.xml.transform.Result;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Permit {
    static Scanner scanner = new Scanner(System.in);
    static final String CANNOT_ASSIGN_PERMIT_MESSAGE = "Could not assign a permit. Max number of Permits reached.";


    public static void permitOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Insert Permit information");
            System.out.println("2. View all Permit information");
            System.out.println("3. Update Permit information");
            System.out.println("4. Delete Permit information");
            System.out.println("5. Return to the home screen");

            int choice;
            while (true) {
                try {
                    System.out.print("Enter your choice: ");
                    choice = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.println("Please enter a valid choice (numerical)");
                }
            }
            try {


            switch (choice) {
                case 1 -> insertPermit();
                case 2 -> viewPermit();
                case 3 -> updatePermit();
                case 4 -> deletePermit();
                case 5 -> {
                    System.out.println("Back to home menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } catch (SQLException e) {
                System.out.println("Something went wrong! Please try again. ");
            }
        }
    }

    private static void insertPermit() throws SQLException {
        try {
            Main.connection.setAutoCommit(false); // Start Transaction
            ResultSet result;
            boolean flag = false;
            long driverId;
            int number;
            String licenseNo;
            String startDate, expDate, expTime, assignedLot = null;
            System.out.println("Enter Permit ID: ");
            String permitId = scanner.nextLine();

            ResultSet resultSet = Main.statement.executeQuery("SELECT LotName FROM ParkingLot;");
            if (!resultSet.next()) {
                System.out.println("There are no Parking Lots.");
            } else {
                do {
                    System.out.println("\nFollowing are the existing Lot names :");
                    resultSet.beforeFirst();
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString("LotName"));
                    }
                    System.out.println("Enter Lot name from above mentioned list: ");
                    assignedLot = scanner.nextLine();
                    result = Main.statement.executeQuery("SELECT LotName FROM ParkingLot WHERE LotName = \'" + assignedLot + "\' ;");
                    if (result.next()) {
                        flag = true;
                    }
                } while (!flag);
            }

            System.out.println("Enter Zone ID: ");
            System.out.println("For Employees: A, B, C, D");
            System.out.println("For Students: AS, BS, CS, DS");
            System.out.println("For Visitors: V");
            String assignedZoneId = scanner.nextLine();

            System.out.println("Enter Space Type (Electric/ Handicap/ Compact Car/ Regular):");
            String assignedSpaceType = scanner.nextLine();


            System.out.println("Enter Car License Number: ");
            licenseNo = scanner.nextLine();


            do {
                System.out.println("Enter Start Date (YYYY-MM-DD): ");
                startDate = scanner.nextLine();
            } while (!Main.isValidDateTimeFormat(startDate, "YYYY-MM-DD"));

            do {
                System.out.println("Enter Expiry Date (YYYY-MM-DD): ");
                expDate = scanner.nextLine();
            } while (!Main.isValidDateTimeFormat(expDate, "YYYY-MM-DD"));

            do {
                System.out.println("Enter Expiry Time (HH:MM:SS): ");
                expTime = scanner.nextLine();
            } while (!Main.isValidDateTimeFormat(expTime, "HH:MM:SS"));

            while (true) {
                try {
                    System.out.println("Enter your driver id: ");
                    driverId = Long.parseLong(scanner.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.println("Please enter a valid driver id (numerical)");
                }
            }

            System.out.println("Enter Permit Type (residential/ commuter/ peak hours/ special event/ Park & Ride): ");
            String permitType = scanner.nextLine();
            String insertQuery = ("INSERT into Permit (PermitID, PermitType, StartDate, ExpDate, ExpTime, AssignedSpaceType, AssignedZoneID,AssignedLot, DriverID) VALUES (\'" + permitId + "\',\'" + permitType + "\', \'" + startDate + "\', \'" + expDate + "\',\'" + expTime + "\',\'" + assignedSpaceType + "\', \'" + assignedZoneId + "\', \'" + assignedLot + "\'," + driverId + " )\n");


            String status = null;
            ResultSet rs = Main.statement.executeQuery("SELECT Status FROM Driver WHERE DriverID = " + driverId + ";");
            if (rs.next()) {
                status = rs.getString("Status");
                String countQuery = "Select Count(PermitID) AS CountP from Permit WHERE DriverID = " + driverId;
                ResultSet cns = Main.statement.executeQuery(countQuery);
                int cs = 0;
                if (cns.next()) {
                    cs = cns.getInt("CountP");
                }
                permitCountCondition(cs, status.toUpperCase(), insertQuery, permitType, permitId, licenseNo);
                System.out.println(status);

                if (status.equalsIgnoreCase("V")) {
                    String insertPossesses = ("INSERT into Possesses (PermitID, DriverID) VALUES (\'" + permitId + "\'," + driverId + " )");
                    Main.statement.executeUpdate(insertPossesses);
                }
                // Check if below part is required
                ResultSet sp = Main.statement.executeQuery("SELECT SpaceNumber FROM Space WHERE SpaceType = \'" + assignedSpaceType + "\' AND ZoneID =\'" + assignedZoneId + "\' AND LotName = \'" + assignedLot + "\' AND AvailStatus = \'Available\' order by SpaceNumber asc LIMIT 1;");
                if (sp.next()) {
                    number = sp.getInt("SpaceNumber");
                    String insertComprises = ("INSERT into Comprises (PermitID, SpaceNumber,LotName,ZoneID) VALUES (\'" + permitId + "\'," + number + ",\'" + assignedLot + "\',\'" + assignedZoneId + "\')");
                    Main.statement.executeUpdate(insertComprises);
                    String aStatus = "Occupied";
                    Main.statement.executeUpdate("UPDATE Space SET AvailStatus = \'" + aStatus + "\' WHERE ZoneID = \'" + assignedZoneId + "\' AND LotName = \'" + assignedLot + "\' AND SpaceNumber = " + number);

                }
            } else {
                System.out.println("Driver ID does not exist. Please select valid Driver ID from below list");
                Driver.viewDriver();
            }
            Main.connection.commit();// Commit changes if Permit is inserted and it is assigned to vehicle.
        } catch (SQLException e) {
            Main.connection.rollback(); // rollback to previous state
            System.out.println("Error: " + e.getMessage());
            System.out.println("Cannot add Permit info since invalid information was added. Please add valid information.");
        } finally {
            Main.connection.setAutoCommit(true);
        }
    }

    public static void permitCountCondition(int cs, String status, String insertQuery, String permitType, String permitId, String licenseNo) throws SQLException {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        hashMap.put("E", 2);
        hashMap.put("S", 1);
        hashMap.put("V", 1);
        int expectedCount = hashMap.get(status);
        if (cs < expectedCount) {
            Main.statement.executeUpdate(insertQuery);
            Vehicle.assignVehicleToPermit(permitId, licenseNo);
            System.out.println("New Permit added!");
        } else if (cs == expectedCount && (status.equals("E") || status.equals("S")) && (permitType.equalsIgnoreCase("special event") || permitType.equalsIgnoreCase("Park & Ride"))) {
            Main.statement.executeUpdate(insertQuery);
            Vehicle.assignVehicleToPermit(permitId, licenseNo);
            System.out.println("New Permit added!");
        } else {
            System.out.println(CANNOT_ASSIGN_PERMIT_MESSAGE);
        }
    }


    private static void viewPermit() throws SQLException {
        String query = "SELECT PermitID,PermitType,StartDate,ExpDate,ExpTime,AssignedSpaceType AS SpaceType,AssignedZoneID AS ZoneID, AssignedLot AS Lot, DriverID, LicenseNo  FROM Permit NATURAL JOIN Vehicle ";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);

    }


    private static void deletePermit() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            System.out.println("1. Delete permit information by Permit ID");
            System.out.println("2. Return to the permit screen");

            int choice;
            while (true) {
                try {
                    System.out.print("Enter your choice: ");
                    choice = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.println("Please enter a valid choice (numerical)");
                }
            }

            switch (choice) {
                case 1 -> deletePermitbyPermitID();
                case 2 -> {
                    System.out.println("Back to Permit menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void deletePermitbyPermitID() throws SQLException {
        scanner = new Scanner(System.in);
        System.out.println("Enter Permit Id To Be Deleted: ");
        String permitId = String.valueOf(scanner.nextLine());

        boolean PermitExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Permit WHERE PermitID = \'" + permitId + "\'");

        if (rs.next()) {
            PermitExists = true;
        }
//Possess and comprises -delete and vehicle null
        if (PermitExists) {
            Main.statement.executeUpdate("DELETE FROM Possesses WHERE PermitID = \'" + permitId + "\';");
            System.out.println("Permit row with ID " + permitId + " deleted successfully from possesses table as well.");
            Main.statement.executeUpdate("DELETE FROM Comprises WHERE PermitID = \'" + permitId + "\';");
            System.out.println("Permit row with ID " + permitId + " deleted successfully from comprises table as well.");
            String query = "UPDATE Vehicle set PermitID = \'" + permitId + "\' WHERE PermitID = \'" + permitId + "\';";
            PreparedStatement ps = Main.connection.prepareStatement("UPDATE Vehicle set PermitID = ? WHERE PermitID = ?;");
            ps.setNull(1, Types.VARCHAR);
            ps.setString(2, permitId);
            ps.executeUpdate();
            System.out.println("Permit ID updated successfully in Vehicle.");
            Main.statement.executeUpdate("DELETE FROM Permit WHERE PermitID = \'" + permitId + "\';");
            System.out.println("Permit row with ID " + permitId + " deleted successfully.");

        } else {
            System.out.println("Incorrect Permit ID entered. Please select from the below Permit IDs");
            ResultSet ids = Main.statement.executeQuery("Select * from Permit;");
            while (ids.next()) {
                System.out.println(ids.getString("PermitID"));
            }
            System.out.println();
        }
    }

    private static void updatePermit() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Update permit ID");
            System.out.println("2. Update permit type");
            System.out.println("3. Update Start date");
            System.out.println("4. Update Exp date");
            System.out.println("5. Update Exp time");
            System.out.println("6. Update Assigned Space Type");
            System.out.println("7. Update Assigned Zone ID");
            System.out.println("8. Update Assigned Lot");
            System.out.println("9. Update Driver ID");
            System.out.println("10. Return to the Permit screen");

            int choice;
            while (true) {
                try {
                    System.out.print("Enter your choice: ");
                    choice = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.println("\nPlease enter a valid choice (numerical)");
                }
            }

            switch (choice) {
                case 1 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit Id To Be Updated: ");
                    String permitId = scanner.nextLine();
                    System.out.println("Enter new Permit Id: ");
                    String newPermitId = scanner.nextLine();
                    if (doesPermitIDExist(permitId)) {
                        if (doesPermitIDExist(newPermitId) == true) {
                            System.out.println("Permit ID already exists. Please try again.");
                        } else {

                            Main.statement.executeUpdate("UPDATE Permit SET PermitID = \'" + newPermitId + "\' WHERE PermitID = \'" + permitId + "\';");
                            System.out.println("Permit ID updated successfully.");
                        }
                    }
                }
                case 2 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit ID for type To Be Updated: ");
                    String permitId = scanner.nextLine();
                    System.out.println("Enter new Permit type: ");
                    String newPermitType = scanner.nextLine();
                    if (doesPermitIDExist(permitId) == true) {
                        Main.statement.executeUpdate("UPDATE Permit SET PermitType = \'" + newPermitType + "\' WHERE PermitID = \'" + permitId + "\';");
                        System.out.println("Permit type updated successfully.");
                    }
                }

                case 3 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit ID for type To Be Updated: ");
                    String permitId = scanner.nextLine();
                    System.out.println("Enter new Start date: ");
                    String newStartDate = scanner.nextLine();
                    if (doesPermitIDExist(permitId) == true) {
                        Main.statement.executeUpdate("UPDATE Permit SET StartDate = \'" + newStartDate + "\' WHERE PermitID = \'" + permitId + "\';");
                        System.out.println("Start date updated successfully.");
                    }

                }
                case 4 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit ID for type To Be Updated: ");
                    String permitId = scanner.nextLine();
                    System.out.println("Enter new Exp date: ");
                    String newExpDate = scanner.nextLine();
                    if (doesPermitIDExist(permitId) == true) {
                        Main.statement.executeUpdate("UPDATE Permit SET ExpDate = \'" + newExpDate + "\' WHERE PermitID = \'" + permitId + "\';");
                        System.out.println("Exp date updated successfully.");
                    }

                }

                case 5 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit ID for type To Be Updated: ");
                    String permitId = scanner.nextLine();
                    System.out.println("Enter new Exp time: ");
                    String newExpTime = scanner.nextLine();
                    if (doesPermitIDExist(permitId) == true) {
                        Main.statement.executeUpdate("UPDATE Permit SET ExpTime = \'" + newExpTime + "\' WHERE PermitID = \'" + permitId + "\';");
                        System.out.println("Exp time updated successfully.");
                    }

                }

                case 6 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit ID for type To Be Updated: ");
                    String permitId = scanner.nextLine();
                    System.out.println("Enter new Assigned Space Type: ");
                    String newAssignedSpaceType = scanner.nextLine();
                    if (doesPermitIDExist(permitId) == true) {
                        Main.statement.executeUpdate("UPDATE Permit SET AssignedSpaceType = \'" + newAssignedSpaceType + "\' WHERE PermitID = \'" + permitId + "\';");
                        System.out.println("Assigned Space Type updated successfully.");
                    }

                }

                case 7 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit ID for type To Be Updated: ");
                    String permitId = scanner.nextLine();
                    System.out.println("Enter new Zone ID: ");
                    String newAssignedZoneID = scanner.nextLine();
                    if (doesPermitIDExist(permitId) == true) {
                        Main.statement.executeUpdate("UPDATE Permit SET AssignedZoneID = \'" + newAssignedZoneID + "\' WHERE PermitID = \'" + permitId + "\';");
                        System.out.println("Assigned Zone Id updated successfully.");
                    }

                }
                case 8 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit ID for type To Be Updated: ");
                    String permitId = scanner.nextLine();
                    System.out.println("Enter new Lot: ");
                    String newAssignedLot = scanner.nextLine();
                    if (doesPermitIDExist(permitId) == true) {
                        Main.statement.executeUpdate("UPDATE Permit SET AssignedLot = \'" + newAssignedLot + "\' WHERE PermitID = \'" + permitId + "\';");
                        System.out.println("Assigned Zone Id updated successfully.");
                    }

                }

                case 9 -> {
                    scanner = new Scanner(System.in);
                    System.out.println("Enter Permit ID for type To Be Updated: ");
                    String permitId = scanner.nextLine();
                    long newdriverId;
                    while (true) {
                        try {
                            System.out.print("Enter your new driver id: ");
                            newdriverId = Long.parseLong(scanner.nextLine());
                            break;
                        } catch (Exception e) {
                            System.out.println("Please enter a valid driver id (numerical)");
                        }
                    }
                    if (doesPermitIDExist(permitId) == true) {
                        boolean driverIDexists = false;
                        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Driver WHERE DriverID = \'" + newdriverId + "\';");
                        if (rs.next()) {
                            driverIDexists = true;
                        }
                        if (driverIDexists) {
                            Main.statement.executeUpdate("UPDATE Permit SET DriverID = \'" + newdriverId + "\' WHERE PermitID = \'" + permitId + "\';");
                            System.out.println("Driver Id updated successfully.");
                        } else {
                            System.out.println("Driver ID does not exist. Please select valid Permit ID");
                            ResultSet mj = Main.statement.executeQuery("SELECT * FROM Driver ");
                            while (mj.next()) {
                                System.out.println("DriverID=" + mj.getString("DriverID"));
                            }
                        }

                    }
                }

                case 10 -> {
                    System.out.println("Back to driver menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static boolean doesPermitIDExist(String permitId) throws SQLException {
        boolean permitExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Permit WHERE PermitID = \'" + permitId + "\'");
        if (rs.next()) {
            permitExists = true;
        } else {
            System.out.println("Given PermitID does not exist please enter Permit ID from below list.");
            ResultSet ids = Main.statement.executeQuery("SELECT PermitID FROM Permit;");
            while (ids.next()) {
                System.out.println(ids.getString("PermitID"));
            }
            System.out.println();
        }
        return permitExists;
    }

    public static void printAllPermitIds() throws SQLException {
        ResultSet licenses = Main.statement.executeQuery("SELECT PermitID FROM Permit;");
        while (licenses.next()) {
            System.out.println(licenses.getString("PermitID"));
        }
        System.out.println();
    }
}