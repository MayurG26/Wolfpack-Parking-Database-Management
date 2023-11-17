import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class Citation {
    static Scanner scanner = new Scanner(System.in);

    public static void citationOptions() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Insert citation Information");
            System.out.println("2. View all citation information");
            System.out.println("3. Update citation information");
            System.out.println("4. Delete citation information");
            System.out.println("5. Pay citation");
            System.out.println("6. Appeal citation");
            System.out.println("7. Check parking violation");
            System.out.println("8. Return to the home screen");

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
                    case 1 -> insertCitation();
                    case 2 -> viewCitation();
                    case 3 -> updateCitation();
                    case 4 -> deleteCitation();
                    case 5 -> payCitation();
                    case 6 -> appealCitation();
                    case 7 -> checkParkingViolation();
                    case 8 -> {
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
    private static void checkParkingViolation() throws SQLException {
        List<String> expectedLocationDetails = new ArrayList<>();
        List<String> actualLocationDetails = new ArrayList<>();
        boolean isViolation = false;
        ResultSet rs;
        String permitID = null;
        System.out.println("Following are the existing license number: ");
        Vehicle.printAllLicenseNumbers();
        System.out.println("Enter the license number for which you want to check parking violation: ");
        String licenseNo = scanner.nextLine();
        if (!Vehicle.doesLicenseNoExist(licenseNo)) {
            System.out.println("Incorrect license number entered. Please try again.");
        } else {
            ResultSet resultSet = Main.statement.executeQuery("SELECT PermitID, AssignedSpaceType, AssignedZoneID, AssignedLot FROM Space NATURAL Right JOIN Comprises NATURAL JOIN Permit NATURAL RIGHT JOIN Vehicle WHERE Vehicle.LicenseNo = \'" + licenseNo + "\';");
            while (resultSet.next()) {
                permitID = resultSet.getString("PermitID");
                expectedLocationDetails.add(resultSet.getString("AssignedSpaceType"));
                expectedLocationDetails.add(resultSet.getString("AssignedZoneID"));
                expectedLocationDetails.add(resultSet.getString("AssignedLot"));
            }

            System.out.println("Enter current car parked parking lot name: ");
            String actualLotName = scanner.nextLine();
            System.out.println("Enter current car parked zone ID: ");
            String actualZoneID = scanner.nextLine();
            System.out.println("Enter current car parked space type: ");
            String actualSpaceType = scanner.nextLine();

            if (ParkingLot.doesZoneExist(actualLotName, actualZoneID)) {
                actualLocationDetails.add(actualSpaceType);
                actualLocationDetails.add(actualZoneID);
                actualLocationDetails.add(actualLotName);
                if (permitID == null) {
                    isViolation = true;
                    System.out.println("Car has no permit in lot: " + actualLotName);
                    Double fee = getCitationFee("No Permit", licenseNo);
                    generateCitationForViolation(licenseNo, actualLotName, fee, "No Permit");
                } else {
                    if (expectedLocationDetails.retainAll(actualLocationDetails)) {
                        isViolation = true;
                        System.out.println("Parking violation detected for car --> " + licenseNo);
                        Double fee = getCitationFee("Invalid Permit", licenseNo);
                        generateCitationForViolation(licenseNo, actualLotName, fee, "Invalid Permit");
                    }

                    rs = Main.statement.executeQuery("SELECT CASE WHEN EXISTS (SELECT 1 FROM Permit NATURAL RIGHT JOIN Vehicle WHERE Permit.ExpDate < CURRENT_DATE AND LicenseNo = \'" + licenseNo + "\') THEN 'TRUE' ELSE 'FALSE' END AS VIOLATION ;");
                    if (rs.next()) {
                        String violation = rs.getString(1);
                        if (violation.equalsIgnoreCase("TRUE")) {
                            isViolation = true;
                            System.out.println("Permit is EXPIRED for car: " + licenseNo);
                            Double fee = getCitationFee("Expired Permit", licenseNo);
                            generateCitationForViolation(licenseNo, actualLotName, fee, "Expired Permit");
                        }

                    } else {
                        rs = Main.statement.executeQuery("SELECT CASE WHEN EXISTS (SELECT 1 FROM Permit NATURAL RIGHT JOIN Vehicle WHERE Permit.StartDate > CURRENT_DATE AND LicenseNo = \'" + licenseNo + "\') THEN 'TRUE' ELSE 'FALSE' END AS VIOLATION ;");
                        if (rs.next()) {
                            String violation = rs.getString(1);
                            if (violation.equalsIgnoreCase("TRUE")) {
                                isViolation = true;
                                System.out.println("Permit is not yet valid for car: " + licenseNo);
                                Double fee = getCitationFee("No Permit", licenseNo);
                                generateCitationForViolation(licenseNo, actualLotName, fee, "No Permit");
                            }
                        }
                    }
                }
                if (!isViolation) {
                    System.out.println("There are no parking violations detected for the car --> " + licenseNo);
                }
            } else {
                System.out.println("Incorrect information entered.Please try again.");
            }
        }
    }

    private static void generateCitationForViolation(String licenseNo, String actualLotName, Double fee, String category) throws SQLException {
        System.out.println("Do you want to generate citation for this car? (Y/N)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("Y")) {
            insertCitation(licenseNo, actualLotName, java.time.LocalDate.now().toString(), java.time.LocalTime.now().toString(), fee, category, "NOT PAID", "");
        } else {
            System.out.println("No citation created for the above parking violation.");
        }
    }

    private static void appealCitation() throws SQLException {
        String cNumber = null;
        ResultSet result;
        BigInteger driverId = null;
        boolean exit = false, flag = true;
        System.out.println("Enter Driver ID or License Number (Press enter to skip):");
        String ans = scanner.nextLine();

        if (!ans.isEmpty()) {
            if (StringUtils.isNumeric(ans))
                driverId = new BigInteger(ans);
            ResultSet resultSet = Main.statement.executeQuery("SELECT * FROM Citation WHERE DriverID = \'" + ans + "\' OR LicenseNo = \'" + ans + "\';");
            if (!resultSet.next()) {
                if (!Driver.doesDriverIDExist(driverId) || !Vehicle.doesLicenseNoExist(ans)) {
                    System.out.println("Incorrect driver ID or license number entered. Please try again.");
                } else {
                    System.out.println("There are no citations.");
                }
            } else {
                do {
                    System.out.println("\nFollowing are the citations for the mentioned ID");
                    resultSet.beforeFirst();
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString("CNumber"));
                    }
                    System.out.println("Please provide the citation number for which you wish to submit an appeal:");
                    cNumber = scanner.nextLine();
                    result = Main.statement.executeQuery("SELECT CNumber FROM Citation WHERE DriverID = \'" + ans + "\' OR licenseNo = \'" + ans + "\' AND Cnumber = \'" + cNumber + "\';");
                    if (!result.next()) {
                        System.out.println("Incorrect citation number entered. Please try again.");
                        flag = false;
                    }
                } while (!flag);
                appealOptions(cNumber, ans);
            }
        } else {
            do {
                ResultSet resultSet = Main.statement.executeQuery("SELECT * FROM Citation;");
                System.out.println("\nFollowing are the citations:");
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("CNumber"));
                }
                System.out.println("Please provide the citation number for which you wish to submit an appeal:");
                cNumber = scanner.nextLine();
                if (!doesCitationNoExist(cNumber))
                    System.out.println("Incorrect citation number entered. Please try again.");
            } while (!doesCitationNoExist(cNumber));
            appealOptions(cNumber, ans);
        }
    }

    private static void appealOptions(String cNumber, String ans) throws SQLException {
        boolean exit = false;
        String withParamsQuery = "UPDATE Citation SET AppealStatus = ? WHERE DriverID = \'" + ans + "\' OR LicenseNo = \'" + ans + "\' AND CNumber = \'" + cNumber + "\';";
        String withoutParamsQuery = "UPDATE Citation SET AppealStatus = ? WHERE CNumber = \'" + cNumber + "\';";
        String query = ans.isEmpty() ? withoutParamsQuery : withParamsQuery;
        while (!exit) {
            System.out.println("\n1. Raise Appeal");
            System.out.println("2. Approve Appeal");

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
                    PreparedStatement preparedStatement = Main.connection.prepareStatement(query);
                    preparedStatement.setString(1, "RAISED");
                    preparedStatement.executeUpdate();
                    System.out.println("Appeal raised successfully for citation number " + cNumber);
                    exit = true;
                }
                case 2 -> {
                    PreparedStatement preparedStatement = Main.connection.prepareStatement(query);
                    preparedStatement.setString(1, "APPROVED");
                    preparedStatement.executeUpdate();
                    System.out.println("Appeal approved successfully for citation number " + cNumber);
                    exit = true;
                }
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    private static void payCitation() throws SQLException {
        String cNumber;
        ResultSet result;
        BigInteger driverId = null;
        System.out.println("Enter Driver ID or License Number (Press enter to skip):");
        String ans = scanner.nextLine();

        if (!ans.isEmpty()) {
            if (StringUtils.isNumeric(ans))
                driverId = new BigInteger(ans);
            ResultSet citations = Main.statement.executeQuery("SELECT CNumber FROM Citation WHERE DriverID = \'" + ans + "\' OR LicenseNo = \'" + ans + "\' AND PaymentStatus <> \'PAID\';");
            if (!citations.next()) {
                if (!Driver.doesDriverIDExist(driverId) || !Vehicle.doesLicenseNoExist(ans)) {
                    System.out.println("Incorrect driver ID or license number entered. Please try again.");
                } else {
                    System.out.println("There are no unpaid citations for the mentioned ID.");
                }
            } else {
                System.out.println("\nFollowing are the unpaid citations for the mentioned ID");
                citations.beforeFirst();
                while (citations.next()) {
                    System.out.println(citations.getString("CNumber"));
                }
                do {
                    System.out.println("Provide the citation number associated with the payment:");
                    cNumber = scanner.nextLine();
                    result = Main.statement.executeQuery("SELECT CNumber FROM Citation WHERE WHERE DriverID = \'" + ans + "\' OR LicenseNo = \'" + ans + "\' AND PaymentStatus <> \'PAID\' AND CNumber = \'" + cNumber + "\';");
                } while (!result.next());
                Main.statement.executeUpdate("UPDATE Citation SET PaymentStatus = 'PAID' WHERE CNumber = \'" + cNumber + "\';");
                System.out.println("Payment completed successfully for citation number " + cNumber);
            }
        } else {
            do {
                ResultSet resultSet = Main.statement.executeQuery("SELECT * FROM Citation;");
                System.out.println("\nFollowing are the citations:");
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("CNumber"));
                }
                System.out.println("Provide the citation number associated with the payment:");
                cNumber = scanner.nextLine();
                if (!doesCitationNoExist(cNumber))
                    System.out.println("Incorrect citation number entered. Please try again.");
            } while (!doesCitationNoExist(cNumber));
            Main.statement.executeUpdate("UPDATE Citation SET PaymentStatus = 'PAID' WHERE CNumber = \'" + cNumber + "\';");
            System.out.println("Payment completed successfully for citation number " + cNumber);
        }
    }

    private static void insertCitation() throws SQLException {
        String date, time, lotName;
        Double fee = null;
        boolean exit = false;
        String category = null;
        System.out.println("Enter vehicle license number for citation: ");
        String licenseNo = scanner.nextLine();

        if (!Vehicle.doesLicenseNoExist(licenseNo)) {
            System.out.println("Incorrect license number entered. Please try again.");
            System.out.println("Following is the existing license number information: \n");
            Vehicle.viewVehicle();
        } else {
            do {
                System.out.println("\nEnter citation date (YYYY-MM-DD): ");
                date = scanner.nextLine();
            } while (!Main.isValidDateTimeFormat(date, "YYYY-MM-DD"));
            do {
                System.out.println("Enter citation time (HH:MM:SS): ");
                time = scanner.nextLine();
            } while (!Main.isValidDateTimeFormat(time, "HH:MM:SS"));

            do {
                System.out.println("Enter lot name for which citation is to be created: ");
                lotName = scanner.nextLine();
            } while (!ParkingLot.doesParkingLotExist(lotName));
            System.out.println("Select citation category and fee: ");
            while (!exit) {
                System.out.println("1. $25 for category Invalid Permit");
                System.out.println("2. $30 for category Expired Permit");
                System.out.println("3. $40 for category No Permit");

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
                        category = "Invalid Permit";
                        fee = getCitationFee(category, licenseNo);
                        exit = true;
                    }
                    case 2 -> {
                        category = "Expired Permit";
                        fee = getCitationFee(category, licenseNo);
                        exit = true;
                    }
                    case 3 -> {
                        category = "No Permit";
                        fee = getCitationFee(category, licenseNo);
                        exit = true;
                    }
                    default -> System.out.println("\nInvalid choice. Please try again.");
                }
            }

            System.out.println("Enter citation payment status: ");
            String paymentStatus = scanner.nextLine();
            System.out.println("Enter citation appeal status (Press enter to skip): ");
            String appealStatus = scanner.nextLine();

            insertCitation(licenseNo, lotName, date, time, fee, category, paymentStatus, appealStatus);
        }
    }

    private static void insertCitation(String licenseNo, String lotName, String date, String time, Double fee, String category, String paymentStatus, String appealStatus) throws SQLException {
        String cNumber = generateCitationNumber();
        BigInteger driverId = getDriverIdFromLicenseNo(licenseNo);
        PreparedStatement ps;
        if (driverId == null) {
            ps = Main.connection.prepareStatement("INSERT INTO Citation (CNumber, Date, Fee, Category, PaymentStatus, Time, AppealStatus, LicenseNo) VALUES (\'" + cNumber + "\', \'" + date + "\', " + fee + ", \'" + category + "\', \'" + paymentStatus + "\', \'" + time + "\', ?, \'" + licenseNo + "\');");
        } else {
            ps = Main.connection.prepareStatement("INSERT INTO Citation (CNumber, Date, Fee, Category, PaymentStatus, Time, AppealStatus, LicenseNo, DriverID) VALUES (\'" + cNumber + "\', \'" + date + "\', " + fee + ", \'" + category + "\', \'" + paymentStatus + "\', \'" + time + "\', ?, \'" + licenseNo + "\'," + driverId + ");");
        }
        if (appealStatus.isEmpty()) {
            ps.setNull(1, Types.VARCHAR);
        } else {
            ps.setString(1, appealStatus);
        }
        ps.executeUpdate();
        Main.statement.executeUpdate("INSERT INTO Encompasses (CNumber, LotName) VALUES (\'" + cNumber + "\', \'" + lotName + "\');");
        System.out.println("Citation information entered successfully.");
        System.out.println("Citation number: " + cNumber);
    }

    private static Double getCitationFee(String category, String licenseNo) throws SQLException {
        HashMap<String, Double> hashMap = new HashMap<String, Double>();
        hashMap.put("Invalid Permit", 25.0);
        hashMap.put("Expired Permit", 30.0);
        hashMap.put("No Permit", 40.0);
        Double fee = hashMap.get(category);
        Double discountedFee = fee;
        ResultSet rs = Main.statement.executeQuery("SELECT AssignedSpaceType FROM Permit NATURAL RIGHT JOIN Vehicle WHERE LicenseNo = \'" + licenseNo + "\';");
        while (rs.next()) {
            String spaceType = rs.getString("AssignedSpaceType") != null ? rs.getString("AssignedSpaceType") : "";
            if (spaceType.equalsIgnoreCase("Handicap")) {
                discountedFee = 0.5 * fee;
                System.out.println("Handicap user discount of 50% applied!!");
            }
        }
        return discountedFee;
    }

    private static String generateCitationNumber() throws SQLException {
        String generatedString = RandomStringUtils.random(3, true, true);
        while (doesCitationNoExist(generatedString)) {
            generatedString = RandomStringUtils.random(3, true, true);
            System.out.println(generatedString);
        }
        return generatedString;
    }

    private static void viewCitation() throws SQLException {
        ResultSet rs = Main.statement.executeQuery("SELECT CNumber, LicenseNo, Date, Time, LotName, Category, Fee, PaymentStatus, AppealStatus FROM Citation NATURAL JOIN Encompasses");
        DBTablePrinter.printResultSet(rs);
    }

    /**
     * needs work
     *
     * @throws SQLException
     */
    private static void deleteCitation() throws SQLException {
        System.out.println("\nEnter citation number to be deleted: ");
        String cNumber = scanner.nextLine();

        if (doesCitationNoExist(cNumber)) {
            String paymentStatus = getColumnDetails("PaymentStatus", cNumber) == null ? "" : getColumnDetails("PaymentStatus", cNumber);
            String appealStatus = getColumnDetails("AppealStatus", cNumber) == null ? "" : getColumnDetails("AppealStatus", cNumber);
            if (appealStatus.equalsIgnoreCase("APPROVED") || paymentStatus.equalsIgnoreCase("PAID")) {
                Main.statement.executeUpdate("DELETE FROM Encompasses WHERE CNumber = \'" + cNumber + "\';");
                Main.statement.executeUpdate("DELETE FROM Citation WHERE CNumber = \'" + cNumber + "\';");
                System.out.println("Citation row with citation number " + cNumber + " deleted successfully.");
            } else {
                System.out.println("Cannot delete the given citation number since payment status is not paid or appeal status is not approved yet.");
            }
        } else {
            System.out.println("Incorrect citation number entered. Please select from the below citation numbers:");
            ResultSet ids = Main.statement.executeQuery("SELECT CNumber FROM Citation;");
            while (ids.next()) {
                System.out.println(ids.getString("CNumber"));
            }
            System.out.println();
        }
    }

    private static void updateCitation() throws SQLException {
        boolean exit = false;
        String category = null;
        while (!exit) {
            System.out.println("\n1. Update citation number");
            System.out.println("2. Update citation license number");
            System.out.println("3. Update citation date");
            System.out.println("4. Update citation time");
            System.out.println("5. Update citation lot name");
            System.out.println("6. Update citation category");
            System.out.println("7. Update citation fee");
            System.out.println("8. Return to the citation screen");

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
                    String cNumber = getCitationNumber();
                    System.out.println("\nEnter new citation number: ");
                    String newCitationNumber = scanner.nextLine();

                    if (doesCitationNoExist(newCitationNumber)) {
                        System.out.println("Citation number already exists. Please try again.");
                    } else {
                        Main.statement.executeUpdate("UPDATE Citation SET CNumber = \'" + newCitationNumber + "\' WHERE CNumber = \'" + cNumber + "\';");
                        System.out.println("Citation number updated successfully.");
                    }
                }
                case 2 -> {
                    String cNumber = getCitationNumber();
                    System.out.println("\nEnter new license number: ");
                    String newLicenseNo = scanner.nextLine();

                    if (!Vehicle.doesLicenseNoExist(newLicenseNo)) {
                        System.out.println("License number does not exist. Please try again.");
                        System.out.println("Please assign license number from the existing entries: ");
                        Main.printTable("Vehicle");
                    } else {
                        Main.statement.executeUpdate("UPDATE Citation SET LicenseNo = \'" + newLicenseNo + "\' WHERE CNumber = \'" + cNumber + "\';");
                        ResultSet rs = Main.statement.executeQuery("SELECT Category FROM Citation WHERE CNumber = \'" + cNumber + "\';");
                        while (rs.next()) {
                            category = rs.getString("Category");
                        }
                        Double updatedFee = getCitationFee(category, newLicenseNo);
                        Main.statement.executeUpdate("UPDATE Citation SET Fee = " + updatedFee + " WHERE CNumber = \'" + cNumber + "\';");
                        BigInteger driverId = getDriverIdFromLicenseNo(newLicenseNo);
                        Main.statement.executeUpdate("UPDATE Citation SET DriverID = " + driverId + " WHERE CNumber = \'" + cNumber + "\';");
                        System.out.println("Citation license number updated successfully.");
                    }
                }
                case 3 -> {
                    String cNumber = getCitationNumber();
                    String newCitationDate;
                    do {
                        System.out.println("\nEnter new citation date (YYYY-MM-DD): ");
                        newCitationDate = scanner.nextLine();
                    } while (!Main.isValidDateTimeFormat(newCitationDate, "YYYY-MM-DD"));
                    Main.statement.executeUpdate("UPDATE Citation SET Date = \'" + newCitationDate + "\' WHERE CNumber = \'" + cNumber + "\';");
                    System.out.println("Citation date updated successfully.");
                }
                case 4 -> {
                    String cNumber = getCitationNumber();
                    String newCitationTime;
                    do {
                        System.out.println("\nEnter new citation time (HH:MM:SS): ");
                        newCitationTime = scanner.nextLine();
                    } while (!Main.isValidDateTimeFormat(newCitationTime, "HH:MM:SS"));
                    Main.statement.executeUpdate("UPDATE Citation SET Time = \'" + newCitationTime + "\' WHERE CNumber = \'" + cNumber + "\';");
                    System.out.println("Citation category updated successfully.");
                }
                case 5 -> {
                    String cNumber = getCitationNumber();
                    System.out.println("\nEnter new lot name: ");
                    String newLotName = scanner.nextLine();

                    if (!ParkingLot.doesParkingLotExist(newLotName)) {
                        System.out.println("Lot name does not exist. Please try again.");
                        System.out.println("Please assign lot name from the existing entries: ");
                        Main.printTable("ParkingLot");
                    } else {
                        Main.statement.executeUpdate("UPDATE Encompasses SET LotName = \'" + newLotName + "\' WHERE CNumber = \'" + cNumber + "\';");
                        System.out.println("Citation lot name updated successfully.");
                    }
                }
                case 6 -> {
                    String cNumber = getCitationNumber();
                    System.out.println("\nEnter new citation category: ");
                    String newCitationCategory = scanner.nextLine();
                    Main.statement.executeUpdate("UPDATE Citation SET Category = \'" + newCitationCategory + "\' WHERE CNumber = \'" + cNumber + "\';");
                    System.out.println("Citation category updated successfully.");
                }
                case 7 -> {
                    String cNumber = getCitationNumber();
                    System.out.println("\nEnter new citation fee: ");
                    double newCitationFee = Double.parseDouble(scanner.nextLine());
                    Main.statement.executeUpdate("UPDATE Citation SET Fee = " + newCitationFee + " WHERE CNumber = \'" + cNumber + "\';");
                    System.out.println("Citation number updated successfully.");
                }
                case 8 -> {
                    System.out.println("<-- Back to citation menu");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static String getCitationNumber() throws SQLException {
        boolean flag = false;
        String citationNumber = null;
        while (!flag) {
            System.out.println("\nEnter citation number to be updated: ");
            citationNumber = scanner.nextLine();
            if (!doesCitationNoExist(citationNumber)) {
                System.out.println("Incorrect citation number entered. Please select from the below license numbers:");
                ResultSet citations = Main.statement.executeQuery("SELECT CNumber FROM Citation;");
                while (citations.next()) {
                    System.out.println(citations.getString("CNumber"));
                }
                System.out.println();
            } else {
                flag = true;
            }
        }
        return citationNumber;
    }

    public static List<String> getCitationNumberFromLicense(String licenseNo) throws SQLException {
        List<String> citationNos = new ArrayList<>();
        boolean flag = false;
        ResultSet citations = Main.statement.executeQuery("SELECT CNumber FROM Citation WHERE LicenseNo = \'" + licenseNo + "\';");
        while (citations.next()) {
            citationNos.add(citations.getString("CNumber"));
        }
        return citationNos;
    }

    public static BigInteger getDriverIdFromLicenseNo(String licenseNo) throws SQLException {
        BigInteger driverId = null;
        ResultSet ids = Main.statement.executeQuery("Select DriverID from Permit NATURAL RIGHT OUTER JOIN Vehicle WHERE Vehicle.LicenseNo = \'" + licenseNo + "\';");
        while (ids.next()) {
            if (ids.getBigDecimal("DriverID") == null) {
                driverId = null;
            } else {
                driverId = ids.getBigDecimal("DriverID").toBigInteger();
            }
        }
        return driverId;
    }

    public static boolean doesCitationNoExist(String cNumber) throws SQLException {
        boolean citationExists = false;
        ResultSet rs = Main.statement.executeQuery("SELECT * FROM Citation WHERE CNumber = \'" + cNumber + "\';");
        if (rs.next()) {
            citationExists = true;
        }
        return citationExists;
    }

    public static String getColumnDetails(String columnName, String cNumber) throws SQLException {
        String result = null;
        ResultSet citations = Main.statement.executeQuery("SELECT " + columnName + " FROM Citation WHERE CNumber = \'" + cNumber + "\';");
        while (citations.next()) {
            result = citations.getString(columnName);
        }
        return result;
    }

    public static boolean checkIfCitationsExist(String licenseNo) throws SQLException {
        boolean citationExist = false;
        List<String> cNumbers = getCitationNumberFromLicense(licenseNo);
        if (cNumbers != null) {
            citationExist = true;
        }
        return citationExist;
    }

    public static void deleteCitationIfApproved(String licenseNo, String... queries) throws SQLException {
        boolean queryExist = queries.length > 0;
        List<String> cNumbers = getCitationNumberFromLicense(licenseNo);
        for (String value : cNumbers) {
            if (Citation.doesCitationNoExist(value)) {
                String paymentStatus = Citation.getColumnDetails("PaymentStatus", value) == null ? "" : Citation.getColumnDetails("PaymentStatus", value);
                String appealStatus = Citation.getColumnDetails("AppealStatus", value) == null ? "" : Citation.getColumnDetails("AppealStatus", value);
                if (appealStatus.equalsIgnoreCase("APPROVED") || paymentStatus.equalsIgnoreCase("PAID")) {
                    Main.statement.executeUpdate("DELETE FROM Encompasses WHERE CNumber = \'" + value + "\';");
                    Main.statement.executeUpdate("DELETE FROM Citation WHERE CNumber = \'" + value + "\';");
                    if (queryExist) {
                        for (String query : queries) {
                            Main.statement.executeUpdate(query);
                        }
                    }
                    System.out.println("Deleted successfully.");
                } else {
                    System.out.println("Cannot delete due to UNPAID CITATIONS.");
                }
            } else {
                System.out.println("Invalid citation number entered. Please try again."); //remove
            }
        }
    }

}