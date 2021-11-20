package atm;

/*
 * JDBC (Java Database Connectivity class) 
 * import package: driver (connector) -> package that allows you to connect your Java program to a mysql database
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {

	private Connection con;

	public DbConnector() throws SQLException { // constructor -> establishes connection and creates DB (NOTE: make sure
												// to launch apache and mysql)

		try {
			// link mysql jdbc jar file to class (register JDBC driver)
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Connecting to database...");

			// open a connection to localhost and select our database. String literal is
			// localhost URL, username=root, pass="". If database doesn't exist, exception
			// is caught and we just connect to the localhost without selecting a db
			con = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "");

			// create database
			System.out.println("Creating database...");
			// execute a query
			con.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS atm_database");
			System.out.println("Database created successfully...");
			con = DriverManager.getConnection("jdbc:mysql://localhost/atm_database", "root", ""); // select db after
																									// creating it, if
																									// it didn't exist

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public void addData(String argCreationDate, int argAcctNumber, int argPin, String argBalance, String argAcctType) throws SQLException {

		try {
			// create table
			System.out.println("Creating table...");
			con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Accounts "
					+ "(Account_Creation_Date VARCHAR(30), Account_Number INT PRIMARY KEY, Pin INT, Account_Balance VARCHAR(30), Account_Type VARCHAR(30))");
			System.out.println("Table created successfully...");
			
			// insert data values into table
			con.createStatement().executeUpdate(
					new StringBuilder().append("INSERT INTO Accounts (Account_Creation_Date, Account_Number, Pin, Account_Balance, Account_Type)")
						.append("VALUES (").append("\'").append(argCreationDate).append("\'").append(", ").append(argAcctNumber).append(", ")
						.append(argPin).append(", ").append("\'").append(argBalance).append("\'").append(", ").append("\'")
						.append(argAcctType).append("\'").append(")").toString());
			System.out.println("Records added to database ");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		} 
	}

	public void updateData(String argBal, int argAcctNumber) throws SQLException {
		try {
			// update record in db
			System.out.println("Updating record...");
			con.createStatement().executeUpdate(
					"UPDATE Accounts SET Account_Balance= " + "\'" + argBal + "\'" + " WHERE Account_Number=" + argAcctNumber);
			System.out.println("Record updated successfully...");

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public void terminateAccount(int argAcctNumber) throws SQLException {
		// delete record from table 'Accounts'
		try {
			con.createStatement().executeUpdate("DELETE FROM Accounts WHERE Account_Number= " + argAcctNumber);
			System.out.println("Account deleted successfully...");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		} 
	}

	public void deleteDB() {
		// delete entire database
 
		try {
			con.createStatement().executeUpdate("DROP DATABASE atm_database");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Database deleted successfully...");
	}
}
