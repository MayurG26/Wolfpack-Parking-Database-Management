# Wolf Parking Management System

## CSC 540 Database Management System - Fall 2023

### Team I
1. Adam Bloebaum (abbloeba)
2. Mayur Prakash Gotmare (mgotmar)
3. Mugdha Milind Joshi (mjoshi5)
4. Prachi Madhukar Navale (pnavale)

### Project Setup
1. **Java Development Kit (JDK) Requirement:** Ensure that JDK 21 is installed on your local machine.
2. **IDE Configuration:** Add the project to your preferred Integrated Development Environment (IDE).
3. **Build Project:** Utilize the IDE to build the project. The system uses JDBC for SQL queries and is connected to the NCSU MariaDB.
4. **Run Application:** Execute the Main file to access the Wolf Parking Management System.

### Design Decisions
The system employs a primary menu offering users options such as Driver, Parking Lot, Permit, Vehicle, Citation, and Reports. Users provide input by entering a number from 1 to 8, corresponding to the desired action. Each menu option leads to a submenu displaying specific operations, including Create, Read, Update, and Delete (CRUD) operations. The codebase is organized with a primary class (Main.java) facilitating navigation between main menu options. Additionally, there are separate subclasses for each group of related operations. This organizational approach enhances code manageability and maintainability.

Two helper classes are incorporated into the program. The first, ReloadDB, utilizes a shared database construction to enable the reloading of the entire database at any point, providing time-saving benefits and serving as a safeguard in case of database loss. The second helper class, DBTablePrinter, abstracts the intricacies of obtaining various user inputs and includes a method for formatting menus.

This design promotes a modular and structured implementation of the Wolf Parking Management System, ensuring ease of use, maintenance, and extensibility.
