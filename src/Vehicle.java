import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Vehicle {
    static Scanner scanner = new Scanner(System.in);

    public static void vehicleOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Insert vehicle Information");
            System.out.println("2. View all vehicle information");
            System.out.println("3. Update vehicle information");
            System.out.println("4. Delete vehicle information");
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
                case 1 -> insertVehicle();
                case 2 -> viewVehicle();
                case 3 -> updateVehicle();
                case 4 -> deleteVehicle();
                case 5 -> {
                    System.out.println("Back to home menu");
                    exit = true;
                }
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    //Inserting without permit ID throws error

    private static void insertVehicle() throws SQLException {
        System.out.println("\nEnter License Number of Vehicle: ");
        String licenseNo = scanner.nextLine();
        System.out.println("Enter vehicle model: ");
        String model = scanner.nextLine();
        System.out.println("Enter manufacturer of vehicle (Press enter to skip): ");
        String manf = scanner.nextLine();
        System.out.println("Enter color of vehicle: ");
        String color = scanner.nextLine();
        System.out.println("Enter year of vehicle: ");
        String year = scanner.nextLine();
//        System.out.println("Enter permit ID of vehicle (Enter N/A if no permit ID): ");
//        String permitID = scanner.nextLine();

        if (doesLicenseNoExist(licenseNo)) {
            System.out.println("License Number already exists. Please try again.");
            System.out.println("Following is the existing vehicle information: \n");
            Main.printTable("Vehicle");
        } else {
            Main.statement.executeUpdate("INSERT INTO Vehicle(LicenseNo,Model,Manf, Color, Year) VALUES (\'" + licenseNo + "\',\'" + model + "\', \'" + manf + "\',\'" + color + "\',\'" + year + "\')");
            System.out.println("Vehicle information entered successfully.");
        }
    }

    private static void viewVehicle() throws SQLException {
        String query = "SELECT * FROM Vehicle";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }

    private static void deleteVehicle() throws SQLException {
        System.out.println("\nEnter license number of vehicle to be deleted: ");
        String licenseNo = scanner.nextLine();

        if (doesLicenseNoExist(licenseNo)) {
            Main.statement.executeUpdate("DELETE FROM Vehicle WHERE LicenseNo = \'" + licenseNo + "\';");
            System.out.println("Vehicle row with license number " + licenseNo + " deleted successfully.");
        } else {
            System.out.println("Incorrect license number entered. Please select from the below license numbers:");
            ResultSet ids = Main.statement.executeQuery("SELECT LicenseNo FROM Vehicle;");
            while (ids.next()) {
                System.out.println(ids.getBigDecimal("LicenseNo").toBigInteger());
            }
            System.out.println();
        }
    }

    private static void updateVehicle() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Update vehicle license number");
            System.out.println("2. Update vehicle model");
            System.out.println("3. Update vehicle manufacturer");
            System.out.println("4. Update vehicle color");
            System.out.println("5. Update vehicle year");
            System.out.println("6. Return to the vehicle screen");

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
                    String licenseNo = getVehicleLicenseDetails();
                    System.out.println("\nEnter new license number: ");
                    String newLicenseNo = scanner.nextLine();

                    if (doesLicenseNoExist(newLicenseNo)) {
                        System.out.println("License number already exists. Please try again.");
                    } else {
                        Main.statement.executeUpdate("UPDATE Vehicle SET LicenseNo = \'" + newLicenseNo + "\' WHERE LicenseNo = \'" + licenseNo + "\';");
                        System.out.println("License number updated successfully.");
                    }
                }
                case 2 -> {
                    String licenseNo = getVehicleLicenseDetails();
                    System.out.println("\nEnter new vehicle model: ");
                    String model = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Vehicle SET Model = \'" + model + "\' WHERE LicenseNo = \'" + licenseNo + "\';");
                    System.out.println("Vehicle model updated successfully.");
                }
                case 3 -> {
                    String licenseNo = getVehicleLicenseDetails();
                    System.out.println("\nEnter new vehicle manufacturer: ");
                    String manf = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Vehicle SET Manf = \'" + manf + "\' WHERE LicenseNo = \'" + licenseNo + "\';");
                    System.out.println("Vehicle manufacturer updated successfully.");
                }
                case 4 -> {
                    String licenseNo = getVehicleLicenseDetails();
                    System.out.println("\nEnter new vehicle color: ");
                    String color = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Vehicle SET Color = \'" + color + "\' WHERE LicenseNo = \'" + licenseNo + "\';");
                    System.out.println("Vehicle color updated successfully.");
                }
                case 5 -> {
                    String licenseNo = getVehicleLicenseDetails();
                    System.out.println("\nEnter new vehicle year: ");
                    String year = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Vehicle SET Year = \'" + year + "\' WHERE LicenseNo = \'" + licenseNo + "\';");
                    System.out.println("Vehicle year updated successfully.");
                }
                case 6 -> {
                    System.out.println("<-- Back to vehicle menu.");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static String getVehicleLicenseDetails() throws SQLException {
        boolean flag = false;
        String licenseNo = null;
        while (!flag) {
            System.out.println("\nEnter license number to be updated: ");
            licenseNo = scanner.nextLine();

            if (!doesLicenseNoExist(licenseNo)) {
                System.out.println("Incorrect license number entered. Please select from the below license numbers:");
                ResultSet licenses = Main.statement.executeQuery("SELECT LicenseNo FROM Vehicle;");
                while (licenses.next()) {
                    System.out.println(licenses.getString("LicenseNo"));
                }
                System.out.println();
            } else {
                flag = true;
            }
        }
        return licenseNo;
    }

    private static boolean doesLicenseNoExist(String licenseNo) throws SQLException {
        boolean vehicleExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Vehicle WHERE LicenseNo = \'" + licenseNo+"\';");
        if (rs.next()) {
            vehicleExists = true;
        }
        return vehicleExists;
    }
}
