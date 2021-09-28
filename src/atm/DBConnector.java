package atm;

/*
 * JDBC (Java Database Connectivity class) 
 * import package: driver (connector) -> package that allows you to connect your Java program to a mysql database
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector {

	private Connection con;
	private Statement st;

	public DBConnector() throws SQLException { // constructor -> establishes connection and creates DB (NOTE: make sure
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
			st = con.createStatement(); // execute a query
			String sql = "CREATE DATABASE IF NOT EXISTS atm_database";
			st.executeUpdate(sql);
			System.out.println("Database created successfully...");
			con = DriverManager.getConnection("jdbc:mysql://localhost/atm_database", "root", ""); // select db after
																									// creating it, if
																									// it didn't exist

		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (Exception e) {
			System.out.println(e);
		} 
	}

	public void addData(int acctNo, int pin, String balance, String acctType) throws SQLException {

		try {
			// create table
			st = con.createStatement();
			System.out.println("Creating table...");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS Accounts "
					+ "(Account_Number INT PRIMARY KEY, Pin INT, Account_Balance VARCHAR(30), Account_Type VARCHAR(30))");
			System.out.println("Table created successfully...");

			StringBuilder stringBuilding = new StringBuilder();
			
			// insert data values into table
			st.executeUpdate(stringBuilding.append("INSERT INTO Accounts (Account_Number, Pin, Account_Balance, Account_Type) ")
				.append("VALUES (").append(acctNo).append(",").append(pin).append(",").append("\'")
				.append(balance).append("\'").append(",").append("\'")
				.append(acctType).append("\'") + ")");
			System.out.println("Records added to database ");

		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (Exception e) {
			System.out.println(e);
		} 
	}

	public void updateData(String bal, int acctNo) throws SQLException {
		try {
			// update record in db
			st = con.createStatement();
			System.out.println("Updating record...");
			st.executeUpdate(
					"UPDATE Accounts SET Account_Balance= " + "\'" + bal
					+ "\'" + " WHERE Account_Number=" + acctNo);
			System.out.println("Record updated successfully...");

		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (Exception e) {
			System.out.println(e);
		} 
	}

	public void terminateAccount(int acctNo) throws SQLException {
		// delete record from table 'Accounts'
		try {
			st = con.createStatement();
			st.executeUpdate("DELETE FROM Accounts WHERE Account_Number= " + acctNo);
			System.out.println("Account deleted successfully...");
		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void deleteDB() {
		// delete entire database
		try {
			String sql1 = "DROP DATABASE atm_database";
			st.executeUpdate(sql1);
			System.out.println("Database deleted successfully...");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}
