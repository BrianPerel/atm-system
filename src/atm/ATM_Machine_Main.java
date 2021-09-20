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
 *			window 4 = show ATM user menu options
 *				sub windows = 1 appears for each option entered
 *
 */

import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import java.awt.Button;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.sql.*;


/**
 * ATM_Machine class will generate GUI
 * Performs logic operations for data and choices entered
 */
public class ATM_Machine_Main extends JFrame {

	static String acctNo, savCheck0;
	static String pin = "";
	static Scanner input = new Scanner(System.in);
	private Button button_1;
	static DecimalFormat df = new DecimalFormat("$###,###.00");

	public static void main(String[] args) throws IOException, SQLException {

		/* Statements below are to delete database */
		/*
		Runtime rt0 = Runtime.getRuntime(); // create runtime instance to start open file process
		Process p0 = rt0.exec("C:\\xampp\\xampp-control.exe"); // open xampp app
		DBConnector connect = new DBConnector(); // connect class to DB class to perform db operations
		connect.deleteDB();  // statement to delete db
		p0.destroy(); // close xampp app
		*/

		final File fileMain = new File("Receipt.txt");
		final PrintWriter file = new PrintWriter(fileMain);

		int attempt = 0;

		do {
			if (attempt == 3) {
				JOptionPane.showMessageDialog(null, "Max tries exceeded, ATM System locked! Restart to unlock", "ATM",
						JOptionPane.WARNING_MESSAGE);
				file.close();
				fileMain.delete();
				System.exit(0);
			}

			// format date and time for display
			DateTimeFormatter tf = DateTimeFormatter.ofPattern("MMM dd, h:mm a");
			java.time.LocalDateTime now = java.time.LocalDateTime.now();


			try {
				acctNo = JOptionPane.showInputDialog(null,
					"Today is: " + now.format(tf) + "\nAccount Number: ", "ATM - City Central Bank",
					JOptionPane.QUESTION_MESSAGE);

				acctNo = acctNo.trim(); // remove whitespace from input entered

			} catch(NullPointerException e) {
				file.close();
				fileMain.delete();
				System.exit(0);
			}

			file.printf("\n\tATM - City Central Bank\nToday is: %s\n", now.format(tf));
			attempt++;

			if (acctNo.equals("cancel")) {
				JOptionPane.showMessageDialog(null, "Have a nice day!", "Goodbye", JOptionPane.QUESTION_MESSAGE);
				file.close();
				fileMain.delete();
				System.exit(0);
			}

			if (acctNo.length() != 8 || (acctNo.matches("[0-9]+") == false))
				JOptionPane.showMessageDialog(null, "Invalid Account Number!", "Warning", JOptionPane.WARNING_MESSAGE);

		} while (acctNo.length() != 8 || (acctNo.matches("[0-9]+") == false));

		attempt = 0;

		do {
			if (attempt == 3) {
				JOptionPane.showMessageDialog(null, "Max tries exceeded, ATM System locked! Restart to unlock", "ATM - City Central Bank",
						JOptionPane.WARNING_MESSAGE);
				file.close();
				fileMain.delete();
				System.exit(0);
			}

			// create custom pin UI window with input value masking ('*')
			JPanel panel = new JPanel();
			String custom = "Pin: ";
			JLabel label = new JLabel(custom);
			JPasswordField pass = new JPasswordField(10);
			panel.add(label);
			panel.add(pass);
			String[] options = new String[] {"OK", "Cancel"};

			int option = JOptionPane.showOptionDialog(null, panel, "ATM - City Central Bank", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, null);

			if (option == 0) { // pressing OK button

				char[] password = pass.getPassword();
				pin = new String(password);

			}else if (option == 1) { // pressing Cancel button
				file.close();
				fileMain.delete();
				System.exit(0);
			}else {
				file.close();
				fileMain.delete();
				System.exit(0);
			}


			attempt++;

			if(pin.length() != 4 || (pin.matches("[0-9]+") == false))
				JOptionPane.showMessageDialog(null, "Invalid Pin Number!", "Warning", JOptionPane.WARNING_MESSAGE);

		} while(pin.length() != 4 || (pin.matches("[0-9]+") == false));

		String savCheck;

		do {
			try {
				do {
					savCheck0 = JOptionPane.showInputDialog(null, "Savings (s) or Checkings (c): ", "ATM - City Central Bank",
						JOptionPane.QUESTION_MESSAGE);

					if(savCheck0.equals(""))
						JOptionPane.showMessageDialog(null, "Invalid Response!", "Warning", JOptionPane.WARNING_MESSAGE);

				}while(savCheck0.equals(""));

			}catch(NullPointerException e) {
				file.close();
				fileMain.delete();
				System.exit(0);
			}

			savCheck0 = savCheck0.trim();
			savCheck = Character.toUpperCase(savCheck0.charAt(0)) + savCheck0.substring(1);

			if(savCheck.length() != 1 || (savCheck.matches("[A-Za-z]") == false)
					|| !(savCheck.equals("S")) && !(savCheck.equals("C")))
				JOptionPane.showMessageDialog(null, "Invalid option!", "Warning", JOptionPane.WARNING_MESSAGE);

		}while((savCheck.matches("[A-Za-z]") == false)
				|| (!(savCheck.equals("S")) && !(savCheck.equals("C"))));

		String savCheck2 = "";
		if(savCheck.equals("C"))
			savCheck2 = "Checkings";

		else if (savCheck.equals("S"))
			savCheck2 = "Savings";

		// create account
		Account account = new Account(acctNo, pin, ((Math.random() % 23) * 100000), savCheck2);

		String select = "0";

		// load to menu
		menu(account, file, select, savCheck2, fileMain);
	}

