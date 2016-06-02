package main;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import dbProject.BPlusTree;
import dbProject.Disk;
import dbProject.Graph;
import dbProject.LockException;
import dbProject.LockManager;
import dbProject.OperationDescription;
import dbProject.Record;
import dbProject.Scheduler;
import dbProject.Scheduler.SchedulerType;

public class Main {

	public static void main(String[] args) {

		List<List<Object>> list = new LinkedList<List<Object>>();

		list.add(new LinkedList<Object>());
		list.add(new LinkedList<Object>());
		list.add(new LinkedList<Object>());

		list.get(0).add(new Object());
		list.get(0).add(new Object());
		list.get(1).add(new Object());
		list.get(1).add(new Object());
		list.get(1).add(new Object());
		list.get(1).add(new Object());
		list.get(2).add(new Object());
		list.get(2).add(new Object());
		list.get(2).add(new Object());
		list.get(2).add(new Object());
		list.get(2).add(new Object());

		Scheduler sced = new Scheduler(list);
		sced.setSchedulerType(SchedulerType.SERIAL);
		int i = 0;
		Set<Integer> awake = new HashSet<Integer>();
		awake.add(1);

		while (sced.hasNext()) {
			OperationDescription od = sced.next();
			System.out.println(od.getTransaction() + " - " + od.getOperation() + " - " + od.isAborted());
			if (i == 4) {
				// sced.abortTransaction(1);
				sced.sleepTransaction(1);
			}
			if (i == 5) {
				sced.awakeTransactions(awake);
			}
			i++;
		}

		// Object ob = new Object();
		// LockManager locker = new LockManager();
		// try {
		// locker.lockPageRead(ob, 2);
		// locker.lockPageWrite(ob, 3);
		// locker.lockPageRead(ob, 9);
		// locker.unlockPageRead(ob, 2);
		// locker.unlockPageRead(ob, 9);
		// locker.lockPageWrite(ob, 9);
		// locker.lockPageWrite(ob, 9);
		// // locker.lockPageRead(ob, 8);
		// } catch (LockException e) {
		// // e.printStackTrace();
		// }
		// try {
		// locker.lockKeyRead(5, 3);
		// locker.lockKeyRead(5, 5);
		// locker.lockKeyRead(5, 6);
		// locker.lockKeyRead(5, 7);
		// locker.lockKeyWrite(5, 4);
		//
		// } catch (LockException e) {
		// // e.printStackTrace();
		// }
		// try {
		// locker.lockKeyWrite(6, 4);
		// locker.lockKeyWrite(6, 2);
		//
		// } catch (LockException e) {
		// // e.printStackTrace();
		// }
		// System.out.println(locker.recommendAbort());

		// Graph g = new Graph();
		// g.addEdge(1, 2);
		// g.addEdge(1, 2);
		// g.addEdge(2, 3);
		// g.addEdge(3, 1);
		// System.out.println(g.findTransactionInCycle());

//		LockManager lock = new LockManager();
//		BPlusTree tree = new BPlusTree(3, lock);
//		try {
//			tree.insertData(1, 1, 1);
//			tree.insertData(2, 1, 1);
//			tree.insertData(3, 1, 1);
//			tree.insertData(4, 1, 1);
//			tree.insertData(5, 1, 1);
//			tree.insertData(6, 1, 1);
//			tree.insertData(7, 1, 1);
//			tree.insertData(8, 1, 1);
//			tree.insertData(9, 1, 1);
//			//lock.unlockEverything(1);
//			tree.insertData(10, 1, 1);
//			System.out.println(tree.range_search(2, 100, 1));
//			
//			//tree.removeData(3, 1);
//			//tree.removeData(4, 2);
//			//tree.removeData(5, 1);
//			//tree.removeData(6, 1);
//			//tree.removeData(7, 1);
//			//tree.removeData(8, 1);
//			//tree.removeData(9, 1);
//			
//		} catch (LockException e) {
//			System.out.println(e.getMessage());
//		}

//		Date date = new Date(1464812526 * (long) 1000);
//		Calendar cal1 = Calendar.getInstance();
//		cal1.setTime(date);
//		System.out.println(cal1.get(Calendar.DAY_OF_YEAR));
//		Format formatter = new SimpleDateFormat("MMM yy", Locale.ENGLISH);
//		System.out.println(formatter.format(date));

	}

}
