package atm;

/**
 * @author Brian Perel
 * @version 1.0
 *
 * Purpose: Java ATM simulation program allows you to work with an account: do deposit, balance inquery, withdraw, transfer, terminate account operations.
 * We are simulating this environment to allow a single user to work with a randomly generated account balance and perform ATM operations
 *
 * To get to menu screen: enter account number (must be 8 digits), then enter pin (4 digits) at next window, enter savings or checkings for acct type
 *
 * OOP app using Java
 * ATM functions: display balance, withdrawal, deposit, transfer funds, terminate account, serialization - deserialization
 * ATM withdrawal should be prevented if balance is below withdrawal amount
 * ATM deposit should be prevented if deposit amount is extreme
 * Keep track of 4 attributes: account number, PIN number, account balance, account type
 * Validate all user input: exception, type, format and condition handling
 * Save information to receipt txt file, at end of program ask if client wants a receipt or not, if not receipt file is deleted
 * All currency is in USD $
 *
 *
 * GUI design: window 1 = enter acctNo, window 2 = enter pin, window 3 = enter acctType (savings or checkings)
 * window 4 = show ATM user menu options
 * sub windows = 1 appears for each option entered
 *
 */

import java.util.Scanner;
import java.util.InputMismatchException;
import java.security.SecureRandom;
import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * ATM_Machine class will generate GUI Performs logic operations for data and
 * choices entered
 */
public class ATM_Machine_Main extends JFrame {

	static String acctNo;
	static String pin = "";
	static String acctTypeOption;
	static File receiptFile = new File("");
	static PrintWriter file = null;
	static final String WARNING = "Warning";
	static final String GOODBYE = "Goodbye";
	static final String ZERO_TO_NINE_REG_EXP = "[0-9]+";
	static final String HEADER_TITLE = "ATM - City Central Bank";
	static DecimalFormat formatter = new DecimalFormat("$###,###.00");
	static Scanner input = new Scanner(System.in);
	static SecureRandom randomGenerator = new SecureRandom();

