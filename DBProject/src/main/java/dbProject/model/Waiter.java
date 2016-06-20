package dbProject.model;

public class Waiter {

	private int transactionIndex;
	private LockType type;

	public Waiter(int transactionIndex, LockType type) {
		this.transactionIndex = transactionIndex;
		this.type = type;
	}

	public int getIndex() {
		return this.transactionIndex;
	}

	public LockType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		if (this.type == LockType.READ) {
			return this.transactionIndex + " READ";
		}
		return this.transactionIndex + " WRITE";
	}
}
