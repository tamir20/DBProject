package dbProject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class Scheduler {
	// after each operation, need to awake transactions and check for deadlock

	public static enum SchedulerType {
		SERIAL, ROUND_ROBIN, PSUDO_RANDOM
	}

	private List<List<OperationDescription>> transactionsBackup;
	private List<List<OperationDescription>> transactions;
	private List<List<OperationDescription>> transactionsSleep;
	private SchedulerType type;
	private Random seedRand;

	public Scheduler(List<List<Operation>> transactions) {
		this.transactions = new LinkedList<List<OperationDescription>>();
		this.transactionsBackup = new LinkedList<List<OperationDescription>>();
		this.transactionsSleep = new LinkedList<List<OperationDescription>>();
		this.type = SchedulerType.SERIAL;
		this.seedRand = new Random();

		// initialize the current transactions and operations and the backup
		for (int i = 0; i < transactions.size(); i++) {
			this.transactions.add(new LinkedList<OperationDescription>());
			this.transactionsBackup.add(new LinkedList<OperationDescription>());
		}
		for (int i = 0; i < transactions.size(); i++) {
			for (int j = 0; j < transactions.get(i).size(); j++) {
				OperationDescription od = new OperationDescription(i, j, false);
				this.transactions.get(i).add(od);
				this.transactionsBackup.get(i).add(od);
			}
		}

		// remove empty transactions
		Iterator<List<OperationDescription>> iter = this.transactions.listIterator();
		while (iter.hasNext()) {
			List<OperationDescription> transaction = iter.next();
			if (transaction.isEmpty()) {
				iter.remove();
			}
		}

		// remove empty transactions in backup
		iter = this.transactionsBackup.listIterator();
		while (iter.hasNext()) {
			List<OperationDescription> transaction = iter.next();
			if (transaction.isEmpty()) {
				iter.remove();
			}
		}
	}

	public void setSchedulerType(SchedulerType type) {
		this.type = type;
	}

	public void setSeed(long seed) {
		this.seedRand = new Random(seed);
	}

	public void abortTransaction(int index) {
		// I assume the transactions have at least 1 operation
		List<OperationDescription> abortedTransaction = new LinkedList<OperationDescription>();

		// try to remove the transaction from the transactions list
		for (int i = 0; i < this.transactions.size(); i++) {
			if ((this.transactions.get(i).get(0)).getTransaction() == index) {
				abortedTransaction = getAbortedTransaction(this.transactions.get(i));
				this.transactions.remove(i);
			}
		}

		// try to remove the transaction from the sleeping transactions list
		for (int i = 0; i < this.transactionsSleep.size(); i++) {
			if ((this.transactionsSleep.get(i).get(0)).getTransaction() == index) {
				abortedTransaction = getAbortedTransaction(this.transactions.get(i));
				this.transactionsSleep.remove(i);
			}
		}

		// run the transaction in reverse from the aborted operation in
		// aborted mode
		this.transactions.add(abortedTransaction);
	}

	public OperationDescription next() {
		// I assume the transactions have at least 1 operation

		if (this.transactions.isEmpty()) {
			return null;
		}
		// nextTran holds the index of the transaction in the list
		int nextTran = 0;
		switch (this.type) {
		case SERIAL:
			// because it is always the first transaction left from the
			// transactions
			nextTran = 0;
			break;
		case PSUDO_RANDOM:
			nextTran = this.seedRand.nextInt(this.transactions.size());
			break;
		case ROUND_ROBIN:
			// and later on we will send this transaction to the end of the
			// transactions list (list is used as a queue)
			nextTran = 0;
			break;
		}

		// this is the next operation to execute so i save its index
		OperationDescription result = new OperationDescription(this.transactions.get(nextTran).get(0));

		// remove the operation from the transaction. if the transaction is now
		// empty, remove the transaction from the transaction list
		this.transactions.get(nextTran).remove(0);
		if (this.transactions.get(nextTran).isEmpty()) {
			this.transactions.remove(nextTran);
		} else {
			// if the transaction isn't empty and it's round robin, send the
			// transaction to the end of the transactions list
			if (this.type == SchedulerType.ROUND_ROBIN) {
				List<OperationDescription> currentTran = this.transactions.remove(nextTran);
				this.transactions.add(currentTran);
			}
		}

		if (result.getOperation() == 0 && result.isAborted()) {
			// if the aborted transaction has ended, rerun the original
			// transaction from the backup
			for (int i = 0; i < this.transactionsBackup.size(); i++) {
				if ((this.transactionsBackup.get(i).get(0)).getTransaction() == result.getTransaction()) {
					List<OperationDescription> newTransaction = new LinkedList<OperationDescription>(
							this.transactionsBackup.get(i));
					this.transactions.add(newTransaction);
				}
			}
		}
		return result;
	}

	public Boolean hasNext() {
		return !this.transactions.isEmpty();
	}

	public Boolean isDeadlock() {
		// this can cause livelock, better to use recommendAbort from
		// LockManager and check that it returns -1 (means there is no deadlock)
		if (this.transactions.isEmpty() && !this.transactionsSleep.isEmpty()) {
			return true;
		}
		return false;
	}

	public void sleepTransaction(int transactiosIndex) {
		// I assume the transactions have at least 1 operation

		List<OperationDescription> transaction;
		for (int i = 0; i < this.transactions.size(); i++) {
			if ((this.transactions.get(i).get(0)).getTransaction() == transactiosIndex) {
				transaction = this.transactions.remove(i);
				this.transactionsSleep.add(transaction);
			}
		}
	}

	public void awakeTransactions(Set<Integer> transactions) {
		// remove empty transactions
		Iterator<Integer> iter = transactions.iterator();
		while (iter.hasNext()) {
			int transactionIndex = iter.next();
			for (int i = 0; i < this.transactionsSleep.size(); i++) {
				if ((this.transactionsSleep.get(i).get(0)).getTransaction() == transactionIndex) {
					List<OperationDescription> transaction = this.transactionsSleep.remove(i);
					// important to insert the transaction at the end of the
					// list for the round robin and serial correctness
					this.transactions.add(transaction);
				}
			}
		}
	}

	private List<OperationDescription> getAbortedTransaction(List<OperationDescription> transaction) {
		// return the aborted transaction left to excecute, or if the
		// transaction aborted before it started, return the original
		// transaction
		List<OperationDescription> result = new LinkedList<OperationDescription>();
		Stack<OperationDescription> stack = new Stack<OperationDescription>();
		int currentOperation = transaction.get(0).getOperation();
		Boolean operationFound = false;

		// fill the stack in the operations which are done
		for (int i = 0; i < this.transactionsBackup.size(); i++) {
			if (this.transactionsBackup.get(i).get(0).getTransaction() == transaction.get(0).getTransaction()) {
				for (int j = 0; j < transactionsBackup.get(i).size() && !operationFound; j++) {
					if (transactionsBackup.get(i).get(j).getOperation() != currentOperation) {
						stack.push(new OperationDescription(transactionsBackup.get(i).get(j)));
					} else {
						operationFound = true;
					}
				}
			}
		}

		if (stack.isEmpty()) {
			// if there is not operation which is already done, return the
			// transaction from the backup
			for (int i = 0; i < this.transactionsBackup.size(); i++) {
				if (this.transactionsBackup.get(i).get(0).getTransaction() == transaction.get(0).getTransaction()) {
					result = new LinkedList<OperationDescription>(this.transactionsBackup.get(i));
					return result;
				}
			}
		}

		while (!stack.isEmpty()) {
			// get the already done operations in reverse order and in aborted
			// mode
			OperationDescription operation = stack.pop();
			result.add(new OperationDescription(operation.getTransaction(), operation.getOperation(), true));
		}
		return result;
	}

}