	public static void main(String[] args) throws IOException, SQLException {

		/*
		 * Statements to delete database:
		 * 
		 * Runtime rt0 = Runtime.getRuntime(); // create runtime instance to start open
		 * file process Process p0 = rt0.exec("C:\\xampp\\xampp-control.exe"); // open
		 * xampp app DBConnector connect = new DBConnector(); // connect class to DB
		 * class to perform db operations connect.deleteDB(); // statement to delete db
		 * p0.destroy(); // close xampp app
		 */

		// format date and time for display
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("YYYY-MM-d-");
		java.time.LocalDateTime now = java.time.LocalDateTime.now();

		receiptFile = new File(new StringBuilder().append("Receipt.").append(now.format(dateTimeFormat)).append("id")
				.append(randomGenerator.nextInt(99)).append(".log").toString());
		file = new PrintWriter(receiptFile);

		int attempts = 0;

		do {
			if (attempts == 3) {
				JOptionPane.showMessageDialog(null, "Max tries exceeded, ATM System locked! Restart to unlock", "ATM",
						JOptionPane.WARNING_MESSAGE);
				closeApp();
			}

			// format date and time for display for app's initial window
			dateTimeFormat = DateTimeFormatter.ofPattern("MMM dd, h:mm a");

			try {
				acctNo = JOptionPane.showInputDialog(null, "Today is: " + now.format(dateTimeFormat) + "\nAccount Number: ",
						HEADER_TITLE, JOptionPane.QUESTION_MESSAGE);

				if (acctNo.equalsIgnoreCase("cancel")) {
					JOptionPane.showMessageDialog(null, "Have a nice day!", GOODBYE, JOptionPane.QUESTION_MESSAGE);
					closeApp();
				}

			} catch (NullPointerException e) {
				closeApp();
			}

			file.printf("%n\tATM - City Central Bank%nToday is: %s%n", now.format(dateTimeFormat));
			attempts++;

			if (acctNo.length() != 8 || !(acctNo.matches(ZERO_TO_NINE_REG_EXP))) {
				JOptionPane.showMessageDialog(null, "Invalid Account Number!", WARNING, JOptionPane.WARNING_MESSAGE);
			}

		} while (acctNo.length() != 8 || !(acctNo.matches(ZERO_TO_NINE_REG_EXP)));

		attempts = 0;

		do {
			if (attempts == 3) {
				JOptionPane.showMessageDialog(null, "Max tries exceeded, ATM System locked! Restart to unlock",
						HEADER_TITLE, JOptionPane.WARNING_MESSAGE);
				closeApp();
			}

			// create custom pin UI window with input value masking ('*')
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Pin: ");
			JPasswordField pass = new JPasswordField(10);
			panel.add(label);
			panel.add(pass);
			String[] options = new String[] { "OK", "Cancel" };

			int option = JOptionPane.showOptionDialog(null, panel, HEADER_TITLE, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, null);

			if (option == 0) { // if you press OK button
				pin = new String(pass.getPassword());
			} else { // if you press cancel button
				closeApp();
			}

			attempts++;

			if (pin.length() != 4 || !(pin.matches(ZERO_TO_NINE_REG_EXP))) {
				JOptionPane.showMessageDialog(null, "Invalid Pin Number!", WARNING, JOptionPane.WARNING_MESSAGE);
			}

		} while (pin.length() != 4 || !(pin.matches(ZERO_TO_NINE_REG_EXP)));

		do {
			try {
				do {
					acctTypeOption = JOptionPane.showInputDialog(null, "Savings (s) or Checkings (c): ", HEADER_TITLE,
							JOptionPane.QUESTION_MESSAGE);

					if (acctTypeOption.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Invalid Response!", WARNING, JOptionPane.WARNING_MESSAGE);
					}

				} while (acctTypeOption.isEmpty());

			} catch (NullPointerException e) {
				closeApp();
			}

			// for some reason when user enters 'checkings' for acct type invalid type flag is raised
			// this is a current work around for that problem
			if (acctTypeOption.equalsIgnoreCase("Checkings")) {
				acctTypeOption = "C";
			}

			if (!(acctTypeOption.equalsIgnoreCase("S")) && !(acctTypeOption.equalsIgnoreCase("C"))
					&& !(acctTypeOption.equalsIgnoreCase("Savings") && !(acctTypeOption.equalsIgnoreCase("Checkings")))) {
				JOptionPane.showMessageDialog(null, "Invalid option!", WARNING, JOptionPane.WARNING_MESSAGE);
			}

		} while (!acctTypeOption.matches("[a-zA-Z]+") || (!(acctTypeOption.equalsIgnoreCase("S"))
				&& !(acctTypeOption.equalsIgnoreCase("C"))
				&& !(acctTypeOption.equalsIgnoreCase("Savings") && !(acctTypeOption.equalsIgnoreCase("Checkings")))));

		if (acctTypeOption.equalsIgnoreCase("c") || acctTypeOption.equalsIgnoreCase("checkings")) {
			acctTypeOption = "Checkings";
		}

		else if (acctTypeOption.equalsIgnoreCase("s") || acctTypeOption.equalsIgnoreCase("savings")) {
			acctTypeOption = "Savings";
		}

		// create account
		Account account = new Account(acctNo, pin, ((Math.random() % 23) * 100000), acctTypeOption);

		// load to menu
		menu(account, file, "0", acctTypeOption, receiptFile);
	}

