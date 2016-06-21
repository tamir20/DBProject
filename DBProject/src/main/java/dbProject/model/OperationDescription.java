package dbProject.model;

public class OperationDescription {

	private int transactionIndex;
	private int operationIndex;
	private Boolean aborted;

	public OperationDescription(int transactionIndex, int operationIndex, Boolean aborted) {
		this.transactionIndex = transactionIndex;
		this.operationIndex = operationIndex;
		this.aborted = aborted;
	}

	public OperationDescription(OperationDescription op) {
		this.transactionIndex = op.transactionIndex;
		this.operationIndex = op.operationIndex;
		this.aborted = op.aborted;
	}

	public int getTransaction() {
		return this.transactionIndex;
	}

	public int getOperation() {
		return this.operationIndex;
	}

	public Boolean isAborted() {
		return this.aborted;
	}

	public void setAborted(Boolean aborted) {
		this.aborted = aborted;
	}
}
