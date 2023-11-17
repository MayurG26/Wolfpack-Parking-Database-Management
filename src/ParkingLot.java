import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ParkingLot {

    static Scanner scanner = new Scanner(System.in);

    public static void lotOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Parking Lot Information");
            System.out.println("2. Zone information");
            System.out.println("3. Space information");
            System.out.println("4. Back to home menu");
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
                    case 1 -> parkingLotOptions();
                    case 2 -> zoneOptions();
                    case 3 -> spacesOptions();
                    case 4 -> {
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

    private static void parkingLotOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Insert Parking Lot Information");
            System.out.println("2. View all Parking Lot information");
            System.out.println("3. Update Parking Lot Information");
            System.out.println("4. Delete Parking Lot Information");
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
                case 1 -> insertParkingLot();
                case 2 -> viewParkingLot();
                case 3 -> updateParkingLot();
                case 4 -> deleteParkingLot();
                case 5 -> {
                    System.out.println("Back to home menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void zoneOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Insert Zone Information");
            System.out.println("2. View all Zone information");
            System.out.println("3. Update Zone Information");
            System.out.println("4. Delete Zone Information");
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
                case 1 -> insertZones();
                case 2 -> viewZones();
                case 3 -> updateZones();
                case 4 -> deleteZones();
                case 5 -> {
                    System.out.println("Back to home menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void spacesOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Insert Space Information");
            System.out.println("2. View all Space information");
            System.out.println("3. Update Space Information");
            System.out.println("4. Delete Space Information");
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
                case 1 -> insertSpaces();
                case 2 -> viewSpaces();
                case 3 -> updateSpaces();
                case 4 -> deleteSpaces();
                case 5 -> {
                    System.out.println("Back to home menu");
                    System.out.println();
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void insertParkingLot() throws SQLException {
        System.out.println("Enter Lot Name: ");
        String lotName = scanner.nextLine();
        System.out.println("Enter Lot Address: ");
        String lotAddress = scanner.nextLine();
        ;
        int numSpaces;
        while (true) {
            try {
                System.out.println("Enter number of spaces: ");
                numSpaces = Integer.parseInt(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Please enter number of spaces in numerical");
            }
        }
        int numZones;
        while (true) {
            try {
                System.out.println("Enter number of zones: ");
                numZones = Integer.parseInt(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Please enter number of zones in numerical");
            }
        }
        if (!doesParkingLotExist(lotName)) {
            Main.statement.executeUpdate("INSERT INTO  ParkingLot (LotName,Address,NumSpace,NumZone) VALUES (\'" + lotName + "\',\'" + lotAddress + "\'," + numSpaces + "," + numZones + ")");
            System.out.println("New Parking Lot is added in the database ");
        } else {
            System.out.println("Parking lot with the given name already exists!");
        }
    }

    private static void viewParkingLot() throws SQLException {
        String query = "Select * from ParkingLot";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }

    private static void deleteParkingLot() throws SQLException {
        scanner = new Scanner(System.in);
        System.out.println("Enter Lot name To Be Deleted: ");
        String lotName = scanner.nextLine();

        boolean lotExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM ParkingLot WHERE LotName = \'" + lotName + "\'");
        if (rs.next()) {
            lotExists = true;
        }

        if (lotExists) {
            Main.statement.executeUpdate("DELETE FROM Encompasses WHERE LotName = \'" + lotName + "\';");
            Main.statement.executeUpdate("DELETE FROM Comprises WHERE LotName = \'" + lotName + "\';");
            Main.statement.executeUpdate("DELETE FROM Space WHERE LotName = \'" + lotName + "\';");
            Main.statement.executeUpdate("DELETE FROM Zone WHERE LotName = \'" + lotName + "\';");
            Main.statement.executeUpdate("DELETE FROM ParkingLot WHERE LotName = \'" + lotName + "\';");
            System.out.println("Parking Lot with Name " + lotName + " deleted successfully.");
        } else {
            System.out.println("Incorrect lot name entered. Please select from the below lot names");
            ResultSet names = Main.statement.executeQuery("SELECT LotName FROM ParkingLot;");
            while (names.next()) {
                System.out.println(names.getString("LotName"));
            }
            System.out.println();
        }
    }

    private static void insertZones() throws SQLException {
        System.out.println("Enter the Lot Name where the zone id is : ");
        String lotName = scanner.nextLine();
        System.out.println("Enter the zone id: ");
        String zone = scanner.nextLine();
        boolean lotExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM ParkingLot WHERE LotName = \'" + lotName + "\'");
        if (rs.next()) {
            lotExists = true;
        }
        if (lotExists) {
            if (!doesZoneExist(lotName, zone)) {
                Main.statement.executeUpdate("INSERT INTO  Zone (LotName,ZoneID) VALUES (\'" + lotName + "\',\'" + zone + "\')");
                System.out.println("Zone id was assigned in the given Parking Lot");
            } else {
                System.out.println("Zone id already exists!");
            }
        } else {
            System.out.println("Incorrect lot name entered. Please select from the below lot names");
            ResultSet names = Main.statement.executeQuery("SELECT LotName FROM ParkingLot;");
            while (names.next()) {
                System.out.println(names.getString("LotName"));
            }
            System.out.println();
        }
    }

    private static void viewZones() throws SQLException {
        String query = "Select * from Zone";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }

    private static void deleteZones() throws SQLException {
        System.out.println("Enter Zone id To Be Deleted: ");
        String zone = scanner.nextLine();
        System.out.println("Enter the Parking Lot from where the Zone needs To Be Deleted: ");
        String lotName = scanner.nextLine();

        boolean zoneExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Zone WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone + "\'");
        if (rs.next()) {
            zoneExists = true;
        }

        if (zoneExists) {
            Main.statement.executeUpdate("DELETE FROM Comprises WHERE LotName = \'" + lotName + "\'and ZoneID = \'" + zone + "\';");
            Main.statement.executeUpdate("DELETE FROM Space WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone + "\';");
            Main.statement.executeUpdate("DELETE FROM Zone WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone + "\';");
            System.out.println("Zone with ID " + zone + " from Lot " + lotName + " deleted successfully.");
        } else {
            System.out.println("Incorrect Zone entered. Please select from the below Zones");
            ResultSet name = Main.statement.executeQuery("SELECT ZoneID, LotName FROM Zone;");
            while (name.next()) {
                System.out.println("Zone = " + name.getString("ZoneID") + " Lot = " + name.getString("LotName"));
            }
            System.out.println();
        }
    }

    public static void insertSpaces() throws SQLException {
        System.out.println("Enter the Lot Name where the space is : ");
        String lotName = scanner.nextLine();
        System.out.println("Enter the zone id: ");
        String zone = scanner.nextLine();
        int number;
        while (true) {
            try {
                System.out.println("Enter the space number: ");
                number = Integer.parseInt(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Please enter space number in numerical");
            }
        }
        System.out.println("Enter the Avaibility Status: ");
        String status = scanner.nextLine();
        boolean zoneExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Zone WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone + "\'");
        if (rs.next()) {
            zoneExists = true;
        }
        if (zoneExists) {
            if (!doesSpaceExist(lotName, zone, number)) {
                String query = "INSERT INTO Space (SpaceNumber, LotName, ZoneID,AvailStatus) VALUES (" + number + " , \'" + lotName + "\', \'" + zone + "\',\'" + status + "\')";
                Main.statement.executeUpdate(query);
                System.out.println("Space Number was inserted in the given Parking Lot");
            } else {
                System.out.println("Space number already exists in given Zone and Parking lot!");
            }
        } else {
            System.out.println("Incorrect Zone id entered. Please select from the below Zones");
            ResultSet name = Main.statement.executeQuery("SELECT ZoneID, LotName FROM Zone;");
            while (name.next()) {
                System.out.println("Zone = " + name.getString("ZoneID") + " Lot = " + name.getString("LotName"));
            }
            System.out.println();
        }

    }

    private static void viewSpaces() throws SQLException {
        String query = "Select * from Space";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }

    private static void deleteSpaces() throws SQLException {
        int number;
        while (true) {
            try {
                System.out.println("Enter Space Number To Be Deleted: ");
                number = Integer.parseInt(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Please enter space number in numerical");
            }
        }
        System.out.println("Enter the Zone id from where the Space Number needs To Be Deleted: ");
        String zone = scanner.nextLine();
        System.out.println("Enter the Parking Lot from where the Space Number needs To Be Deleted: ");
        String lotName = scanner.nextLine();

        boolean spaceExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Space WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone + "\' and SpaceNumber = " + number);
        if (rs.next()) {
            spaceExists = true;
        }

        if (spaceExists) {
            Main.statement.executeUpdate("DELETE FROM Comprises WHERE LotName = \'" + lotName + "\'and ZoneID = \'" + zone + "\' and SpaceNumber = " + number);
            Main.statement.executeUpdate("DELETE FROM Space WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone + "\' and SpaceNumber = " + number);
            System.out.println("Space Number " + number + " with Zone ID " + zone + " from Lot " + lotName + " deleted successfully.");
        } else {
            System.out.println("Incorrect Space entered. Please select from the below Spaces");
            ResultSet name = Main.statement.executeQuery("SELECT SpaceNumber,ZoneID, LotName FROM Space;");
            while (name.next()) {
                System.out.println("Space Number = " + name.getInt("SpaceNumber") + " Zone = " + name.getString("ZoneID") + " Lot = " + name.getString("LotName"));
            }
            System.out.println();
        }
    }

    private static void updateParkingLot() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Update Parking Lot name");
            System.out.println("2. Update Parking Lot address");
            System.out.println("3. Update number of spaces");
            System.out.println("4. Update number of zones");
            System.out.println("5. Return to the Parking Lot screen");

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
                    String name = getParkingLotDetails();
                    System.out.println("\nEnter new Parking Lot name: ");
                    String newName = scanner.nextLine();

                    if (doesParkingLotExist(newName)) {
                        System.out.println("Parking Lot already exists. Please try again.");
                    } else {
                        Main.statement.executeUpdate("UPDATE ParkingLot SET LotName = \'" + newName + "\' WHERE LotName = \'" + name + "\';");
                        System.out.println("Parking Lot updated successfully.");
                    }
                }
                case 2 -> {
                    String name = getParkingLotDetails();
                    System.out.println("\nEnter new Parking Lot address: ");
                    String newAddress = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE ParkingLot SET Address = \'" + newAddress + "\' WHERE LotName = \'" + name + "\';");
                    System.out.println("Parking Lot address  updated successfully.");
                }
                case 3 -> {
                    String name = getParkingLotDetails();
                    Integer newNumSpace;
                    while (true) {
                        try {
                            System.out.println("Enter new number of spaces: ");
                            newNumSpace = Integer.parseInt(scanner.nextLine());
                            break;
                        } catch (Exception e) {
                            System.out.println("Please enter number of spaces in numerical");
                        }
                    }
                    Main.statement.executeUpdate("UPDATE ParkingLot SET NumSpace = " + newNumSpace + " WHERE LotName = \'" + name + "\';");
                    System.out.println("Number of spaces updated successfully.");
                }
                case 4 -> {
                    String name = getParkingLotDetails();
                    Integer newNumZone;
                    while (true) {
                        try {
                            System.out.println("Enter new number of zones: ");
                            newNumZone = Integer.parseInt(scanner.nextLine());
                            break;
                        } catch (Exception e) {
                            System.out.println("Please enter number of zones in numerical");
                        }
                    }
                    Main.statement.executeUpdate("UPDATE ParkingLot SET NumZone = " + newNumZone + " WHERE LotName = \'" + name + "\';");
                    System.out.println("Number of zones updated successfully.");
                }
                case 5 -> {
                    System.out.println("Back to Parking Lot menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static String getParkingLotDetails() throws SQLException {
        boolean flag = false;
        String name = null;
        while (!flag) {
            System.out.println("\nEnter Parking Lot name to be updated: ");
            name = scanner.nextLine();

            if (!doesParkingLotExist(name)) {
                System.out.println("Incorrect Parking Lot entered. Please select name from the below Parking Lot");
                ResultSet names = Main.statement.executeQuery("SELECT LotName FROM ParkingLot;");
                while (names.next()) {
                    System.out.println(names.getString("LotName").toString());
                }
                System.out.println();
            } else {
                flag = true;
            }
        }
        return name;
    }

    public static boolean doesParkingLotExist(String name) throws SQLException {
        boolean lotExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM ParkingLot WHERE LotName = \'" + name + "\'");
        if (rs.next()) {
            lotExists = true;
        }
        return lotExists;
    }

    private static void updateZones() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Update Zone ID");
            System.out.println("2. Update Lot name");
            System.out.println("3. Return to the Parking Lot screen");

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
                    ArrayList zone = getZoneDetails();
                    String name = (String) zone.get(0);
                    String id = (String) zone.get(1);
                    System.out.println("\nEnter new Zone ID: ");
                    String newID = scanner.nextLine();

                    if (doesZoneExist(name, newID)) {
                        System.out.println("Zone already exists. Please try again.");
                    } else {

                        Main.statement.executeUpdate("UPDATE Zone SET ZoneID = \'" + newID + "\' WHERE ZoneID = \'" + id + "\' AND LotName = \'" + name + "\'");
                        System.out.println("Parking Lot updated successfully.");
                    }
                }
                case 2 -> {
                    ArrayList zone = getZoneDetails();
                    String name = (String) zone.get(0);
                    String id = (String) zone.get(1);
                    System.out.println("\nEnter new Lot name: ");
                    String newLot = scanner.nextLine();

                    if (doesZoneExist(newLot, id)) {
                        System.out.println("Zone already exists. Please try again.");
                    } else {
                        boolean zoneExists = false;
                        ResultSet rs = Main.statement.executeQuery("SELECT * FROM ParkingLot WHERE LotName = \'" + newLot + "\'");
                        if (rs.next()) {
                            zoneExists = true;
                        }
                        if (zoneExists) {
                            Main.statement.executeUpdate("UPDATE Zone SET LotName = \'" + newLot + "\' WHERE ZoneID = \'" + id + "\' AND LotName = \'" + name + "\'");
                            System.out.println("Parking Lot and zones updated successfully.");
                        } else {
                            System.out.println("Incorrect Parking Lot entered. Please select from the below Lots");
                            ResultSet names = Main.statement.executeQuery("SELECT  LotName FROM ParkingLot;");
                            while (names.next()) {
                                System.out.println(" Lot = " + names.getString("LotName"));
                            }
                            System.out.println();
                        }

                    }
                }

                case 3 -> {
                    System.out.println("Back to Parking Lot menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static ArrayList getZoneDetails() throws SQLException {
        boolean flag = false;
        String name = null;
        String zone = null;
        while (!flag) {
            System.out.println("\nEnter Parking Lot name where zone needs to be updated: ");
            name = scanner.nextLine();
            System.out.println("\nEnter zone ID that needs to be updated: ");
            zone = scanner.nextLine();

            if (!doesZoneExist(name, zone)) {
                System.out.println("Incorrect Parking Lot entered. Please select name from the below Parking Lot");
                ResultSet names = Main.statement.executeQuery("SELECT * FROM Zone;");
                while (names.next()) {
                    System.out.println("Lot name = " + names.getString("LotName").toString() + ", Zone ID = " + names.getString("ZoneID").toString());
                }
                System.out.println();
            } else {
                flag = true;
            }
        }
        ArrayList<String> zones = new ArrayList<String>();
        zones.add(name);
        zones.add(zone);
        return zones;
    }

    public static boolean doesZoneExist(String name, String zone) throws SQLException {
        boolean zoneExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Zone WHERE ZoneID = \'" + zone + "\' AND LotName = \'" + name + "\'");
        if (rs.next()) {
            zoneExists = true;
        }
        return zoneExists;
    }

    private static void updateSpaces() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Update Space Number");
            System.out.println("2. Update Lot name");
            System.out.println("3. Update Zone ID");
            System.out.println("4. Update Availability status");
            System.out.println("5. Update Space Type");
            System.out.println("6. Return to the Parking Lot screen");

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
                    ArrayList space = getSpaceDetails();
                    String name = (String) space.get(0);
                    String id = (String) space.get(1);
                    Integer number = (Integer) space.get(2);
                    Integer newNumber;
                    while (true) {
                        try {
                            System.out.println("Enter new Space Number: ");
                            newNumber = Integer.parseInt(scanner.nextLine());
                            break;
                        } catch (Exception e) {
                            System.out.println("Please enter space number in numerical");
                        }
                    }

                    if (doesSpaceExist(name, id, newNumber)) {
                        System.out.println("Space already exists. Please try again.");
                    } else {
                        Main.statement.executeUpdate("UPDATE Space SET SpaceNumber = \'" + newNumber + "\' WHERE ZoneID = \'" + id + "\' AND LotName = \'" + name + "\' AND SpaceNumber = " + number);
                        System.out.println("Parking Lot Space updated successfully.");

                    }
                }
                case 2 -> {
                    ArrayList space = getSpaceDetails();
                    String name = (String) space.get(0);
                    String id = (String) space.get(1);
                    Integer number = (Integer) space.get(2);
                    System.out.println("\nEnter new Lot Name: ");
                    String newName = scanner.nextLine();

                    if (doesSpaceExist(newName, id, number)) {
                        System.out.println("Space already exists. Please try again.");
                    } else {
                        boolean zoneExists = false;
                        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Zone WHERE LotName = \'" + newName + "\' and ZoneID = \'" + id + "\'");
                        if (rs.next()) {
                            zoneExists = true;
                        }
                        if (zoneExists) {
                            Main.statement.executeUpdate("UPDATE Space SET LotName = \'" + newName + "\' WHERE ZoneID = \'" + id + "\' AND LotName = \'" + name + "\' AND SpaceNumber = " + number);
                            System.out.println("Parking Lot Space updated successfully.");
                        } else {
                            System.out.println("Incorrect Parking Lot entered. Please select from the below Lots");
                            ResultSet names = Main.statement.executeQuery("SELECT  LotName,ZoneID FROM Zone;");
                            while (names.next()) {
                                System.out.println(" Lot = " + names.getString("LotName") + " zone = " + names.getString("ZoneID"));
                            }
                            System.out.println();
                        }

                    }
                }
                case 3 -> {
                    ArrayList space = getSpaceDetails();
                    String name = (String) space.get(0);
                    String id = (String) space.get(1);
                    Integer number = (Integer) space.get(2);
                    System.out.println("\nEnter new Zone ID: ");
                    String newId = scanner.nextLine();

                    if (doesSpaceExist(name, newId, number)) {
                        System.out.println("Space already exists. Please try again.");
                    } else {
                        boolean zoneExists = false;
                        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Zone WHERE LotName = \'" + name + "\' and ZoneID = \'" + newId + "\'");
                        if (rs.next()) {
                            zoneExists = true;
                        }
                        if (zoneExists) {
                            Main.statement.executeUpdate("UPDATE Space SET ZoneID = \'" + newId + "\' WHERE ZoneID = \'" + id + "\' AND LotName = \'" + name + "\' AND SpaceNumber = " + number);
                            System.out.println("Parking Lot Space updated successfully.");
                        } else {
                            System.out.println("Incorrect Zone ID entered. Please select from the below zones");
                            ResultSet names = Main.statement.executeQuery("SELECT  ZoneID FROM Zone WHERE LotName = \'" + name + "\';");
                            while (names.next()) {
                                System.out.println(" Zone = " + names.getString("ZoneID"));
                            }
                            System.out.println();
                        }

                    }
                }
                case 4 -> {
                    ArrayList space = getSpaceDetails();
                    String name = (String) space.get(0);
                    String id = (String) space.get(1);
                    Integer number = (Integer) space.get(2);
                    System.out.println("\nEnter new Availability status: ");
                    String newStatus = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Space SET AvailStatus = \'" + newStatus + "\' WHERE ZoneID = \'" + id + "\' AND LotName = \'" + name + "\' AND SpaceNumber = " + number);
                    System.out.println("Parking Lot Space updated successfully.");
                }
                case 5 -> {
                    ArrayList space = getSpaceDetails();
                    String name = (String) space.get(0);
                    String id = (String) space.get(1);
                    Integer number = (Integer) space.get(2);
                    System.out.println("\nEnter new Space Type (Electric/ Handicap/ Compact Car/ Regular):");
                    String newType = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Space SET SpaceType = \'" + newType + "\' WHERE ZoneID = \'" + id + "\' AND LotName = \'" + name + "\' AND SpaceNumber = " + number);
                    System.out.println("Parking Lot Space updated successfully.");
                }

                case 6 -> {
                    System.out.println("Back to Parking Lot menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static ArrayList getSpaceDetails() throws SQLException {
        boolean flag = false;
        String name = null;
        String zone = null;
        Integer number = null;
        while (!flag) {
            System.out.println("\nEnter Parking Lot name where space needs to be updated: ");
            name = scanner.nextLine();
            System.out.println("\nEnter zone ID that needs to be updated: ");
            zone = scanner.nextLine();
            while (true) {
                try {
                    System.out.println("\nEnter Space Number that needs to be updated: ");
                    number = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.println("Please enter space number in numerical");
                }
            }


            if (!doesSpaceExist(name, zone, number)) {
                System.out.println("Incorrect Space entered. Please select space from the below Spaces");
                ResultSet names = Main.statement.executeQuery("SELECT * FROM Space;");
                while (names.next()) {
                    System.out.println("Lot name = " + names.getString("LotName").toString() + ", Zone ID = " + names.getString("ZoneID").toString() + ", Space Number = " + names.getInt("SpaceNumber"));
                }
                System.out.println();
            } else {
                flag = true;
            }
        }
        ArrayList<Object> spaces = new ArrayList<Object>();
        spaces.add(name);
        spaces.add(zone);
        spaces.add(number);
        return spaces;
    }

    private static boolean doesSpaceExist(String name, String zone, Integer number) throws SQLException {
        boolean spaceExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Space WHERE SpaceNumber = " + number + " AND ZoneID = \'" + zone + "\' AND LotName = \'" + name + "\'");
        if (rs.next()) {
            spaceExists = true;
        }
        return spaceExists;
    }
}



