package main;

import dbProject.LockException;
import dbProject.LockManager;

public class Main {

	public static void main(String[] args) {

		// List<List<Operation>> list = new LinkedList<List<Operation>>();
		//
		// list.add(new LinkedList<Operation>());
		// list.add(new LinkedList<Operation>());
		// list.add(new LinkedList<Operation>());
		//
		// list.get(0).add(new Operation());
		// list.get(0).add(new Operation());
		// list.get(1).add(new Operation());
		// list.get(1).add(new Operation());
		// list.get(1).add(new Operation());
		// list.get(1).add(new Operation());
		// list.get(2).add(new Operation());
		// list.get(2).add(new Operation());
		// list.get(2).add(new Operation());
		// list.get(2).add(new Operation());
		// list.get(2).add(new Operation());
		//
		// Scheduler sced = new Scheduler(list);
		// sced.setSchedulerType(SchedulerType.SERIAL);
		// int i = 0;
		// Set<Integer> awake = new HashSet<Integer>();
		// awake.add(0);
		//
		// while (sced.hasNext()) {
		// OperationDescription od = sced.next();
		// System.out.println(od.getTransaction() + " - " + od.getOperation());
		// if (i == 4) {
		// sced.abortTransaction(1);
		// }
		// i++;
		// }

		Object ob = new Object();
		LockManager locker = new LockManager();
		try {
			locker.lockPageRead(ob, 2);
			locker.lockPageWrite(ob, 3);
			locker.lockPageRead(ob, 9);
			locker.unlockPageRead(ob, 2);
			locker.unlockPageRead(ob, 9);
			locker.lockPageWrite(ob, 9);
			locker.lockPageWrite(ob, 9);
			// locker.lockPageRead(ob, 8);
		} catch (LockException e) {
			// e.printStackTrace();
		}
		try {
			locker.lockKeyRead(5, 3);
			locker.lockKeyRead(5, 5);
			locker.lockKeyRead(5, 6);
			locker.lockKeyRead(5, 7);
			locker.lockKeyWrite(5, 4);

		} catch (LockException e) {
			// e.printStackTrace();
		}
		try {
			locker.lockKeyWrite(6, 4);
			locker.lockKeyWrite(6, 2);

		} catch (LockException e) {
			// e.printStackTrace();
		}
		System.out.println(locker.recommendAbort());

		// Graph g = new Graph();
		// g.addEdge(1, 2);
		// g.addEdge(1, 2);
		// g.addEdge(2, 3);
		// g.addEdge(3, 1);
		// System.out.println(g.findTransactionInCycle());
	}

}
