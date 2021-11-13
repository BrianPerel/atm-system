package atm;

import java.io.Serializable;

/**
 * Class to setup Account object, operations Attributes: account type, number, pin, balance
 */
public class Account implements Serializable {

	private static final long serialVersionUID = -9178301191101564867L;
	private int hashCodeNumber;
	private String acctNumber;
	private String acctPin;
	private String acctType;
	private double acctBalance;

	public Account() {
		this(null, null, 0.00, null);
	}

	public Account(String acctNo, String pin) {
		this.acctNumber = acctNo;
		this.acctPin = pin;
	}

	public Account(String acctNo, String pin, double balance, String acctType) throws IllegalArgumentException {
		this.acctNumber = acctNo;
		this.acctPin = pin;
		this.acctType = acctType;

		if (balance < 0) {
			throw new IllegalArgumentException("Value less than 0!");
		}

		this.acctBalance = balance;
	}

	public void setNumber(int number) {
		this.hashCodeNumber = number;
	}

	public void setAcctNumber(String acctNo) {
		this.acctNumber = acctNo;
	}

	public void setPIN(String pin) {
		this.acctPin = pin;
	}

	public void setBalance(double balance) throws IllegalArgumentException {
		this.acctBalance = balance;

		if (balance < 0) {
			throw new IllegalArgumentException("Value less than 0!");
		}
	}

	public void setType(String acctType) {
		this.acctType = acctType;
	}

	public int getNumber() {
		return hashCodeNumber;
	}

	public String getAcctNumber() {
		return acctNumber;
	}

	public String getPIN() {
		return acctPin;
	}

	public double getBalance() {
		return acctBalance;
	}

	public String getType() {
		return acctType;
	}

	@Override
	public String toString() {
		return String.format("\tAccount number: %s%n\tPIN number: %s%n\tAccount balance: $%,.2f%n\tAccount type: %s%n",
				acctNumber, acctPin, acctBalance, acctType);
	}

	@Override
	public boolean equals(Object o) {
		return !(o instanceof Account) || o != this;
	}

	@Override
	public int hashCode() {
		return hashCodeNumber * 12;
	}
}
