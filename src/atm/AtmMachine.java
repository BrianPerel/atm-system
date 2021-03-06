package atm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;

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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * ATM_Machine class will generate GUI Performs logic operations for data and
 * choices entered
 */
public class AtmMachine extends JFrame {

	private static String acctNumber;
	private static String pin = "";
	private static String acctTypeOption;
	private static File receiptFile = new File("");
	private static PrintWriter file;
	private static final String WARNING = "Warning";
	private static final String GOODBYE = "Goodbye";
	private static final String ZERO_TO_NINE_REG_EXP = "[0-9]+";
	private static final String HEADER_TITLE = "ATM - City Central Bank";
	private static final long serialVersionUID = -4923383132218838840L;
	private static DecimalFormat df = new DecimalFormat("$###,###.00");
	private static Scanner input = new Scanner(System.in);
	private static SecureRandom randomGenerator = new SecureRandom();

	public static void main(String[] args) throws IOException, SQLException {

		// format date and time for display
		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		
		// creates root directory 'log' if it doesn't already exist
		File theDir = new File("log");
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		
		receiptFile = new File(new StringBuilder("log/").append("Receipt.").append(now.format(DateTimeFormatter.ofPattern("yyyy-MM-d-"))).append("id")
				.append(randomGenerator.nextInt(99)).append(".log").toString());
		file = new PrintWriter(receiptFile);

		getAcctNumberPrompt(now, 0);
		getAcctPinNumberPrompt(0);
		getAcctTypePrompt();

		if (acctTypeOption.equalsIgnoreCase("c") || acctTypeOption.equalsIgnoreCase("checkings")) {
			acctTypeOption = "Checkings";
		} else if (acctTypeOption.equalsIgnoreCase("s") || acctTypeOption.equalsIgnoreCase("savings")) {
			acctTypeOption = "Savings";
		}
		
		String acctCreationDate = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a"));

		// load to menu the new account as you create it
		displayMenu(acctCreationDate, new Account(acctNumber, pin, ((Math.random() % 23) * 100000), acctTypeOption), file, "0", acctTypeOption,
				receiptFile);
	}

	private static void getAcctTypePrompt() {
		do {
			try {
				do {
					acctTypeOption = JOptionPane.showInputDialog(null, "Savings (s) or Checkings (c): ", HEADER_TITLE,
							JOptionPane.QUESTION_MESSAGE);
					
					if(acctTypeOption == null) {
						closeApp();
					}

					if (acctTypeOption.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Invalid Response!", WARNING, JOptionPane.WARNING_MESSAGE);
					}

				} while (acctTypeOption.isEmpty());

			} catch (NullPointerException e) {
				e.printStackTrace();
				closeApp();
			}

			// for some reason when user enters 'checkings' for acct type invalid type flag
			// is raised
			// this is a current work around for that problem
			if (acctTypeOption.equalsIgnoreCase("checkings")) {
				acctTypeOption = "c";
			}

			if (!(acctTypeOption.equalsIgnoreCase("s")) && !(acctTypeOption.equalsIgnoreCase("c"))
					&& !(acctTypeOption.equalsIgnoreCase("savings")
							&& !(acctTypeOption.equalsIgnoreCase("checkings")))) {
				JOptionPane.showMessageDialog(null, "Invalid option!", WARNING, JOptionPane.WARNING_MESSAGE);
			}

		} while (!acctTypeOption.matches("[a-zA-Z]+") || (!(acctTypeOption.equalsIgnoreCase("s"))
				&& !(acctTypeOption.equalsIgnoreCase("c"))
				&& !(acctTypeOption.equalsIgnoreCase("savings") && !(acctTypeOption.equalsIgnoreCase("checkings")))));
	}

	/**
	 * Handles the retrieval of acct pin number from user
	 * @param attempts tracks the number of attempts used to enter PIN
	 */
	private static void getAcctPinNumberPrompt(int attempts) {
		do {
			if (attempts == 3) {
				JOptionPane.showMessageDialog(null, "Max tries exceeded, ATM System locked! Restart to unlock",
						HEADER_TITLE, JOptionPane.WARNING_MESSAGE);
				closeApp();
			}

			// create custom pin UI window with input value masking ('*')
			JPanel panel = new JPanel();
			JPasswordField maskedPassword = new JPasswordField(10);
			panel.add(new JLabel("Pin: "));
			panel.add(maskedPassword);

			int option = JOptionPane.showOptionDialog(null, panel, HEADER_TITLE, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new String[] { "OK", "Cancel" }, null);

			if (option == 0) { // if you press OK button
				pin = new String(maskedPassword.getPassword());
			} else { // if you press cancel button
				closeApp();
			}

			attempts++;

			if (pin.length() != 4 || !(pin.matches(ZERO_TO_NINE_REG_EXP))) {
				JOptionPane.showMessageDialog(null, "Invalid Pin Number!", WARNING, JOptionPane.WARNING_MESSAGE);
			}

		} while (pin.length() != 4 || !(pin.matches(ZERO_TO_NINE_REG_EXP)));
	}

