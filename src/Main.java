//package src;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    static final String USERNAME = "pnavale";
    static final String PW = "prach04";
    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/pnavale";

    public static Connection connection = null;
    public static Statement statement = null;

    public static final ResultSet result = null;

    /**
     * This function prints a menu to the system console.
     * Through this menu, one can interact with this program.
     * The first step must always be to load the database ("Initialize/Reload Database"),
     * if it is not already loaded. Then one can either look at reports,
     * handle payments, or otherwise interact with the database.
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            connectToDatabase();

            // Display the main menu
            boolean exit = false;
            while (!exit) {
                System.out.println("\nMenu:");
                System.out.println("1. Driver");
                System.out.println("2. Parking Lot");
                System.out.println("3. Permit");
                System.out.println("4. Vehicle");
                System.out.println("5. Citation");
                System.out.println("6. Reports");
                System.out.println("7. Initialize/Reload Database");
                System.out.println("8. Exit()");

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

                // Perform the selected action
                switch (choice) {
                    case 1 -> Driver.driverOptions();
                    case 2 -> ParkingLot.lotOptions();
                    case 3 -> Permit.permitOptions();
                    case 4 -> Vehicle.vehicleOptions();
                    case 5 -> Citation.citationOptions();
                    case 6 -> Report.ReportsMenu();
                    case 7 -> initialize();
                    case 8 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            System.out.println("\nSQL file not found!");
            throw new RuntimeException(e);
        } finally {
            System.out.println("\nCLOSING the Database connection finally!");
            close();
        }
    }

    public static void initialize() throws FileNotFoundException {
        String path = "src/ReloadDB.sql";
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setSendFullScript(false);
        scriptRunner.setStopOnError(true);
        scriptRunner.runScript(new FileReader(path));
    }

    public static void printTable(String tableName) throws SQLException {
        String queryString = "SELECT * FROM " + tableName;
        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        ResultSet rs = preparedStatement.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }

    /**
     * Connects to the database.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void connectToDatabase() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, USERNAME, PW);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new SQLException("Could not connect to database.");
        } catch (Exception e) {
            throw new ClassNotFoundException("Class for driver not found.");
        }
    }

    /**
     * Closes the connection to the database.
     */
    private static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (result != null) {
            try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isValidDateTimeFormat(String inputString, String expectedFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
            formatter.parse(inputString);
            return true;
        } catch (Exception e) {
            System.out.println("Incorrect format entered, please try again with " + expectedFormat + " format.");
            return false;
        }
    }
}