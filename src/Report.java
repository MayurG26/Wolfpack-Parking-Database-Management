import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Report {
    // Generate a report for citations. For each lot, generate a report for the total number of citations given in all zones in the lot for a given time range (e.g., monthly or annually). Return the list of zones for each lot as tuple pairs (lot, zone). Return the number of cars that are currently in violation. Return the number of employees having permits for a given parking zone. Return permit information given an ID or phone number. Return an available space number given a space type in a given parking lot.
    static Scanner scanner = new Scanner(System.in);

    public static void ReportsMenu() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Generate report for citations");
            System.out.println("2. Generate report for citation for a particular lot in the given time range");
            System.out.println("3. Generate list of zones for each lots");
            System.out.println("4. Generate number of cars currently in violation");
            System.out.println("5. Generate report for number of employees having permits for a given parking zone");
            System.out.println("6. Generate Permit Information for a Driver");
            System.out.println("7. Generate an available space number for a space type in given parking lot");
            System.out.println("8. Back to home menu ");
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
                case 1 -> CitationReport();
                case 2 -> LotCitationReport();
                case 3 -> ListZones();
                case 4 -> NumCarViolation();
                case 5 -> NumEmployee();
                case 6 -> PermitInfo();
                case 7 -> AvailSpaceNumber();
                case 8 ->{
                    System.out.println("Back to home menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void CitationReport() throws SQLException {
        System.out.println("List of zones and lot name");
        Main.statement.executeUpdate("SELECT pl.LotName, z.ZoneID FROM ParkingLot pl JOIN Zone z ON pl.LotName = z.LotName;");
    }
    private static void LotCitationReport() throws SQLException {
        String assignedLot;
        ResultSet result;
        Boolean flag=false;
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
        ResultSet rs =Main.statement.executeQuery("SELECT pl.LotName, z.ZoneID FROM ParkingLot pl JOIN Zone z ON pl.LotName = z.LotName;");
        DBTablePrinter.printResultSet(rs);

    }
    private static void ListZones() throws SQLException {
        System.out.println("List of zones and lot name:");
        ResultSet rs =Main.statement.executeQuery("SELECT pl.LotName, z.ZoneID FROM ParkingLot pl JOIN Zone z ON pl.LotName = z.LotName;");
        DBTablePrinter.printResultSet(rs);
    }
    private static void NumCarViolation() throws SQLException {

    }
    private static void NumEmployee() throws SQLException {

    }
    private static void PermitInfo() throws SQLException {

    }
    private static void AvailSpaceNumber() throws SQLException {

    }
}