	/**
	 * Handles the retrieval of the acct number from the user
	 * @param now the current date and time
	 * @param attempts tracks the number of attempts used to enter PIN
	 */
	private static void getAcctNumberPrompt(java.time.LocalDateTime now, int attempts) {
		DateTimeFormatter dateTimeFormat;
		
		do {
			if (attempts == 3) {
				JOptionPane.showMessageDialog(null, "Max tries exceeded, ATM System locked! Restart to unlock", "ATM",
						JOptionPane.WARNING_MESSAGE);
				closeApp();
			}

			// format date and time for display for app's initial window
			dateTimeFormat = DateTimeFormatter.ofPattern("MMM dd, h:mm a");

			try {
				acctNumber = JOptionPane.showInputDialog(null,
						"Today is: ".concat(now.format(dateTimeFormat)).concat("\nAccount Number: "), HEADER_TITLE,
						JOptionPane.QUESTION_MESSAGE);
								
				if (acctNumber == null) {
					closeApp();
				}

			} catch (NullPointerException e) {
				e.printStackTrace();
				closeApp();
			}

			file.printf("%n\tATM - City Central Bank%nToday is: %s%n", now.format(dateTimeFormat));
			attempts++;

			if (acctNumber.length() != 8 || !(acctNumber.matches(ZERO_TO_NINE_REG_EXP))) {
				JOptionPane.showMessageDialog(null, "Invalid Account Number!", WARNING, JOptionPane.WARNING_MESSAGE);
			}

		} while (acctNumber.length() != 8 || !(acctNumber.matches(ZERO_TO_NINE_REG_EXP)));
	}

