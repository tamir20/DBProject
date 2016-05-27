package main;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dbProject.Disk;
import dbProject.LockException;
import dbProject.LockerManager;
import dbProject.Operation;
import dbProject.OperationDescription;
import dbProject.Record;
import dbProject.Scheduler;
import dbProject.Scheduler.SchedulerType;

public class Main {

	public static void main(String[] args) {

		List<List<Operation>> list = new LinkedList<List<Operation>>();

		list.add(new LinkedList<Operation>());
		list.add(new LinkedList<Operation>());
		list.add(new LinkedList<Operation>());

		list.get(0).add(new Operation());
		list.get(0).add(new Operation());
		list.get(1).add(new Operation());
		list.get(1).add(new Operation());
		list.get(1).add(new Operation());
		list.get(1).add(new Operation());
		list.get(2).add(new Operation());
		list.get(2).add(new Operation());
		list.get(2).add(new Operation());
		list.get(2).add(new Operation());
		list.get(2).add(new Operation());

		Scheduler sced = new Scheduler(list);
		sced.setSchedulerType(SchedulerType.SERIAL);
		int i = 0;
		Set<Integer> awake = new HashSet<Integer>();
		awake.add(0);

		while (sced.hasNext()) {
			OperationDescription od = sced.next();
			System.out.println(od.getTransaction() + " - " + od.getOperation());
			if (i == 4) {
				sced.abortTransaction(1);
			}
			i++;
		}

		// Object ob = new Object();
		// LockerManager locker = new LockerManager();
		// try {
		// locker.lockPageRead(ob, 2);
		// //locker.lockPageWrite(ob, 3);
		// locker.lockPageRead(ob, 9);
		// locker.unlockPageRead(ob, 2);
		// locker.unlockPageRead(ob, 9);
		// locker.lockPageWrite(ob, 9);
		// locker.lockPageWrite(ob, 9);
		// //locker.lockPageRead(ob, 8);
		// } catch (LockException e) {
		// e.printStackTrace();
		// }
	}

}