	public static void menu(Account account, PrintWriter file, String select, String savCheck, File fileMain) throws IOException, SQLException {
		boolean acctTerminated = false; // flag checks if account has been terminated by user or not

		// open xampp app
		Runtime rt0 = Runtime.getRuntime();
		Process p0 = rt0.exec("C:\\xampp\\xampp-control.exe");

		// create connection ptr to database
	    DBConnector connect = new DBConnector(); // connect class to DB class to perform db operations

	    String bal = df.format(account.getBalance()); // get balance and format it
	    connect.addData(Integer.parseInt(acctNo), Integer.parseInt(pin), bal, savCheck); // add data to db

		do {
			try {
				// display menu for user
				select = JOptionPane.showInputDialog(null,
						"Enter:\n\t1. (1) for balance inquiry\n\t2. (2) for cash withdrawal"
								+ "\n\t3. (3) for cash deposit\n\t4. (4) to terminate account\n\t5."
								+ " (5) to transfer funds\n\t6. (6) (Save) Serialize Account"
								+ "\n\t7. (7) (Load) Deserialize Account \n\t8. (8) to quit\n\n\tSelect your transaction: \n",
						"ATM - City Central Bank", JOptionPane.QUESTION_MESSAGE);

				switch (select) {

				// balance inquiry
				case "1": {
					if (account != null) {
						JOptionPane.showMessageDialog(null, account, "Balance Inquiry",
								JOptionPane.INFORMATION_MESSAGE);
						file.print("\nBalance inquiry...\n" + account);
					} else if (account == null) {
						JOptionPane.showMessageDialog(null, "Account is empty", "Warning!",
								JOptionPane.WARNING_MESSAGE);
						file.print("Balance inquiry...\n\tAccount doesn't exist");
					}
					break;
				}

				// withdraw funds
				case "2": {
					if (acctTerminated == true) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't withdraw!", "Warning!",
								JOptionPane.WARNING_MESSAGE);
						continue;
					}
					ATM w1 = new WithdrawFunds(account);
					w1.withdraw(file);
					break;
				}

				// deposit funds
				case "3": {
					if (acctTerminated == true) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't deposit!", "Warning!",
								JOptionPane.WARNING_MESSAGE);
						continue;
					}
					ATM d1 = new DepositFunds(account);
					d1.depositCash(file);
					break;
				}

				// terminate account
				case "4": {
					if (acctTerminated == true) {
						JOptionPane.showMessageDialog(null, "Account is already empty!", "Warning!",
								JOptionPane.WARNING_MESSAGE);
						continue;
					}

					connect.terminateAccount(Integer.parseInt(account.getAcctNo())); // deletes account from db table @ localhost site

					account = null; // set account to value of null (clearing all attribute values)
					JOptionPane.showMessageDialog(null, "\nAccount has been terminated\n", "Account Termination",
							JOptionPane.INFORMATION_MESSAGE);

					file.println("\nAccount has been terminated");
					acctTerminated = true; // flip flag so that certain ops can't be done under a terminated account
					break;
				}