	/**
	 * Displays the user with the ATM menu
	 * @param argAccount
	 * @param argFile
	 * @param argSelect
	 * @param argAcctTypeOption
	 * @param argReceiptFile
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void displayMenu(String argCreationDate, Account argAccount, PrintWriter argFile, String argSelect, String argAcctTypeOption,
			File argReceiptFile) throws IOException, SQLException {
		
		boolean isAcctTerminated = false; // flag checks if account has been terminated by user or not
		
		// open xampp app
		Process processToOpenXampp = Runtime.getRuntime().exec("C:\\xampp\\xampp-control.exe");

		// create connection ptr to database
		DbConnector connect = new DbConnector(); // connect class to DB class to perform db operations
		connect.addData(argCreationDate, Integer.parseInt(acctNumber), Integer.parseInt(pin), df.format(argAccount.getBalance()),
				argAcctTypeOption); // add data to db

		do {
			try {
				// display menu for user
				argSelect = JOptionPane.showInputDialog(null,
						"Enter:\n\t1. (1) for balance inquiry\n\t2. (2) for cash withdrawal"
								.concat("\n\t3. (3) for cash deposit\n\t4. (4) to terminate account\n\t5.")
								.concat(" (5) to transfer funds\n\t6. (6) (Save) Serialize Account")
								.concat("\n\t7. (7) (Load) Deserialize Account \n\t8. (8) to quit\n\n\tSelect your transaction: \n"),
						HEADER_TITLE, JOptionPane.QUESTION_MESSAGE);
				
				if(argSelect == null) {
					closeApp();
				}

				switch (argSelect) {
				// balance inquiry
				case "1": {
					if (argAccount != null) {
						JOptionPane.showMessageDialog(null, argAccount, "Balance Inquiry",
								JOptionPane.INFORMATION_MESSAGE);
						argFile.print("\nBalance inquiry...\n" + argAccount);
					} else {
						JOptionPane.showMessageDialog(null, "Account is empty", WARNING, JOptionPane.WARNING_MESSAGE);
						argFile.print("Balance inquiry...\n\tAccount doesn't exist");
					}
					
					break;
				}

				// withdraw funds
				case "2": {
					if (isAcctTerminated) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't withdraw!", WARNING,
								JOptionPane.WARNING_MESSAGE);
					} else {
						AbstractAtm w1 = new WithdrawFunds(argAccount);
						w1.withdraw(argFile);
					}
					
					break;
				}

				// deposit funds
				case "3": {
					if (isAcctTerminated) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't deposit!", WARNING,
								JOptionPane.WARNING_MESSAGE);
					} else {
						AbstractAtm d1 = new DepositFunds(argAccount);
						d1.depositCash(argFile);
					}
					
					break;
				}

				// terminate account
				case "4": {
					if (isAcctTerminated) {
						JOptionPane.showMessageDialog(null, "Account is already terminated!", WARNING,
								JOptionPane.WARNING_MESSAGE);
					} else {
						connect.terminateAccount(Integer.parseInt(argAccount.getAcctNumber())); // deletes account from db
																								// table @ localhost site
						argAccount = null; // set account to value of null (clearing all attribute values)
						JOptionPane.showMessageDialog(null, "Account has been terminated\n", "Account Termination",
								JOptionPane.INFORMATION_MESSAGE);
	
						argFile.println("\nAccount has been terminated");
						isAcctTerminated = true; // flip flag so that certain ops can't be done under a terminated account
					}
					
					break;
				}

				// transfer funds
				case "5": {
					if (isAcctTerminated) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't transfer!", WARNING,
								JOptionPane.WARNING_MESSAGE);
					} else {
						String acctNo2;
	
						do {
							acctNo2 = JOptionPane.showInputDialog(null, "\nAccount Number 2: ", "Account Terminated",
									JOptionPane.QUESTION_MESSAGE);
							
							if(acctNo2 == null) {
								closeApp();
							}
							
							if (acctNumber.equals(acctNo2) || acctNo2.length() != 8
									|| !(acctNo2.matches(ZERO_TO_NINE_REG_EXP))) {
								JOptionPane.showMessageDialog(null, "Invalid Account!", WARNING,
										JOptionPane.WARNING_MESSAGE);
							}
						} while (acctNumber.equals(acctNo2)
								|| (acctNo2.length() != 8 || !(acctNo2.matches(ZERO_TO_NINE_REG_EXP))));
						Account account2 = new Account(acctNo2, pin, (Math.random() % 21) * 100000, argAcctTypeOption);
						AbstractAtm t1 = new TransferFunds(argAccount, account2);
						t1.transferFunds(acctNo2, argFile);
					}
					
					break;
				}

				// save (serialize) object
				case "6": {
					if (isAcctTerminated) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't serialize!", WARNING,
								JOptionPane.WARNING_MESSAGE);
					} else {
						serialize(argAccount);
					}

					break;
				}

				// load (deserialize) object
				case "7": {
					if (isAcctTerminated) {
						JOptionPane.showMessageDialog(null, "Account is empty, can't serialize!", WARNING,
								JOptionPane.WARNING_MESSAGE);
					} else {
						deserialize();
					}

					break;
				}

				// exit program
				case "8": {
					argFile.print("\n\n\nHave a nice day!");
					processToOpenXampp.destroy(); // close xampp app
					String in = "";
					in = JOptionPane.showInputDialog(null, "\nWould you like a receipt? ", "Receipt?",
							JOptionPane.QUESTION_MESSAGE);
					
					if(in == null) {
						closeApp();
					}

					argFile.close();

					if (in.equalsIgnoreCase("no") || in.equalsIgnoreCase("n") || in.equalsIgnoreCase("cancel")) {
						try {
							Files.delete(argReceiptFile.toPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						JOptionPane.showMessageDialog(null, "\nHave a nice day!", GOODBYE,
								JOptionPane.QUESTION_MESSAGE);
						System.exit(0);
					}

					JOptionPane.showMessageDialog(null, "Receipt saved as: " + argReceiptFile.getName(), "Receipt",
							JOptionPane.INFORMATION_MESSAGE);
					JOptionPane.showMessageDialog(null, "\nHave a nice day!", GOODBYE, JOptionPane.QUESTION_MESSAGE);
					Runtime.getRuntime().exec("notepad " + argReceiptFile); // open notepad program with pre-selected
																			// file

					System.exit(0);
					break;
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
				e.printStackTrace();
				processToOpenXampp.destroy(); // close xampp app
				closeApp();
			}
		} while (argSelect != null && !argSelect.equals("8"));
	}

	public static void serialize(Account argAccount) {
		// Serialization
		try {
			// Save object state to a binary file
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("AccountData.dat"));

			out.writeObject(argAccount); 			

			// close serialization process
			out.close();

			JOptionPane.showMessageDialog(null, "\nObject has been serialized", "Serialize",
					JOptionPane.QUESTION_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deserialize() {
		// Deserialization process
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("AccountData.dat"));
			Account account1 = (Account) in.readObject(); // store the content from binary file to a reference
			// variable (object)
			// after reading = deserialize

			// print out the saved data from binary file
			JOptionPane.showMessageDialog(null,
					new StringBuilder().append("\nAccount Number: ").append(account1.getAcctNumber())
							.append("\nAccount Pin: ").append(account1.getPIN()).append("\nAccount Balance: ")
							.append(df.format(account1.getBalance())).append("\nAccount type: ")
							.append(account1.getType()),
					"Deserialize", JOptionPane.QUESTION_MESSAGE);

			in.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Deserialization error!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Class not found error!");
		}
	}

	public static void closeApp() {
		file.close();

		try {
			Files.delete(receiptFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void deleteDB() {
		
		// Statements to delete database:
		Process process = null;
		
		try {
			process = Runtime.getRuntime().exec("C:\\xampp\\xampp-control.exe");
			new DbConnector().deleteDB(); // create runtime instance to start and open xampp app
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} 
		
		if(process != null) {
			process.destroy(); // close xampp app
		}
	}
}