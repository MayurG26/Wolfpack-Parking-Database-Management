import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Driver {
    static Scanner scanner = new Scanner(System.in);

    public static void driverOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Insert driver Information");
            System.out.println("2. View all driver information");
            System.out.println("3. Update driver information");
            System.out.println("4. Delete driver information");
            System.out.println("5. Return to the home screen");

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
                case 1 -> insertDriver();
                case 2 -> viewDriver();
                case 3 -> updateDriver();
                case 4 -> deleteDriver();
                case 5 -> {
                    System.out.println("Back to home menu");
                    exit = true;
                }
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    private static void insertDriver() throws SQLException {
        System.out.println("\nEnter Driver Name: ");
        String driverName = scanner.nextLine();
        BigInteger driverId = null;

        while (true) {
            try {
                System.out.println("Enter Driver ID: ");
                driverId = new BigInteger(String.valueOf(Long.parseLong(scanner.nextLine())));
                break;
            } catch (Exception e) {
                System.out.println("Please enter a valid driver id (numerical)");
            }
        }

        System.out.println("Enter Driver Status: ");
        String driverStatus = scanner.nextLine();

        if(doesDriverIDExist(driverId)){
            System.out.println("Driver ID already exists. Please try again.");
            System.out.println("Following is the existing driver information: \n");
            Main.printTable("Driver");
        }else{
            Main.statement.executeUpdate("INSERT INTO Driver(DriverID,DriverName,Status) VALUES ("+ driverId+",\'"+driverName+"\', \'"+driverStatus+"\')");
            System.out.println("Driver information entered successfully.");
        }
    }

    public static void viewDriver() throws SQLException {
        String query = "Select * from Driver";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }

    private static void deleteDriver() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Delete driver information by driver ID");
            System.out.println("2. Delete driver information by driver name");
            System.out.println("3. Return to the driver screen");

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
                case 1 -> deleteDriverById();
                case 2 -> deleteDriverByName();
                case 3 -> {
                    System.out.println("Back to driver menu");
                    exit = true;
                }
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    /**
     * add name condition
     *
     * @throws SQLException
     */
    private static void deleteDriverById() throws SQLException {
        System.out.println("\nEnter Driver Id To Be Deleted: ");
        BigInteger driverId = scanner.nextBigInteger();
        scanner.nextLine();

        if(doesDriverIDExist(driverId)) {
            Main.statement.executeUpdate("DELETE FROM Driver WHERE DriverID = '" + driverId + "';");
            System.out.println("Driver row with ID " + driverId + " deleted successfully.");
        } else {
            System.out.println("Incorrect driver ID entered. Please select from the below driver IDs");
            ResultSet ids = Main.statement.executeQuery("SELECT DriverID FROM Driver;");
            while (ids.next()) {
                System.out.println(ids.getBigDecimal("DriverID").toBigInteger());
            }
            System.out.println();
        }
    }

    private static void deleteDriverByName() throws SQLException {
        System.out.println("Enter Driver Name To Be Deleted: ");
        String driverName = scanner.nextLine();

        boolean driverExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Driver WHERE DriverName = \'" + driverName + "\'");
        if (rs.next()) {
            driverExists = true;
        }

        if (driverExists) {
            Main.statement.executeUpdate("DELETE FROM Driver WHERE DriverName = \'" + driverName + "\';");
            System.out.println("Driver row with Name " + driverName + " deleted successfully.");
        } else {
            System.out.println("Incorrect driver Name entered. Please select from the below drivers");
            ResultSet name = Main.statement.executeQuery("SELECT DriverName FROM Driver;");
            while (name.next()) {
                System.out.println(name.getString("DriverName"));
            }
            System.out.println();
        }
    }

    private static void updateDriver() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Update driver ID");
            System.out.println("2. Update driver name");
            System.out.println("3. Update driver status");
            System.out.println("4. Return to the driver screen");

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
                    BigInteger driverId = getDriverIdDetails();
                    System.out.println("\nEnter new driver ID: ");
                    BigInteger newDriverId = scanner.nextBigInteger();
                    scanner.nextLine();

                    if (doesDriverIDExist(newDriverId)){
                        System.out.println("Driver ID already exists. Please try again.");
                    }else {
                        Main.statement.executeUpdate("UPDATE Driver SET DriverID = " + newDriverId + " WHERE DriverID = " + driverId + ";");
                        System.out.println("Driver ID updated successfully.");
                    }
                }
                case 2 -> {
                    BigInteger driverId = getDriverIdDetails();
                    System.out.println("\nEnter new driver name: ");
                    String newDriverName = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Driver SET DriverName = \'" + newDriverName + "\' WHERE DriverID = " + driverId + ";");
                    System.out.println("Driver name updated successfully.");
                }
                case 3 -> {
                    BigInteger driverId = getDriverIdDetails();
                    System.out.println("\nEnter new driver status: ");
                    String newDriverStatus = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Driver SET Status = \'" + newDriverStatus + "\' WHERE DriverID = " + driverId + ";");
                    System.out.println("Driver status updated successfully.");
                }
                case 4 -> {
                    System.out.println("Back to driver menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static BigInteger getDriverIdDetails() throws SQLException {
        boolean flag = false;
        BigInteger driverId = null;
        while (!flag) {
            System.out.println("\nEnter driver ID to be updated: ");
            driverId = scanner.nextBigInteger();
            scanner.nextLine();

            if (!doesDriverIDExist(driverId)) {
                System.out.println("Incorrect driver ID entered. Please select from the below driver IDs");
                ResultSet ids = Main.statement.executeQuery("SELECT DriverID FROM Driver;");
                while (ids.next()) {
                    System.out.println(ids.getBigDecimal("DriverID").toBigInteger());
                }
                System.out.println();
            }else{
                flag = true;
            }
        }
        return driverId;
    }

    private static boolean doesDriverIDExist(BigInteger driverId) throws SQLException {
        boolean driverExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Driver WHERE DriverID = " + driverId);
        if (rs.next()) {
            driverExists = true;
        }
        return driverExists;
    }
}
