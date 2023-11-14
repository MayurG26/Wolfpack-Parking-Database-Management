import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Driver {
    static Scanner scanner = new Scanner(System.in);

    public static void driverOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Insert driver Information");
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
                    System.out.println("Please enter a valid choice (numerical)");
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
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void insertDriver() {
        System.out.println("Enter Driver Name: ");
        String driverName = scanner.nextLine();
        System.out.println("Enter Driver ID: ");
        int driverId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter Driver Status: ");
        String driverStatus = scanner.nextLine();
    }

    public static void viewDriver() throws SQLException {
        String query = "Select * from Driver";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }

    private static void deleteDriver() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            System.out.println("1. Delete driver information by driver ID");
            System.out.println("2. Delete driver information by driver name");
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
                case 1 -> deleteDriverById();
                case 2 -> deleteDriverByName();
                case 3 -> {
                    System.out.println("Back to driver menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void deleteDriverById() throws SQLException {
        scanner = new Scanner(System.in);
        System.out.println("Enter Driver Id To Be Deleted: ");
        int driverId = scanner.nextInt();

        boolean driverExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Driver WHERE DriverID = \'" + driverId + "\'");
        if (rs.next()) {
            driverExists = true;
        }

        if (driverExists) {
            Main.statement.executeUpdate("DELETE FROM Driver WHERE DriverID = \'" + driverId + "\';");
            System.out.println("Driver row with ID " + driverId + " deleted successfully.");
        } else {
            System.out.println("Incorrect driver ID entered. Please select from the below driver IDs");
            ResultSet ids = Main.statement.executeQuery("SELECT DriverID FROM Driver;");
            while (ids.next()) {
                System.out.println(ids.getInt("DriverID"));
            }
            System.out.println();
        }
    }

    private static void deleteDriverByName() throws SQLException {
        System.out.println("Enter Driver Name To Be Deleted: ");
        int driverName = scanner.nextInt();

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
            ResultSet name = Main.statement.executeQuery("SELECT DriverID FROM Driver;");
            while (name.next()) {
                System.out.println(name.getInt("DriverName"));
            }
            System.out.println();
        }
    }

    private static void updateDriver() {
    }
}
