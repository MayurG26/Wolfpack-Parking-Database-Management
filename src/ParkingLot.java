import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
//                case 3 -> updateParkingLot();
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
//                    case 3 -> updateZones();
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
//                case 3 -> updateSpaces();
                case 4 -> deleteSpaces();
                case 5 -> {
                    System.out.println("Back to home menu");
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
        System.out.println("Enter Number of Zones: ");
        int numZones = scanner.nextInt();
        System.out.println("Enter Number of Spaces: ");
        int numSpaces = scanner.nextInt();
        Main.statement.executeUpdate("INSERT INTO  ParkingLot (LotName,Address,NumSpace,NumZone) VALUES (\'"+lotName+"\',\'"+lotAddress+"\',"+numZones+","+numSpaces+")");
        System.out.println("New Parking Lot is added in the database");

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
        System.out.println("Enter the Lot Name where the zone is : ");
        String lotName = scanner.nextLine();
        System.out.println("Enter the zone: ");
        String zone = scanner.nextLine();
        Main.statement.executeUpdate("INSERT INTO  Zone (LotName,ZoneID) VALUES (\'"+lotName+"\',\'"+zone+"\')");
        System.out.println("Zone was assigned in the given Parking Lot");

    }
    private static void viewZones() throws SQLException {
        String query = "Select * from Zones";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }
    private static void deleteZones() throws SQLException {
        System.out.println("Enter Zone To Be Deleted: ");
        String zone = scanner.nextLine();
        System.out.println("Enter the Parking Lot from where the Zone needs To Be Deleted: ");
        String lotName = scanner.nextLine();

        boolean zoneExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Zone WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone +"\'");
        if (rs.next()) {
            zoneExists = true;
        }

        if (zoneExists) {
            Main.statement.executeUpdate("DELETE FROM Zone WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone +"\';");
            System.out.println("Zone with ID " + zone + " from Lot " +lotName + " deleted successfully.");
        } else {
            System.out.println("Incorrect Zone entered. Please select from the below Zones");
            ResultSet name = Main.statement.executeQuery("SELECT ZoneID, LotName FROM Zone;");
            while (name.next()) {
                System.out.println("Zone = "+name.getString("ZoneID")+" Lot = "+name.getString("LotName"));
            }
            System.out.println();
        }
    }
    private static void insertSpaces() throws SQLException {
        System.out.println("Enter the Lot Name where the zone is : ");
        String lotName = scanner.nextLine();
        System.out.println("Enter the zone: ");
        String zone = scanner.nextLine();
        System.out.println("Enter the space number: ");
        int number = Integer.parseInt(scanner.nextLine());
        String query = "INSERT INTO Space (SpaceNumber, LotName, ZoneID) VALUES (" + number + ", '" + lotName + "', '" + zone + "')";
        Main.statement.executeUpdate(query);
        System.out.println("Space Number was inserted in the given Parking Lot");
    }
    private static void viewSpaces() throws SQLException {
        String query = "Select * from Space";
        PreparedStatement myStmt = Main.connection.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }

    private static void deleteSpaces() throws SQLException {
        System.out.println("Enter Space Number To Be Deleted: ");
        int number = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the Zone from where the Space Number needs To Be Deleted: ");
        String zone = scanner.nextLine();
        System.out.println("Enter the Parking Lot from where the Space Number needs To Be Deleted: ");
        String lotName = scanner.nextLine();

        boolean zoneExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Space WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone +"\' and SpaceNumber = "+number);
        if (rs.next()) {
            zoneExists = true;
        }

        if (zoneExists) {
            Main.statement.executeUpdate("DELETE FROM Space WHERE LotName = \'" + lotName + "\' and ZoneID = \'" + zone +"\' and SpaceNumber = "+number);
            System.out.println("Space Number "+ number + " with Zone ID " + zone + " from Lot " +lotName + " deleted successfully.");
        } else {
            System.out.println("Incorrect Space entered. Please select from the below Spaces");
            ResultSet name = Main.statement.executeQuery("SELECT SpaceNumber,ZoneID, LotName FROM Space;");
            while (name.next()) {
                System.out.println("Space Number = "+name.getString("SpaceNumber")+" Zone = "+name.getString("ZoneID")+" Lot = "+name.getString("LotName"));
            }
            System.out.println();
        }
    }

    private static void updateDriver() {
    }
}
