package atm;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;

import javax.swing.JOptionPane;

/**
 * Inherit ATM program for primary methods / operations Performs transfer
 * operations -prompt for transfer amount, ask for account to transfer to, check
 * boundary range and format, perform op -performs file write while class is
 * executed
 */
public class TransferFunds extends AbstractAtm {

	private final Account account;
	private final Account accountTwo; // first account, transfer funds from. second account, transfer funds to
	private static DecimalFormat df = new DecimalFormat("$###,###.00"); // formatting to make values include a '$', commas, and
																// rounding to 2 places

	public TransferFunds(Account argAccount, Account argAccountTwo) {
		super(argAccount);
		this.account = argAccount;
		this.accountTwo = argAccountTwo;
	}

	@Override
	public void transferFunds(String acctNo2, PrintWriter file) throws IOException {

		String transferAmt;
		double money = getTransferAmt(file);

		// valid amount range
		if (money > 0 && money < this.account.getBalance()) {
			this.account.setBalance(this.account.getBalance() - money);
			file.print("\n\n Transferring...");
			JOptionPane.showMessageDialog(null,
					new StringBuilder("\nTransfer complete!\n\nYour New Balance for Account 1 (")
						.append(account.getAcctNumber()).append(") is: ")
						.append(df.format(this.account.getBalance())).append("\nYour New Balance for Account 2 (")
						.append(this.accountTwo.getAcctNumber()).append(") is: ")
						.append(df.format(this.accountTwo.getBalance())),
					"ATM - City Central Bank", JOptionPane.QUESTION_MESSAGE);

			file.print("Transfer complete! Your New Balance for Account " + account.getAcctNumber() + " is: "
					+ df.format(this.account.getBalance()) + "\nYour New Balance for Account "
					+ this.accountTwo.getAcctNumber() + " is: " + df.format(this.accountTwo.getBalance()));

			// update db record in table (since withdraw op performed on account)
			try {
				// create connection ptr to database
				// connect class to DB class to perform db operations and add data to db
				new DbConnector().updateData(df.format(account.getBalance()), Integer.parseInt(account.getAcctNumber())); 
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		} else if (money <= 0) {

			try {
				transferAmt = JOptionPane.showInputDialog(null, "Amount entered is too little!", "Warning",
						JOptionPane.QUESTION_MESSAGE);
				
				if (Double.parseDouble(transferAmt) == 0) {
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

	public double getTransferAmt(PrintWriter file) {
		String transferAmt;

		// amount entered must be of numeric format, re-prompt every time format is incorrect
		do {
			transferAmt = JOptionPane.showInputDialog(null, "\nTransfer amount: $", "ATM", JOptionPane.QUESTION_MESSAGE);

			if (!transferAmt.matches("[0-9.]+")) {
				JOptionPane.showMessageDialog(null, "Invalid amount!", "Warning", JOptionPane.WARNING_MESSAGE);
			}

		} while (!transferAmt.matches("[0-9.]+"));

		double money = Double.parseDouble(transferAmt);
		file.print("\n\tTransfer amount: $" + money);
		return money;
	}

	@Override
	public void depositCash(PrintWriter file) throws IOException {}

	@Override
	public void withdraw(PrintWriter file) throws IOException {}
}