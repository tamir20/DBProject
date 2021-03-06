package dbProject;

import dbProject.model.Graph;
import dbProject.model.LockException;
import dbProject.model.LockKey;
import dbProject.model.LockPage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class LockManager {

	private List<LockKey> keyList;
	private List<LockPage> pageList;

	// for pseudo random scheduler type, need to get the random variable (with
	// seed) from the constructor
	private Random random;

	public LockManager(Random random) {
		this.keyList = new LinkedList<LockKey>();
		this.pageList = new LinkedList<LockPage>();
		this.random = random;
	}

	public void lockPageRead(Object page, int transactionIndex) throws LockException {
		// Boolean success;
		for (int i = 0; i < this.pageList.size(); i++) {
			if (this.pageList.get(i).getPage() == page) {
				// success = this.pageList.get(i).lockRead(transactionIndex);
				// return success;
				this.pageList.get(i).lockRead(transactionIndex);
				return;
			}
		}
		LockPage lock = new LockPage(page);
		// // if we are here then success should be always true
		// success = lock.lockRead(transactionIndex);
		lock.lockRead(transactionIndex);
		this.pageList.add(lock);
		// return success;
	}

	public void unlockPageRead(Object page, int transactionIndex) {
		for (int i = 0; i < this.pageList.size(); i++) {
			if (this.pageList.get(i).getPage() == page) {
				this.pageList.get(i).unlockRead(transactionIndex);
				if (!this.pageList.get(i).inUse()) {
					this.pageList.remove(i);
				}
			}
		}
	}

	public void lockKeyRead(int key, int transactionIndex) throws LockException {
		// Boolean success;
		for (int i = 0; i < this.keyList.size(); i++) {
			if (this.keyList.get(i).getKey() == key) {
				// success = this.keyList.get(i).lockRead(transactionIndex);
				// return success;
				this.keyList.get(i).lockRead(transactionIndex);
				return;
			}
		}
		LockKey lock = new LockKey(key);
		// // if we are here then success should be always true
		// success = lock.lockRead(transactionIndex);
		lock.lockRead(transactionIndex);
		this.keyList.add(lock);
		// return success;
		return;
	}

	public void unlockKeyRead(int key, int transactionIndex) {
		for (int i = 0; i < this.keyList.size(); i++) {
			if (this.keyList.get(i).getKey() == key) {
				this.keyList.get(i).unlockRead(transactionIndex);
				if (!this.keyList.get(i).inUse()) {
					this.keyList.remove(i);
				}
			}
		}
	}

	public void unlockEverything(int transactionIndex) {
		for (Iterator<LockKey> iter = keyList.iterator(); iter.hasNext();) {
			LockKey lock = iter.next();
			lock.unlockEverything(transactionIndex);
			if (!lock.inUse()) {
				iter.remove();
			}
		}
		for (Iterator<LockPage> iter = pageList.iterator(); iter.hasNext();) {
			LockPage lock = iter.next();
			lock.unlockEverything(transactionIndex);
			if (!lock.inUse()) {
				iter.remove();
			}
		}
	}
	
	public void unlockAllPages(int transactionIndex) {
		for (Iterator<LockPage> iter = pageList.iterator(); iter.hasNext();) {
			LockPage lock = iter.next();
			lock.unlockEverything(transactionIndex);
			if (!lock.inUse()) {
				iter.remove();
			}
		}
	}

	public void lockPageWrite(Object page, int transactionIndex) throws LockException {
		// Boolean success;
		for (int i = 0; i < this.pageList.size(); i++) {
			if (this.pageList.get(i).getPage() == page) {
				// success = this.pageList.get(i).lockWrite(transactionIndex);
				// return success;
				this.pageList.get(i).lockWrite(transactionIndex);
				return;
			}
		}
		LockPage lock = new LockPage(page);
		// // if we are here then success should be always true
		// success = lock.lockWrite(transactionIndex);
		lock.lockWrite(transactionIndex);
		this.pageList.add(lock);
		// return success;
		return;
	}

	public void unlockPageWrite(Object page, int transactionIndex) {
		for (int i = 0; i < this.pageList.size(); i++) {
			if (this.pageList.get(i).getPage() == page) {
				this.pageList.get(i).unlockWrite(transactionIndex);
				if (!this.pageList.get(i).inUse()) {
					this.pageList.remove(i);
				}
			}
		}
	}

	public void lockKeyWrite(int key, int transactionIndex) throws LockException {
		// Boolean success;
		for (int i = 0; i < this.keyList.size(); i++) {
			if (this.keyList.get(i).getKey() == key) {
				// success = this.keyList.get(i).lockWrite(transactionIndex);
				// return success;
				this.keyList.get(i).lockWrite(transactionIndex);
				return;
			}
		}
		LockKey lock = new LockKey(key);
		// // if we are here then success should be always true
		// success = lock.lockWrite(transactionIndex);
		lock.lockWrite(transactionIndex);
		this.keyList.add(lock);
		// return success;
		return;
	}

	public void unlockKeyWrite(int key, int transactionIndex) {
		for (int i = 0; i < this.keyList.size(); i++) {
			if (this.keyList.get(i).getKey() == key) {
				this.keyList.get(i).unlockWrite(transactionIndex);
				if (!this.keyList.get(i).inUse()) {
					this.keyList.remove(i);
				}
			}
		}
	}

	public Set<Integer> awakeTransactions() {
		Set<Integer> transactions = new HashSet<Integer>();
		for (int i = 0; i < this.pageList.size(); i++) {
			transactions.addAll(pageList.get(i).awakeTransactions());
		}
		for (int i = 0; i < this.keyList.size(); i++) {
			transactions.addAll(keyList.get(i).awakeTransactions());
		}
		return transactions;
	}

	public int recommendAbort(Set<Integer> aborting) {
		// find cycle in the lock graph and return one of its transactions

		Graph g = new Graph(this.random);
		for (int i = 0; i < this.keyList.size(); i++) {
			List<Map<String, Integer>> edges = this.keyList.get(i).getEdgesForAllWaiters();
			if (edges != null) {
				for (int j = 0; j < edges.size(); j++) {
					int from = edges.get(j).get("from");
					int to = edges.get(j).get("to");
					if(from!=to){
					if (!aborting.contains(from) && !aborting.contains(to)) {
						g.addEdge(from, to);
					}}
				}
			}
		}
		for (int i = 0; i < this.pageList.size(); i++) {
			List<Map<String, Integer>> edges = this.pageList.get(i).getEdgesForAllWaiters();
			if (edges != null) {
				for (int j = 0; j < edges.size(); j++) {
					int from = edges.get(j).get("from");
					int to = edges.get(j).get("to");
					if (!aborting.contains(from) && !aborting.contains(to)) {
						g.addEdge(from, to);
					}
				}
			}
		}
		return g.findRandomTransactionInCycle();
	}
	
	@Override
	public String toString() {
		return this.keyList.toString() + " , " + this.pageList.toString() + "}";
	}

}
