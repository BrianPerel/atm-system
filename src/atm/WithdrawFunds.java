package atm;

import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.sql.*;

/**
 * Inherit ATM program for primary methods / operations Performs withdraw
 * operations -prompt for withdraw amount, check valid range and format, perform
 * op -program writes to file while class is executed
 */
class WithdrawFunds extends ATM {

	private final Account account;
	static DecimalFormat df = new DecimalFormat("$###,###.00");

	public WithdrawFunds(Account account) {
		super(account);
		this.account = account;
	}

	@Override
	public void withdraw(PrintWriter file) throws IOException {

		String money0;

		// check for numeric input format, loop until correct format entered
		do {
			money0 = JOptionPane.showInputDialog(null, "\nWithdraw amount: $", "Withdraw",
					JOptionPane.QUESTION_MESSAGE);

			if (!money0.matches("[0-9.]+")) {
				JOptionPane.showMessageDialog(null, "Invalid amount!", "Warning", JOptionPane.WARNING_MESSAGE);
			}

		} while (!money0.matches("[0-9.]+"));

		final double money = Double.parseDouble(money0);
		file.print("\n\tWithdraw amount: $" + money);

		// valid range
		if (money > 0 && money < account.getBalance()) {
			this.account.setBalance(this.account.getBalance() - money);
			file.print("\n\n\nWithdrawing...");
			JOptionPane.showMessageDialog(null,
					"Withdraw Complete! Your New Balance is: " + df.format(account.getBalance()), "Withdraw",
					JOptionPane.QUESTION_MESSAGE);
			file.printf("%nWithdraw complete! Your New Balance is: $%,.2f%n", account.getBalance());

			// update db record in table (since withdraw op performed on account)
			try {
				// create connection ptr to database
				DBConnector connect = new DBConnector(); // connect class to DB class to perform db operations
				String bal = df.format(account.getBalance()); // get balance and format it
				connect.updateData(bal, Integer.parseInt(account.getAcctNo())); // add data to db
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		} else if (money <= 0) {

			try {
				JOptionPane.showMessageDialog(null, "\tAmount entered is too little!", "Warning",
						JOptionPane.WARNING_MESSAGE);

				if (money == 0) {
					JOptionPane.showMessageDialog(null, "\nWithdraw operation cancelled...");
					file.println("Withdraw operation cancelled...");
				} else {
					withdraw(file);
				}
			} catch (InputMismatchException inputMismatchException) {
				JOptionPane.showMessageDialog(null, "\tError! Enter a number choice. Invalid option!\n", "Warning",
						JOptionPane.WARNING_MESSAGE);
				withdraw(file);
			}
		}

		else if (money > account.getBalance()) {
			JOptionPane.showMessageDialog(null, "\tError: You don't have sufficient funds!", "Warning",
					JOptionPane.QUESTION_MESSAGE);

			if (money == 0) {
				JOptionPane.showMessageDialog(null, "\nWithdraw operation cancelled...");
				file.printf("Withdraw operation cancelled...");
			} else {
				withdraw(file);
			}
		}
	}

	@Override
	public void depositCash(PrintWriter file) throws IOException {
	}

	@Override
	public void transferFunds(String acctNo2, PrintWriter file) throws IOException {
	}
}