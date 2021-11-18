package atm;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;

import javax.swing.JOptionPane;

/**
 * Inherit ATM program for primary methods / operations Performs deposit ATM
 * operation -asks for deposit amount, checks amount range and format, performs
 * op
 */
public class DepositFunds extends AbstractAtm {
	
	private final Account account;
	private static DecimalFormat formatter = new DecimalFormat("$###,###.00"); // for decimal rounding (to 2 places, plus $ and comma
																// insertion)

	public DepositFunds(Account account) {
		super(account);
		this.account = account;
	}

	@Override
	public void depositCash(PrintWriter file) {

		double money = getDepositAmt(file);

		// account amount cannot supersede or excede this amount (1-1000000000)
		if (money >= 1 && money < 1000000000) {
			account.setBalance(account.getBalance() + money);
			file.print("\nDepositing...");
			JOptionPane.showMessageDialog(null,
					"Deposit Complete! Your New Balance is: " + formatter.format(this.account.getBalance()));
			file.printf("Deposit Complete! Your New Balance is: $%,.2f%n", this.account.getBalance());

			// update db record in table (since withdraw op performed on account)
			try {
				// create connection ptr to database
				// connect class to DB class to perform db operations
				new DBConnector().updateData(formatter.format(account.getBalance()), Integer.parseInt(account.getAcctNumber())); 
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				JOptionPane.showMessageDialog(null, "\tWarning: You don't have sufficient funds!", "Warning",
						JOptionPane.WARNING_MESSAGE);

				if (money == 0) {
					JOptionPane.showMessageDialog(null, "\nDeposit operation cancelled...");
					file.print("Deposit operation cancelled!");
				} else {
					depositCash(file);
				}
			} catch (InputMismatchException inputMismatchException) {
				JOptionPane.showMessageDialog(null, "\tWarning! Enter a number choice\n", "Warning",
						JOptionPane.WARNING_MESSAGE);
				depositCash(file);
			}
		}
	}

	/**
	 * Obtains the desired deposit amount from the user
	 * @param file the file we're logging to 
	 * @return the deposit amount
	 */
	public double getDepositAmt(PrintWriter file) {
		
		String depositAmt;

		do {
			depositAmt = JOptionPane.showInputDialog(null, "Deposit amount: $", "Deposit",
					JOptionPane.QUESTION_MESSAGE);

			if (!depositAmt.matches("[0-9.]+")) {
				JOptionPane.showMessageDialog(null, "Invalid amount!", "Warning", JOptionPane.WARNING_MESSAGE);
			}

		} while (!depositAmt.matches("[0-9.]+"));

		double money = Double.parseDouble(depositAmt);
		file.println("\nDeposit amount: $" + money);
		return money;
	}

	@Override
	public void withdraw(PrintWriter file) throws IOException {}

	@Override
	public void transferFunds(String acctNo2, PrintWriter file) throws IOException {}
}
