package atm;

import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.io.IOException;

/**
 * Inherit ATM program for primary methods / operations Performs transfer
 * operations -prompt for transfer amount, ask for account to transfer to, check
 * boundary range and format, perform op -performs file write while class is
 * executed
 */
class TransferFunds extends ATM {

	private final Account account; // first account, transfer funds from
	private final Account account2; // second account, transfer funds to
	static DecimalFormat formatter = new DecimalFormat("$###,###.00"); // formatting to make values include a '$', commas, and
																// rounding to 2 places

	public TransferFunds(Account account, Account account2) {
		super(account);
		this.account = account;
		this.account2 = account2;
	}

	@Override
	public void transferFunds(String acctNo2, PrintWriter file) throws IOException {

		String money0;

		// amount entered must be of numeric format, re-prompt every time format is incorrect
		do {
			money0 = JOptionPane.showInputDialog(null, "\nTransfer amount: $", "ATM", JOptionPane.QUESTION_MESSAGE);

			if (!money0.matches("[0-9.]+")) {
				JOptionPane.showMessageDialog(null, "Invalid amount!", "Warning", JOptionPane.WARNING_MESSAGE);
			}

		} while (!money0.matches("[0-9.]+"));

		double money = Double.parseDouble(money0);
		file.print("\n\tTransfer amount: $" + money);

		// valid amount range
		if (money > 0 && money < this.account.getBalance()) {
			this.account.setBalance(this.account.getBalance() - money);
			file.print("\n\n Transferring...");
			JOptionPane.showMessageDialog(null,
					"\nTransfer complete!\n\nYour New Balance for Account 1 (" + account.getAcctNo() + ") is: "
							+ formatter.format(this.account.getBalance()) + "\nYour New Balance for Account 2 ("
							+ this.account2.getAcctNo() + ") is: " + formatter.format(this.account2.getBalance()),
					"ATM - City Central Bank", JOptionPane.QUESTION_MESSAGE);

			file.printf("Transfer complete! Your New Balance for Account " + account.getAcctNo() + " is: "
					+ formatter.format(this.account.getBalance()) + "\nYour New Balance for Account "
					+ this.account2.getAcctNo() + " is: " + formatter.format(this.account2.getBalance()));

			// update db record in table (since withdraw op performed on account)
			try {
				// create connection ptr to database
				// connect class to DB class to perform db operations and add data to db
				new DBConnector().updateData(formatter.format(account.getBalance()), Integer.parseInt(account.getAcctNo())); 
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		} else if (money <= 0) {

			try {
				money0 = JOptionPane.showInputDialog(null, "Amount entered is too little!", "Warning",
						JOptionPane.QUESTION_MESSAGE);
				money = Double.parseDouble(money0);

				if (money == 0) {
					JOptionPane.showMessageDialog(null, "\nTransfer operation cancelled...", "Cancelled",
							JOptionPane.QUESTION_MESSAGE);
					file.println("Transfer operation cancelled");
				} else {
					transferFunds(acctNo2, file);
				}
			} catch (InputMismatchException inputMismatchException) {
				JOptionPane.showMessageDialog(null, "\tError! Enter a number choice. Invalid option!\n", "Warning",
						JOptionPane.QUESTION_MESSAGE);

				transferFunds(acctNo2, file);
			}
		}

		// checks if transfer amount is greater than available amount in account
		else if (money > this.account.getBalance()) {
			JOptionPane.showMessageDialog(null, "\tError: You don't have sufficient funds!", "Warning",
					JOptionPane.WARNING_MESSAGE);

			transferFunds(acctNo2, file);
		}
	}

	@Override
	public void depositCash(PrintWriter file) throws IOException {
	}

	@Override
	public void withdraw(PrintWriter file) throws IOException {
	}
}
