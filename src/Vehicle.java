import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
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
            System.out.println("5. Assign Vehicle to permit");
            System.out.println("6. Remove Vehicle from permit");
            System.out.println("7. Return to the home screen");

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
            try {
                switch (choice) {
                    case 1 -> insertVehicle();
                    case 2 -> viewVehicle();
                    case 3 -> updateVehicle();
                    case 4 -> deleteVehicle();
                    case 5 -> assignVehicleToPermit();
                    case 6 -> removeVehicleFromPermit();
                    case 7 -> {
                        System.out.println("<-- Back to home menu");
                        exit = true;
                    }
                    default -> System.out.println("\nInvalid choice. Please try again.");
                }
            } catch (SQLException e) {
                System.out.println("Something went wrong! Please try again. ");
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

        if (doesLicenseNoExist(licenseNo)) {
            System.out.println("License Number already exists. Please try again.");
            System.out.println("Following is the existing vehicle information: \n");
            Main.printTable("Vehicle");
        } else {
            Main.statement.executeUpdate("INSERT INTO Vehicle(LicenseNo,Model,Manf, Color, Year) VALUES (\'" + licenseNo + "\',\'" + model + "\', \'" + manf + "\',\'" + color + "\',\'" + year + "\')");
            System.out.println("Vehicle information entered successfully.");
        }
    }

    public static void viewVehicle() throws SQLException {
        ResultSet rs = Main.statement.executeQuery("SELECT LicenseNo, Model, Manf, Color, Year FROM Vehicle;");
        DBTablePrinter.printResultSet(rs);
    }

    private static void deleteVehicle() throws SQLException {
        try {
            Main.connection.setAutoCommit(false);
            System.out.println("\nEnter license number of vehicle to be deleted: ");
            String licenseNo = scanner.nextLine();

            String vehicleDeleteQuery = "DELETE FROM Vehicle WHERE EXISTS (Select 1 from Citation WHERE LicenseNo = \'" + licenseNo + "\' AND PaymentStatus = 'PAID' OR AppealStatus = 'APPROVED');";
            List<String> cNumbers = Citation.getCitationNumberFromLicense(licenseNo);
            if (!cNumbers.isEmpty()) {
                for (String value : cNumbers) {
                    System.out.println(value);
                    Main.statement.executeUpdate("DELETE FROM Encompasses WHERE CNumber = \'" + value + "\';");
                    PreparedStatement ps = Main.connection.prepareStatement("UPDATE Citation SET LicenseNo = ? WHERE CNumber = \'" + value + "\' AND (PaymentStatus = 'PAID' OR AppealStatus = 'APPROVED');");
                    ps.setNull(1,Types.VARCHAR);
                    ps.executeUpdate();
                    if (!Main.statement.execute(vehicleDeleteQuery)) {
                        System.out.println("Cannot remove the vehicle due to unpaid citations.");
                        Main.connection.rollback();
                        return;
                    }
                    Main.statement.executeUpdate("DELETE FROM Citation WHERE CNumber = \'" + value + "\' AND (PaymentStatus = 'PAID' OR AppealStatus = 'APPROVED');");
                }
                System.out.println("Vehicle row with license number " + licenseNo + " deleted successfully.");
            } else {
                Main.statement.executeUpdate("DELETE FROM Vehicle WHERE LicenseNo = \'" + licenseNo + "\';");
            }
            Main.connection.commit();
        } finally {
            Main.connection.setAutoCommit(true);
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

    public static boolean doesLicenseNoExist(String licenseNo) throws SQLException {
        boolean vehicleExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Vehicle WHERE LicenseNo = \'" + licenseNo + "\';");
        if (rs.next()) {
            vehicleExists = true;
        }
        return vehicleExists;
    }

    public static void assignVehicleToPermit() throws SQLException {
        String permitId, licenseNo;
        do {
            System.out.println("Enter Permit ID: ");
            permitId = scanner.nextLine();
            if (!Permit.doesPermitIDExist(permitId))
                System.out.println("Incorrect Permit ID entered. Please try again.");
        } while (!Permit.doesPermitIDExist(permitId));
        do {
            System.out.println("Enter License Number: ");
            licenseNo = scanner.nextLine();
            if (!Vehicle.doesLicenseNoExist(licenseNo))
                System.out.println("Incorrect license number entered. Please try again.");
        } while (!Vehicle.doesLicenseNoExist(licenseNo));

        assignVehicleToPermit(permitId, licenseNo);
    }

    public static void assignVehicleToPermit(String permitID, String licenseNo) throws SQLException {
        int cs = 0;
        String insertQuery = "UPDATE Vehicle SET PermitID = \'" + permitID + "\' WHERE LicenseNo = \'" + licenseNo + "\';";
        String status = null;
        BigInteger driverID = BigInteger.valueOf(0);
        if (!doesLicenseNoExist(licenseNo)) {
            throw new SQLException("Vehicle with the given License number does not exist");
        }
        if (Permit.doesPermitIDExist(permitID)) {
            ResultSet id = Main.statement.executeQuery("SELECT DriverID FROM Permit WHERE PermitID = \'" + permitID + "\';");
            if (id.next()) {
                driverID = id.getBigDecimal("DriverID").toBigInteger();
            } else {
                System.out.println("Driver ID not found for the given permit. Please try again.");
            }
            ResultSet rs = Main.statement.executeQuery("SELECT Status FROM Driver WHERE DriverID = " + driverID + ";");
            if (rs.next()) {
                status = rs.getString("Status");
                String countQuery = "Select Count(PermitID) AS CountP from Vehicle WHERE PermitID = \'" + permitID + "\';";
                ResultSet cns = Main.statement.executeQuery(countQuery);
                if (cns.next()) {
                    cs = cns.getInt("CountP");
                    System.out.println(cs);
                }
            }
            HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
            hashMap.put("E", 2);
            hashMap.put("S", 1);
            hashMap.put("V", 1);
            int expectedCount = hashMap.get(status);
            if (cs < expectedCount) {
                Main.statement.executeUpdate(insertQuery);
                System.out.println("Vehicle " + licenseNo + " assigned to permit " + permitID + " successfully.");
            } else {
                System.out.println(Permit.CANNOT_ASSIGN_PERMIT_MESSAGE);
            }

        } else {
            System.out.println("Incorrect permit ID entered. Please select from the below permit IDs:");
            Permit.printAllPermitIds();
        }
    }

    public static void removeVehicleFromPermit() throws SQLException {
        boolean flag = false;
        String permitID, licenseNo;
        do {
            System.out.println("Enter Permit ID: ");
            permitID = scanner.nextLine();
            if (!Permit.doesPermitIDExist(permitID))
                System.out.println("Incorrect Permit ID entered. Please try again.");
        } while (!Permit.doesPermitIDExist(permitID));
        do {
            System.out.println("Enter License Number: ");
            licenseNo = scanner.nextLine();
            if (!Vehicle.doesLicenseNoExist(licenseNo))
                System.out.println("Incorrect license number entered. Please try again.");
        } while (!Vehicle.doesLicenseNoExist(licenseNo));

        List<String> cNumbers = Citation.getCitationNumberFromLicense(licenseNo);
        if (!cNumbers.isEmpty()) {// needs work
            System.out.println("Cannot remove the vehicle due to following citations: ");
            for (String value : cNumbers) {
                System.out.println(value);
            }
        } else {
            while (!flag) {
                if (Permit.doesPermitIDExist(permitID)) {
                    if (doesLicenseNoExist(licenseNo)) {
                        flag = true;
                        PreparedStatement ps = Main.connection.prepareStatement("UPDATE Vehicle SET PermitID = ? WHERE LicenseNo = \'" + licenseNo + "\';");
                        ps.setNull(1, Types.VARCHAR);
                        ps.executeUpdate();
                        System.out.println("Vehicle " + licenseNo + " removed from permit " + permitID + " successfully.");
                    } else {
                        System.out.println("Incorrect license number entered. Please select from the below license numbers:");
                        printAllLicenseNumbers();
                    }
                } else {
                    System.out.println("Incorrect permit ID entered. Please select from the below permit IDs:");
                    Permit.printAllPermitIds();
                }
            }
        }
    }

    public static void printAllLicenseNumbers() throws SQLException {
        ResultSet licenses = Main.statement.executeQuery("SELECT LicenseNo FROM Vehicle;");
        while (licenses.next()) {
            System.out.println(licenses.getString("LicenseNo"));
        }
        System.out.println();
    }


}