package dbProject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dbProject.model.*;

public class Lock {
	private int readLock;
	private Boolean writeLock;
	private List<Integer> holders;
	private List<Waiter> waiters;

	public Lock() {
		this.readLock = 0;
		this.writeLock = false;
		this.holders = new LinkedList<>();
		this.waiters = new LinkedList<>();
	}

	public void lockRead(int transactionIndex) throws LockException {
		if (this.holders.contains(transactionIndex)) {
			// return true;
			return;
		}
		// if transaction is in waiters then don't need to insert it again
		for (int i = 0; i < this.waiters.size(); i++) {
			if (this.waiters.get(i).getIndex() == transactionIndex && this.waiters.get(i).getType() == LockType.READ) {
				// return false;
				throw new LockException();
			}
		}
		if (this.writeLock || !this.waiters.isEmpty()) {
			this.waiters.add(new Waiter(transactionIndex, LockType.READ));
			// return false;
			throw new LockException();
		}
		this.readLock++;
		this.holders.add(transactionIndex);
		// return true;
		return;
	}

	public void unlockRead(int transactionIndex) {
		if (this.writeLock) {
			return;
		}
		if (!this.holders.contains(transactionIndex)) {
			return;
		}
		this.readLock--;
		for (int i = 0; i < this.holders.size(); i++) {
			if (this.holders.get(i) == transactionIndex) {
				this.holders.remove(i);
			}
		}

	}

	public void lockWrite(int transactionIndex) throws LockException {
		// this part is for letting a transaction get write key if it has only
		// read key (and it is the only transaction trying to lock this key).
		// For example, a transaction searches for key (acquire read key), then
		// deletes it (trying to acquire write key).
		if (this.readLock == 1 && this.holders.contains(transactionIndex) && this.waiters.isEmpty()) {
			unlockRead(transactionIndex);
			lockWrite(transactionIndex);
		}

		if (this.writeLock && this.holders.contains(transactionIndex)) {
			// return true;
			return;
		}
		// if transaction is in waiters then don't need to insert it again
		for (int i = 0; i < this.waiters.size(); i++) {
			if (this.waiters.get(i).getIndex() == transactionIndex && this.waiters.get(i).getType() == LockType.WRITE) {
				// return false;
				throw new LockException();
			}
		}
		if (this.writeLock || this.readLock != 0 || !this.holders.isEmpty() || !this.waiters.isEmpty()) {
			this.waiters.add(new Waiter(transactionIndex, LockType.WRITE));
			// return false;
			throw new LockException();
		}
		this.writeLock = true;
		this.holders.add(transactionIndex);
		// return true;
		return;
	}

	public void unlockWrite(int transactionIndex) {
		if (this.readLock != 0 || !this.holders.contains(transactionIndex)) {
			return;
		}
		this.writeLock = false;
		for (int i = 0; i < this.holders.size(); i++) {
			if (this.holders.get(i) == transactionIndex) {
				this.holders.remove(i);
			}
		}
		// now the holders list should be empty
	}

	public Boolean inUse() {
		if (this.writeLock || this.readLock != 0 || !this.waiters.isEmpty()) {
			return true;
		}
		return false;
	}

	public Set<Integer> awakeTransactions() {
		Set<Integer> transactions = new HashSet<Integer>();
		// if there are still holders or no waiters then there are no
		// transactions to awake
		if (!this.holders.isEmpty() || this.waiters.isEmpty()) {
			return transactions;
		}

		Boolean writerFound = false;

		Waiter waiter = this.waiters.get(0);
		if (waiter.getType() == LockType.WRITE) {
			writerFound = true;
		}
		// anyway we add the first writer, and this will be the only transaction
		// to hold the lock
		this.holders.add(waiter.getIndex());
		transactions.add(waiter.getIndex());
		this.waiters.remove(0);
		waiter = this.waiters.get(0);

		// for the rest of the transactions, we awake them only if they are read
		// (and the first transaction was read too), and stop at the first write
		// transaction, or if there are no waiters left
		while (waiter != null && !writerFound) {
			if (waiter.getType() == LockType.WRITE) {
				writerFound = true;
			} else {
				this.holders.add(waiter.getIndex());
				transactions.add(waiter.getIndex());
				this.waiters.remove(0);
				waiter = this.waiters.get(0);
			}
		}
		return transactions;
	}

	public void unlockEverything(int transactionIndex) {
		this.unlockRead(transactionIndex);
		this.unlockWrite(transactionIndex);

		// delete from waiters list
		for (int i = 0; i < this.waiters.size(); i++) {
			if (this.waiters.get(i).getIndex() == transactionIndex) {
				this.waiters.remove(i);
			}
		}
	}

	public List<Map<String, Integer>> getEdgesForFirstWaiter() {
		if (this.waiters.isEmpty() || this.holders.isEmpty()) {
			return null;
		}
		List<Map<String, Integer>> edges = new LinkedList<Map<String, Integer>>();
		int waitingTransaction = this.waiters.get(0).getIndex();
		for (int i = 0; i < this.holders.size(); i++) {
			Map<String, Integer> edge = new HashMap<String, Integer>();
			edge.put("from", waitingTransaction);
			edge.put("to", this.holders.get(i).intValue());
			edges.add(edge);
		}
		return edges;
	}
}
