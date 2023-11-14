import org.apache.ibatis.jdbc.Null;
import org.apache.ibatis.jdbc.SQL;

import javax.xml.transform.Result;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class Permit {
    static Scanner scanner = new Scanner(System.in);




    public static void permitOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Insert Permit information");
            System.out.println("2. View all Permit information");
            System.out.println("3. Update Permit information");
            System.out.println("4. Delete Permit information");
            System.out.println("5. Return to the home screen");
            System.out.println("Select suitable option");
            System.out.println("Choose what operation you want to perform");

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
                case 1 -> insertPermit();
                case 2 -> viewPermit();
                case 3 -> updatePermit();
                case 4 -> deletePermit();
                case 5 -> {System.out.println("Back to home menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void insertPermit() throws SQLException {
        System.out.println("Enter Permit ID: ");
        String permitId = scanner.nextLine();

        System.out.println("Enter Permit Type: ");
        String permitType = scanner.nextLine();

        System.out.println("Enter Start Date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();

        System.out.println("Enter Expiry Date (YYYY-MM-DD): ");
        String expDate = scanner.nextLine();

        System.out.println("Enter Expiry Time (HH:MM:SS): ");
        String expTime = scanner.nextLine();

        System.out.println("Enter Assigned Space Type: ");
        String assignedSpaceType = scanner.nextLine();

        System.out.println("Enter Assigned Zone ID: ");
        String assignedZoneId = scanner.nextLine();

        System.out.println("Enter Car License Number: ");
        String licenseNo = scanner.nextLine();

        long driverId;

        while (true) {
            try {
                System.out.print("Enter your driver id: ");
                driverId =Long.parseLong(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Please enter a valid driver id (numerical)");
            }
        }

        try {
            String status = null;
            String LotName = null;
            Integer number = null;
            boolean driverIDexists=false;
            ResultSet rs=Main.statement.executeQuery("SELECT Status FROM Driver WHERE DriverID = " + driverId + ";");
            if(rs.next()){
                status = rs.getString("Status");
                driverIDexists=true;
            }
            if(driverIDexists)
            {
                String insertQuery = ("INSERT into Permit (PermitID, PermitType, StartDate, ExpDate, ExpTime, AssignedSpaceType, AssignedZoneID, DriverID) VALUES (\'" + permitId + "\',\'" + permitType + "\', \'" + startDate + "\', \'" + expDate + "\',\'" + expTime + "\',\'" + assignedSpaceType + "\', \'" + assignedZoneId + "\', " + driverId + " )\n");
                Main.statement.executeUpdate(insertQuery);
                System.out.println("New Permit added!");
                System.out.println(status);
//                System.out.println(status.equals("V"));

                if(status.equals("V")){
                    System.out.println(status);
                    String insertPossesses = ("INSERT into Possesses (PermitID, DriverID) VALUES (\'" + permitId + "\',"+driverId + " )\n");
                    Main.statement.executeUpdate(insertPossesses);
                }
                ResultSet sp=Main.statement.executeQuery("SELECT SpaceNumber,LotName FROM Space WHERE SpaceType = \'" + assignedSpaceType + "\' AND ZoneID =\'"+assignedZoneId+"\'order by SpaceNumber asc LIMIT 1;");
                if(sp.next()){
                    number = sp.getInt("SpaceNumber");
                    System.out.println(number);
                    LotName = sp.getString("LotName");
                    String insertComprises = ("INSERT into Comprises (PermitID, SpaceNumber,LotName,ZoneID) VALUES (\'" + permitId + "\',"+number + ",\'"+LotName+"\',\'"+assignedZoneId+"\')\n");
                    Main.statement.executeUpdate(insertComprises);
                    String aStatus = "Occupied";
                    Main.statement.executeUpdate("UPDATE Space SET AvailStatus = \'"+aStatus+"\' WHERE ZoneID = \'" + assignedZoneId +"\' AND LotName = \'"+LotName+"\' AND SpaceNumber = "+number);

                }


            }
            else{
                System.out.println("Driver ID does not exist. Please select valid Driver ID from below list");
   //            System.out.println("Cannot add Permit info since DriverID does not exist. Please add Driver ID from the below given list.");
                Driver.viewDriver();
            }




        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Cannot add Permit info since invalid information was added. Please add valid information.");
//            Driver.viewDriver();
        }


    }


    private static void viewPermit() throws SQLException {
        String query = "Select * from Permit";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);

    }


    private static void deletePermit() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            System.out.println("1. Delete driver information by Permit ID");
            //System.out.println("2. Delete driver information by driver ID");
            System.out.println("3. Return to the driver screen");

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
                // case 2 -> deleteDriverByName();
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
            ps.setString(2,permitId);
            ps.executeUpdate();
            System.out.println("Permit ID updated successfully in Vehicle.");
            Main.statement.executeUpdate("DELETE FROM Permit WHERE PermitID = \'" + permitId + "\';");
            System.out.println("Permit row with ID " + permitId + " deleted successfully.");

        } else {
            System.out.println("Incorrect Permit ID entered. Please select from the below Permit IDs");
            ResultSet ids = Main.statement.executeQuery("Select * from Permit;");
            while (ids.next()) {
                System.out.println(ids.getInt("PermitID"));
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
            System.out.println("8. Update Driver ID");
            System.out.println("9. Return to the Permit screen");

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
                    if(doesPermitIDExist(permitId)) {
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
                    if (doesPermitIDExist(permitId)==true) {
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
                    if (doesPermitIDExist(permitId)==true) {
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
                    if (doesPermitIDExist(permitId)==true) {
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
                    if (doesPermitIDExist(permitId)==true) {
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
                    if (doesPermitIDExist(permitId)==true) {
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
                    if (doesPermitIDExist(permitId)==true) {
                        Main.statement.executeUpdate("UPDATE Permit SET AssignedZoneID = \'" + newAssignedZoneID + "\' WHERE PermitID = \'" + permitId + "\';");
                        System.out.println("Assigned Zone Id updated successfully.");
                    }

                }

                case 8 -> {
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
                        boolean driverIDexists=false;
                        ResultSet rs=Main.statement.executeQuery("SELECT * FROM Driver WHERE DriverID = \'" + newdriverId + "\';");
                        if(rs.next()){
                            driverIDexists=true;
                        }
                        if(driverIDexists)
                        {
                            Main.statement.executeUpdate("UPDATE Permit SET DriverID = \'" + newdriverId + "\' WHERE PermitID = \'" + permitId + "\';");
                            System.out.println("Driver Id updated successfully.");
                        }
                        else{
                            System.out.println("Driver ID does not exist. Please select valid Permit ID");
                            ResultSet mj=Main.statement.executeQuery("SELECT * FROM Driver ");
                            while(mj.next())
                            {
                                System.out.println("DriverID="+mj.getString("DriverID"));
                            }
                        }

                    }
                }

                case 9 -> {
                    System.out.println("Back to driver menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static boolean doesPermitIDExist(String permitId) throws SQLException {
        boolean permitExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Permit WHERE PermitID = \'" + permitId+"\'");
        if (rs.next()) {
            permitExists = true;
        }
        else{
            System.out.println("Given PermitID does not exist please enter Permit ID from below list.");
            ResultSet ids = Main.statement.executeQuery("SELECT PermitID FROM Permit;");
            while (ids.next()) {
                System.out.println(ids.getString("PermitID"));
            }
            System.out.println();
        }
        return permitExists;
    }
}