				// transfer funds
				case "5": {
					if (acctTerminated == true) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't transfer!", "Warning!",
								JOptionPane.WARNING_MESSAGE);
						continue;
					}
					String acctNo2;

					do {
						acctNo2 = JOptionPane.showInputDialog(null, "\nAccount Number 2: ", "Account Terminated",
								JOptionPane.QUESTION_MESSAGE);
						if (acctNo.equals(acctNo2) || acctNo2.length() < 8 || acctNo2.length() > 8
								|| (acctNo2.matches("[0-9]+") == false)) {
							JOptionPane.showMessageDialog(null, "Invalid Account!", "Warning!",
									JOptionPane.WARNING_MESSAGE);
						}
					} while(acctNo.equals(acctNo2)
							|| (acctNo2.length() < 8 || acctNo2.length() > 8 || (acctNo2.matches("[0-9]+") == false)));
					Account account2 = new Account(acctNo2, pin, (Math.random() % 21) * 100000, savCheck);
					ATM t1 = new TransferFunds(account, account2);
					t1.transferFunds(acctNo2, file);
					break;
				}

				// save (serialize) object
				case "6": {
					if (acctTerminated == true) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't serialize!", "Warning!",
								JOptionPane.WARNING_MESSAGE);
						continue;
					}
					String filename = "Data.dat"; // create binary file (.dat = data file) to save object state to

					// Serialization
					try {
						// Save object in a file
						FileOutputStream file1 = new FileOutputStream(filename);
						ObjectOutputStream out = new ObjectOutputStream(file1);

						// method for object serialization
						out.writeObject(account);

						// close serialization process
						out.close();
						file1.close();

						JOptionPane.showMessageDialog(null, "\nObject has been serialized", "Serialize", JOptionPane.QUESTION_MESSAGE);
					}
					catch(IOException ex) {
						System.out.println("IOException is caught");
					}

					break;
				}

				// load (deserialize) object
				case "7": {
					if (acctTerminated == true) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't serialize!", "Warning!",
								JOptionPane.WARNING_MESSAGE);
						continue;
					}
					String filename = "Data.dat";

					Account account1 = null; // create the empty object, request os to allocate chunk of memory to store contents from file

					// Deserialization
					try {
						FileInputStream file2 = new FileInputStream(filename);
						ObjectInputStream in = new ObjectInputStream(file2);

						account1 = (Account)in.readObject(); // store the content from binary file to a reference variable (object)
															 // after reading = deserialize

						// print out the saved data from binary file
						JOptionPane.showMessageDialog(null, "\nAccount Number: " + account1.getAcctNo() + "\nAccount Pin: " +
												account1.getPIN() + "\nAccount Balance: " + df.format(account1.getBalance()) + "\nAccount type: " +
												account1.getType(), "Deserialize", JOptionPane.QUESTION_MESSAGE);
					}
					catch(IOException ex) {
						System.out.println("Deserialization error!");
					}
					catch(ClassNotFoundException ex) {
						System.out.println("Class not found error!");
					}
					break;
				}

				// exit program
				case "8": {
					file.print("\n\nHave a nice day!");
					p0.destroy(); // close xampp app
					String in = JOptionPane.showInputDialog(null, "\nWould you like a receipt? ", "Receipt?",
							JOptionPane.QUESTION_MESSAGE);

					file.close();
					if (in.equals("No") || in.equals("no") || in.equals("NO")) {
						fileMain.delete();
						JOptionPane.showMessageDialog(null, "\nHave a nice day!", "Goodbye", JOptionPane.QUESTION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, "Receipt saved as txt file: " + fileMain.getName(),
								"Receipt", JOptionPane.INFORMATION_MESSAGE);
						Runtime rt = Runtime.getRuntime();
						String file1 = "Receipt.txt";
						JOptionPane.showMessageDialog(null, "\nHave a nice day!", "Goodbye", JOptionPane.QUESTION_MESSAGE);
						Process p = rt.exec("notepad " + file1); // open notepad program with pre-selected file
					}

					System.exit(0);
					break;
				}

				default: {
					JOptionPane.showMessageDialog(null, "Invalid option!", "Warning", JOptionPane.WARNING_MESSAGE);
					break;
				}
			}

			} catch(InputMismatchException inputMismatchException) {
				JOptionPane.showMessageDialog(null, "\tError! Enter a number choice. Invalid option!\n", "Warning",
						JOptionPane.WARNING_MESSAGE);
				input.nextLine();
			} catch(NullPointerException e) {
				p0.destroy(); // close xampp app
				file.close();
				fileMain.delete();
				System.exit(0);
			}
		} while (select != "8");
	}
}