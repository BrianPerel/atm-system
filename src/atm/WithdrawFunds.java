package atm;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;

import javax.swing.JOptionPane;

/**
 * Inherit ATM program for primary methods / operations Performs withdraw
 * operations -prompt for withdraw amount, check valid range and format, perform
 * op -program writes to file while class is executed
 */
public class WithdrawFunds extends ATM {

	private final Account account;
	static DecimalFormat df = new DecimalFormat("$###,###.00");

	public WithdrawFunds(Account account) {
		super(account);
		this.account = account;
	}

	@Override
	public void withdraw(PrintWriter file) throws IOException {

		final double money = getWithdrawAmt(file);

		// valid range
		if (money > 0 && money < account.getBalance()) {
			this.account.setBalance(this.account.getBalance() - money);
			file.print("\n\n\n Withdrawing...");
			JOptionPane.showMessageDialog(null,
					"Withdraw Complete! Your New Balance is: " + df.format(account.getBalance()), "Withdraw",
					JOptionPane.QUESTION_MESSAGE);
			file.printf("%nWithdraw complete! Your New Balance is: $%,.2f%n", account.getBalance());

			// update db record in table (since withdraw op performed on account)
			try {
				// create connection ptr to database
				// connect class to DB class to perform db operations and add data to db
				new DBConnector().updateData(df.format(account.getBalance()), Integer.parseInt(account.getAcctNumber())); 
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

			withdraw(file);
		}
	}

	public double getWithdrawAmt(PrintWriter file) {
		String money0;

		// check for numeric input format, loop until correct format entered
		do {
			money0 = JOptionPane.showInputDialog(null, "Withdraw amount: $", "Withdraw",
					JOptionPane.QUESTION_MESSAGE);

			if (!money0.matches("[0-9.]+")) {
				JOptionPane.showMessageDialog(null, "Invalid amount!", "Warning", JOptionPane.WARNING_MESSAGE);
			}

		} while (!money0.matches("[0-9.]+"));

		final double money = Double.parseDouble(money0);
		file.print("\n\tWithdraw amount: $" + money);
		return money;
	}

	@Override
	public void depositCash(PrintWriter file) throws IOException {}

	@Override
	public void transferFunds(String acctNo2, PrintWriter file) throws IOException {}
}