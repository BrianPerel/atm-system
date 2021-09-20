package atm;

/**
 * Class to setup Account object, use Serializable interface to use serialize
 * operations Attributes: account type, number, pin, balance
 */
class Account implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int number;
	private String acctNo;
	private String pin;
	private double balance;
	private String acctType;

	public Account() {
		this(null, null, 0.00, null);
	}

	public Account(String acctNo, String pin) {
		this.acctNo = acctNo;
		this.pin = pin;
	}

	public Account(String acctNo, String pin, double balance, String acctType) throws IllegalArgumentException {
		this.acctNo = acctNo;
		this.pin = pin;
		this.acctType = acctType;

		if (balance < 0)
			throw new IllegalArgumentException("Value less than 0!");

		this.balance = balance;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public void setPIN(String pin) {
		this.pin = pin;
	}

	public void setBalance(double balance) throws IllegalArgumentException {
		this.balance = balance;

		if (balance < 0) {
			throw new IllegalArgumentException("Value less than 0!");
		}
	}

	public void setType(String acctType) {
		this.acctType = acctType;
	}

	public int getNumber() {
		return this.number;
	}

	public String getAcctNo() {
		return this.acctNo;
	}

	public String getPIN() {
		return this.pin;
	}

	public double getBalance() {
		return this.balance;
	}

	public String getType() {
		return this.acctType;
	}

	@Override
	public String toString() {
		return String.format(
				"\tAccount number: %s\n\tPIN number: %s" + "\n\tAccount balance: $%,.2f\n\tAccount type: %s\n",
				this.acctNo, this.pin, this.balance, this.acctType);
	}

	@Override
	public boolean equals(Object o) {
		return !(o instanceof Account) || o != this;
	}

	@Override
	public int hashCode() {
		return number * 12;
	}
}