	public static void menu(Account account, PrintWriter file, String select, String savCheck, File receiptFile)
			throws IOException, SQLException {
		boolean isAcctTerminated = false; // flag checks if account has been terminated by user or not

		// open xampp app
		Process processToOpenXampp = Runtime.getRuntime().exec("C:\\xampp\\xampp-control.exe");

		// create connection ptr to database
		DBConnector connect = new DBConnector(); // connect class to DB class to perform db operations
		connect.addData(Integer.parseInt(acctNo), Integer.parseInt(pin), 
				formatter.format(account.getBalance()), savCheck); // add data to db

		do {
			try {
				// display menu for user
				select = JOptionPane.showInputDialog(null,
						"Enter:\n\t1. (1) for balance inquiry\n\t2. (2) for cash withdrawal"
								+ "\n\t3. (3) for cash deposit\n\t4. (4) to terminate account\n\t5."
								+ " (5) to transfer funds\n\t6. (6) (Save) Serialize Account"
								+ "\n\t7. (7) (Load) Deserialize Account \n\t8. (8) to quit\n\n\tSelect your transaction: \n",
						HEADER_TITLE, JOptionPane.QUESTION_MESSAGE);

				switch (select) {
					// balance inquiry
					case "1": {					
						if (account != null) {
							JOptionPane.showMessageDialog(null, account, "Balance Inquiry",
									JOptionPane.INFORMATION_MESSAGE);
							file.print("\nBalance inquiry...\n" + account);
							break;
						}
						
						JOptionPane.showMessageDialog(null, "Account is empty", WARNING, JOptionPane.WARNING_MESSAGE);
						file.print("Balance inquiry...\n\tAccount doesn't exist");
							
						break;
					}
	
					// withdraw funds
					case "2": {
						if (isAcctTerminated) {
							JOptionPane.showMessageDialog(null, "Account is empty, can't withdraw!", WARNING,
									JOptionPane.WARNING_MESSAGE);
							continue;
						}
						
						ATM w1 = new WithdrawFunds(account);
						w1.withdraw(file);
						break;
					}
	
					// deposit funds
					case "3": {
						if (isAcctTerminated) {
							JOptionPane.showMessageDialog(null, "Account is empty, can't deposit!", WARNING,
									JOptionPane.WARNING_MESSAGE);
							continue;
						}
						ATM d1 = new DepositFunds(account);
						d1.depositCash(file);
						break;
					}
	
					// terminate account
					case "4": {
						if (isAcctTerminated) {
							JOptionPane.showMessageDialog(null, "Account is already terminated!", WARNING,
									JOptionPane.WARNING_MESSAGE);
							continue;
						}
	
						connect.terminateAccount(Integer.parseInt(account.getAcctNo())); // deletes account from db table @
																							// localhost site
	
						account = null; // set account to value of null (clearing all attribute values)
						JOptionPane.showMessageDialog(null, "Account has been terminated\n", "Account Termination",
								JOptionPane.INFORMATION_MESSAGE);
	
						file.println("\nAccount has been terminated");
						isAcctTerminated = true; // flip flag so that certain ops can't be done under a terminated account
						break;
					}
	
					// transfer funds
					case "5": {
						if (isAcctTerminated) {
							JOptionPane.showMessageDialog(null, "Account is empty, can't transfer!", WARNING,
									JOptionPane.WARNING_MESSAGE);
							continue;
						}
						String acctNo2;
	
						do {
							acctNo2 = JOptionPane.showInputDialog(null, "\nAccount Number 2: ", "Account Terminated",
									JOptionPane.QUESTION_MESSAGE);
							if (acctNo.equals(acctNo2) || acctNo2.length() != 8
									|| !(acctNo2.matches(ZERO_TO_NINE_REG_EXP))) {
								JOptionPane.showMessageDialog(null, "Invalid Account!", WARNING,
										JOptionPane.WARNING_MESSAGE);
							}
						} while (acctNo.equals(acctNo2) || (acctNo2.length() != 8
								|| !(acctNo2.matches(ZERO_TO_NINE_REG_EXP))));
						Account account2 = new Account(acctNo2, pin, (Math.random() % 21) * 100000, savCheck);
						ATM t1 = new TransferFunds(account, account2);
						t1.transferFunds(acctNo2, file);
						break;
					}
	
					// save (serialize) object
					case "6": {
						if (isAcctTerminated) {
							JOptionPane.showMessageDialog(null, "Account is empty, can't serialize!", WARNING,
									JOptionPane.WARNING_MESSAGE);
							continue;
						}
	
						// Serialization
						try {
							// Save object state to a binary file
							FileOutputStream file1 = new FileOutputStream("AccountData.dat");
							ObjectOutputStream out = new ObjectOutputStream(file1);
	
							// method for object serialization
							out.writeObject(account);
	
							// close serialization process
							out.close();
							file1.close();
	
							JOptionPane.showMessageDialog(null, "\nObject has been serialized", "Serialize",
									JOptionPane.QUESTION_MESSAGE);
						} catch (IOException ex) {
							System.out.println("IOException is caught");
						}
	
						break;
					}
	
					// load (deserialize) object
					case "7": {
						if (isAcctTerminated) {
							JOptionPane.showMessageDialog(null, "Account is empty, can't serialize!", WARNING,
									JOptionPane.WARNING_MESSAGE);
							continue;
						}
							
						Account account1 = null; // create the empty object, request os to allocate chunk of memory to store
													// contents from file
	
						// Deserialization process
						try {
							FileInputStream file2 = new FileInputStream("AccountData.dat");
							ObjectInputStream in = new ObjectInputStream(file2);
	
							account1 = (Account) in.readObject(); // store the content from binary file to a reference
																	// variable (object)
																	// after reading = deserialize
		
							// print out the saved data from binary file
							JOptionPane.showMessageDialog(null,
									new StringBuilder().append("\nAccount Number: ").append(account1.getAcctNo())
											.append("\nAccount Pin: ").append(account1.getPIN())
											.append("\nAccount Balance: ").append(formatter.format(account1.getBalance()))
											.append("\nAccount type: ").append(account1.getType()),
									"Deserialize", JOptionPane.QUESTION_MESSAGE);
	
							in.close();
							
						} catch (IOException ex) {
							System.out.println("Deserialization error!");
						} catch (ClassNotFoundException ex) {
							System.out.println("Class not found error!");
						}
						break;
					}
	
					// exit program
					case "8": {
						file.print("\n\n\nHave a nice day!");
						processToOpenXampp.destroy(); // close xampp app
						String in = JOptionPane.showInputDialog(null, "\nWould you like a receipt? ", "Receipt?",
								JOptionPane.QUESTION_MESSAGE);
						
						file.close();
	
						if (in.equalsIgnoreCase("no") || in.equalsIgnoreCase("n") || in.equalsIgnoreCase("cancel")) {
							receiptFile.delete();
							JOptionPane.showMessageDialog(null, "\nHave a nice day!", GOODBYE,
									JOptionPane.QUESTION_MESSAGE);
							System.exit(0);
						} 
						
						JOptionPane.showMessageDialog(null, "Receipt saved as: " + receiptFile.getName(), "Receipt",
								JOptionPane.INFORMATION_MESSAGE);
						JOptionPane.showMessageDialog(null, "\nHave a nice day!", GOODBYE,
								JOptionPane.QUESTION_MESSAGE);
						Runtime.getRuntime().exec("notepad " + receiptFile); // open notepad program with pre-selected file
					
						System.exit(0);
					}
	
					default: {
						JOptionPane.showMessageDialog(null, "Invalid option!", WARNING, JOptionPane.WARNING_MESSAGE);
						break;
					}
				}

			} catch (InputMismatchException inputMismatchException) {
				JOptionPane.showMessageDialog(null, "\tError! Enter a number choice. Invalid option!\n", WARNING,
						JOptionPane.WARNING_MESSAGE);
				input.nextLine();
			} catch (NullPointerException e) {
				processToOpenXampp.destroy(); // close xampp app
				closeApp();
			}
		} while (!select.equals("8"));
	}
	
	public static void closeApp() {
		file.close();
		receiptFile.delete();
		System.exit(0);
	}
}