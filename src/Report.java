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
            try {


                switch (choice) {
                    case 1 -> CitationReport();
                    case 2 -> LotCitationReport();
                    case 3 -> ListZones();
                    case 4 -> NumCarViolation();
                    case 5 -> NumEmployee();
                    case 6 -> PermitInfo();
                    case 7 -> AvailSpaceNumber();
                    case 8 -> {
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
    private static void CitationReport() throws SQLException {
        System.out.println("Citation Report:");
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Citation " +
                "LEFT JOIN Vehicle ON Citation.LicenseNo = Vehicle.LicenseNo " +
                "LEFT JOIN Permit ON Vehicle.PermitID = Permit.PermitID;");

        DBTablePrinter.printResultSet(rs);
    }
    private static void LotCitationReport() throws SQLException {
        System.out.println("Enter the type of time range you want to see the report for: ");
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Monthly");
            System.out.println("2. Annually");
            System.out.println("3. Custom Input for Dates");
            System.out.println("4. Back to Reports menu ");
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
                case 1 -> {
                    System.out.println("Monthly Report for Citations:");
                    ResultSet rs=Main.statement.executeQuery("Select LotName, Count(CNumber) As 'Total number of Citations', DATE_FORMAT(Date, '%b %Y') AS 'Month-Year' FROM Encompasses natural join Citation GROUP BY LotName;");
                    DBTablePrinter.printResultSet(rs);
                }
                case 2 -> {
                    System.out.println("Yearly Report for Citations:");
                    ResultSet rs=Main.statement.executeQuery("Select LotName, Count(CNumber) As 'Total number of Citations', YEAR(Date) AS Year FROM Encompasses natural join Citation GROUP BY LotName;");
                    DBTablePrinter.printResultSet(rs);
                }
                case 3 ->{
                    String startDate, expDate;
                    do {
                        System.out.println("Enter Start Date (YYYY-MM-DD): ");
                        startDate = scanner.nextLine();
                    } while (!Main.isValidDateTimeFormat(startDate, "YYYY-MM-DD"));

                    do {
                        System.out.println("Enter Expiry Date (YYYY-MM-DD): ");
                        expDate = scanner.nextLine();
                    } while (!Main.isValidDateTimeFormat(expDate, "YYYY-MM-DD"));
                    System.out.println("Custom Time range Report for Citations:");
                    ResultSet rs=Main.statement.executeQuery("SELECT LotName, COUNT(CNumber) AS 'Total Number of Citations' from Encompasses NATURAL JOIN Citation WHERE Citation.Date BETWEEN \'"+startDate+"\' AND \'"+expDate+"\' GROUP BY LotName;");
                    DBTablePrinter.printResultSet(rs);


                }
                case 4 ->{
                    System.out.println("Back to home menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }


    }
    private static void ListZones() throws SQLException {
        System.out.println("List of zones and lot name:");
        ResultSet rs =Main.statement.executeQuery("SELECT * FROM Zone;");
        DBTablePrinter.printResultSet(rs);
    }
    private static void NumCarViolation() throws SQLException {
        System.out.println("Number of cars in violation:");
        ResultSet rs = Main.statement.executeQuery("SELECT Count(*) AS \'Number of Cars in Violation\' FROM (SELECT DISTINCT LicenseNo FROM Citation WHERE PaymentStatus <> 'PAID' AND AppealStatus <> 'APPROVED' ) AS subquery");
        DBTablePrinter.printResultSet(rs);

    }
    private static void NumEmployee() throws SQLException {
        System.out.println("Enter the Lot Name where the zone id is : ");
        String lotName = scanner.nextLine();
        System.out.println("Enter the zone id to check: ");
        String zone = scanner.nextLine();
        if(!ParkingLot.doesZoneExist(lotName,zone)) {
            System.out.println("Zone id does not exist!");
        }
        else {
            ResultSet rs = Main.statement.executeQuery("Select COUNT(*) AS 'Total Employee Permits' from (SELECT DISTINCT DriverID FROM Driver NATURAL JOIN Permit WHERE Status = 'E'AND AssignedZoneID = \'" + zone + "\' and AssignedLot= \'" + lotName + "\') AS subqueryDriver ;");
            DBTablePrinter.printResultSet(rs);
        }
    }
    private static void PermitInfo() throws SQLException {
        System.out.println("\nEnter Driver Id : ");
        BigInteger driverId = scanner.nextBigInteger();
        scanner.nextLine();

        if(Driver.doesDriverIDExist(driverId)) {
            ResultSet rs = Main.statement.executeQuery("SELECT PermitID,PermitType,StartDate,ExpDate,ExpTime,AssignedSpaceType AS SpaceType,AssignedZoneID AS ZoneID, AssignedLot AS Lot, DriverID, LicenseNo  FROM Permit NATURAL JOIN Vehicle WHERE DriverID = "+driverId+";");
            DBTablePrinter.printResultSet(rs);
        } else {
            System.out.println("Incorrect driver ID entered. Please select from the below driver IDs");
            ResultSet ids = Main.statement.executeQuery("SELECT DriverID FROM Driver;");
            while (ids.next()) {
                System.out.println(ids.getBigDecimal("DriverID").toBigInteger());
            }
            System.out.println();
        }

    }
    private static void AvailSpaceNumber() throws SQLException {
        boolean flag =false;
        String assignedLot = null;
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
                ResultSet result = Main.statement.executeQuery("SELECT LotName FROM ParkingLot WHERE LotName = \'" + assignedLot + "\' ;");
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
        ResultSet sp = Main.statement.executeQuery("SELECT SpaceNumber FROM Space WHERE SpaceType = \'" + assignedSpaceType + "\' AND ZoneID =\'" + assignedZoneId + "\' AND LotName = \'" + assignedLot + "\' AND AvailStatus = \'Available\' order by SpaceNumber asc LIMIT 1;");
        int num;
        if (sp.next()) {
            num = sp.getInt("SpaceNumber");
            System.out.println("Available Space Number: "+num);

        }else{
            System.out.println("All spaces for the give space type are occupied or unavailable in the Parking Lot!");
        }
    }
}
