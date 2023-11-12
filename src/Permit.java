import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
//                case 3 -> updateDriver();
//                case 4 -> deletePermit();
                case 5 -> System.out.println("Back to home menu");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

//    private static void insertPermit() {
//        System.out.println("Enter Permit ID: ");
//        String driverName = scanner.nextLine();
//        System.out.println("Enter Permit ID: ");
//        int driverId = Integer.parseInt(scanner.nextLine());
//        System.out.println("Enter Permit Status: ");
//        String driverStatus = scanner.nextLine();
//    }


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

        System.out.println("Enter Driver ID: ");
        long driverId = Long.parseLong(scanner.nextLine());

        try {
            // Assuming you have a connection object, replace 'your_connection' with your actual connection object
//            Connection connection = YourConnectionClass.getConnection();
            // SQL query to insert data into the Permit table
            String insertQuery = ("INSERT into Permit (PermitID, PermitType, StartDate, ExpDate, ExpTime, AssignedSpaceType, AssignedZoneID, DriverID) VALUES (\'" + permitId + "\',\'" + permitType + "\', \'" + startDate + "\', \'" + expDate + "\',\'" + expTime + "\',\'" + assignedSpaceType + "\', \'" + assignedZoneId + "\', " + driverId + " )\n");
            Main.statement.executeUpdate(insertQuery);
//            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
//                preparedStatement.setLong(1, permitId);
//                preparedStatement.setString(2, permitType);
//                preparedStatement.setDate(3, java.sql.Date.valueOf(startDate));
//                preparedStatement.setDate(4, java.sql.Date.valueOf(expDate));
//                preparedStatement.setTime(5, java.sql.Time.valueOf(expTime));
//                preparedStatement.setString(6, assignedSpaceType);
//                preparedStatement.setString(7, assignedZoneId);
//                preparedStatement.setLong(8, driverId);
//
//                // Execute the insert query
//                int rowsAffected = preparedStatement.executeUpdate();
//
//                if (rowsAffected > 0) {
//                    System.out.println("Permit information inserted successfully.");
//                } else {
//                    System.out.println("Failed to insert Permit information.");
//                }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw new SQLException("Error in Permit insertion SQL statement.");
        }
    }


    private static void viewPermit() throws SQLException {
        String query = "Select * from Permit";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);

    }
}
//
//    private static void deletePermit() throws SQLException {
//        Scanner scanner = new Scanner(System.in);
//
//        boolean exit = false;
//        while (!exit) {
//            System.out.println("1. Delete driver information by driver ID");
//            System.out.println("2. Delete driver information by driver name");
//            System.out.println("3. Return to the driver screen");
//
//            int choice;
//            while (true) {
//                try {
//                    System.out.print("Enter your choice: ");
//                    choice = Integer.parseInt(scanner.nextLine());
//                    break;
//                } catch (Exception e) {
//                    System.out.println("Please enter a valid choice (numerical)");
//                }
//            }
//
//            switch (choice) {
//                case 1 -> deleteDriverById();
//                case 2 -> deleteDriverByName();
//                case 3 -> {
//                    System.out.println("Back to driver menu");
//                    exit = true;
//                }
//                default -> System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }
//
//    private static void deleteDriverById() throws SQLException {
//        scanner = new Scanner(System.in);
//        System.out.println("Enter Driver Id To Be Deleted: ");
//        int driverId = scanner.nextInt();
//
//        boolean driverExists = false;
//        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Driver WHERE DriverID = \'" + driverId + "\'");
//        if (rs.next()) {
//            driverExists = true;
//        }
//
//        if (driverExists) {
//            Main.statement.executeUpdate("DELETE FROM Driver WHERE DriverID = \'" + driverId + "\';");
//            System.out.println("Driver row with ID " + driverId + " deleted successfully.");
//        } else {
//            System.out.println("Incorrect driver ID entered. Please select from the below driver IDs");
//            ResultSet ids = Main.statement.executeQuery("SELECT DriverID FROM Driver;");
//            while (ids.next()) {
//                System.out.println(ids.getInt("DriverID"));
//            }
//            System.out.println();
//        }
//    }
//
//    private static void deleteDriverByName() throws SQLException {
//        System.out.println("Enter Driver Name To Be Deleted: ");
//        int driverName = scanner.nextInt();
//
//        boolean driverExists = false;
//        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Driver WHERE DriverName = \'" + driverName + "\'");
//        if (rs.next()) {
//            driverExists = true;
//        }
//
//        if (driverExists) {
//            Main.statement.executeUpdate("DELETE FROM Driver WHERE DriverName = \'" + driverName + "\';");
//            System.out.println("Driver row with Name " + driverName + " deleted successfully.");
//        } else {
//            System.out.println("Incorrect driver Name entered. Please select from the below drivers");
//            ResultSet name = Main.statement.executeQuery("SELECT DriverID FROM Driver;");
//            while (name.next()) {
//                System.out.println(name.getInt("DriverName"));
//            }
//            System.out.println();
//        }
//    }
//
//    private static void updateDriver() {
//    }
//}

