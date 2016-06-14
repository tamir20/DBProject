package dbProject;

import dbProject.io.Output;
import dbProject.io.Parser;
import dbProject.io.ParserImpl;
import dbProject.model.Command;
import dbProject.model.Record;
import dbProject.model.Transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DatabaseManager {

	private final int NODE_SIZE = 5;

	private Parser parser;

	private Output output;

	private Scheduler scheduler;

	private LockManager lockManager;

	private BPlusTree tree;

	private Disk disk;

	private Map<Integer, Set<Integer>> deletedRidLists;

	public DatabaseManager() {
		// init everything
		this.parser = new ParserImpl();
		this.lockManager = new LockManager(new Random(0));
		this.tree = new BPlusTree(NODE_SIZE, this.lockManager);
		this.disk = new Disk();
		this.deletedRidLists = new HashMap<Integer, Set<Integer>>();
	}

	public void setSeed(long seed) {
		this.lockManager = new LockManager(new Random(seed));
		this.scheduler.setSeed(seed);
	}

	public void run() {

		List<Transaction> transactionList = parser.parse();

		for (int i = 0; i < transactionList.size(); i++) {
			this.deletedRidLists.put(transactionList.get(i).getId(), new HashSet<Integer>());
		}

		System.out.println(transactionList);

		scheduler = new Scheduler(transactionList);

		// TODO: set seed for the database with "setSeed"
		scheduler.setSchedulerType(Scheduler.SchedulerType.SERIAL);

		while (scheduler.hasNext()) {
			OperationDescription od = scheduler.next();
			int transactionIndex = od.getTransaction();
			Command cmd;
			if (od.isAborted() == false) {
				if (od.getOperation() != -1) {
					cmd = transactionList.get(od.getTransaction()).getByID(od.getOperation()).getCommand();
					List<String> args = transactionList.get(od.getTransaction()).getByID(od.getOperation()).getArgs();
					if (cmd == Command.ALLOCATE_RECORD) {
						int rid = this.disk.allocateRecord(Integer.parseInt(args.get(0)), args.get(1), args.get(2));
						System.out.println("allocated record with rid = " + rid);
					}
					if (cmd == Command.INSERT) {
						// TODO: change rid = 1 to the rid extracted by the
						// parser, and key = 2 to the key extracted
						try {
							this.lockManager.lockKeyWrite(2, transactionIndex);
							int rid = tree.getValue(2, transactionIndex);
							if (rid < 0) {
								this.tree.insertData(2, 1, transactionIndex);
								System.out.println("inserted successfully");
							} else {
								this.scheduler.abortTransaction(transactionIndex);
								System.out.println("there is already a key like this in the tree. aborting");
								// watch that we must trust other transactions
								// to remove the key or we will never success
								// here
							}
						} catch (NumberFormatException | LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting while INSERT");
						}
					}
					if (cmd == Command.DELETE) {
						// TODO: change key = 2 to the rid extracted by the
						// parser
						try {
							this.lockManager.lockKeyWrite(2, transactionIndex);
							int rid = tree.getValue(2, transactionIndex);
							if (rid >= 0) {
								int ridDeleted = this.tree.removeData(2, transactionIndex);
								// TODO: store ridDeleted in the variable of the
								// delete command
								System.out.println("deleted successfully. rid deleted: " + ridDeleted);
								this.deletedRidLists.get(transactionIndex).add(ridDeleted);
							} else {
								this.scheduler.abortTransaction(transactionIndex);
								System.out.println("the key is not in the tree. aborting");
								// watch that we must trust other transactions
								// to insert the key or we will never success
								// here
							}
						} catch (NumberFormatException | LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting while DELETE");
						}
					}
					if (cmd == Command.SEARCH) {
						Boolean successfullSearch = true;
						int rid = -1;
						try {
							// TODO: change key = 2 to the key extracted by the
							// parser
							rid = this.tree.getValue(2, transactionIndex);
						} catch (LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting while SEARCH");
							successfullSearch = false;
						}
						if (successfullSearch) {
							if (rid < 0) {
								System.out.println("no key found, returning 0");
							} else {
								Record record = this.disk.readRecord(rid);
								if (record != null) {
									System.out.println("v1: " + record.getV1() + ", v2: " + record.getV2());
								} else {
									System.out.println("the rid retrieved from the tree cant be found on the disk");
								}
							}
						}
					}
					if (cmd == Command.RANGE_SEARCH) {
						Boolean successfullRangeSearch = true;
						int min = Integer.parseInt(args.get(0));
						int max = Integer.parseInt(args.get(1));
						List<Integer> rids = null;
						try {
							rids = this.tree.range_search(min, max, transactionIndex);
						} catch (LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting while RANGE SEARCH");
							successfullRangeSearch = false;
						}
						if (successfullRangeSearch) {
							if (rids == null) {
								System.out.println("");
							} else {
								String result = "";
								// TODO: change maxStrings to the maxStrings
								// extracted
								// by the parser
								int maxStrings = 2;
								if (maxStrings > rids.size()) {
									maxStrings = rids.size();
								}
								for (int i = 0; i < maxStrings; i++) {
									Record rec = this.disk.readRecord(rids.get(i));
									if (rec != null) {
										if (i != 0) {
											result += ", ";
										}
										result += rec.getV1() + " " + rec.getV2();
									}
								}
								System.out.println(result);
							}
						}
					}
				}
			} else {
				cmd = transactionList.get(od.getTransaction()).getByID(od.getOperation()).getCommand();
				List<String> args = transactionList.get(od.getTransaction()).getByID(od.getOperation()).getArgs();
				if (cmd == Command.ALLOCATE_RECORD) {
					this.disk.freeRecord(Integer.parseInt(args.get(0)));
					System.out.println("freed record with rid = " + Integer.parseInt(args.get(0)));
				}
				if (cmd == Command.INSERT) {
					// TODO: change rid = 1 to the rid extracted by the
					// parser, and key = 2 to the key extracted
					try {
						this.tree.removeData(2, transactionIndex);
						System.out.println("deleted successfully while aborting");
					} catch (NumberFormatException | LockException e) {
						this.scheduler.sleepTransaction(transactionIndex);
						System.out.println(
								"transaction " + transactionIndex + " has failed to delete while aborting. BUG");
					}
				}
				if (cmd == Command.DELETE) {
					// TODO: change key = 2 to the rid extracted by the
					// parser and change rid = 1 to the value in the variable of
					// the remove command
					try {
						this.tree.insertData(2, 1, transactionIndex);
						System.out.println("deleted successfully while aborting");
					} catch (NumberFormatException | LockException e) {
						this.scheduler.sleepTransaction(transactionIndex);
						System.out.println(
								"transaction " + transactionIndex + " has failed to insert while aborting. BUG");
					}
				}
			}

			if (od.getOperation() == -1) {
				this.lockManager.unlockEverything(transactionIndex);

				System.out.println("transaction " + transactionIndex + " is committing");

				// check if there are disk records to free and free them
				for (Iterator<Integer> iter = this.deletedRidLists.get(transactionIndex).iterator(); iter.hasNext();) {
					int ridToDelete = iter.next();
					this.disk.freeRecord(ridToDelete);
					System.out
							.println("freed rid " + ridToDelete + " while committing transaction " + transactionIndex);
				}
			}

			if (od.getOperation() == this.scheduler.getFirstOperation(transactionIndex) && od.isAborted()) {
				this.lockManager.unlockEverything(transactionIndex);
			}

			// awake transactions freed by the last transaction
			this.scheduler.awakeTransactions(this.lockManager.awakeTransactions());

			// check for deadlocks
			if (this.lockManager.recommendAbort(this.scheduler.getAbortingTransactions()) != -1) {
				int abortedTransaction = this.lockManager.recommendAbort(this.scheduler.getAbortingTransactions());
				this.scheduler.abortTransaction(abortedTransaction);
				System.out.println("aborted transaction " + abortedTransaction + " due to deadlock");
			}
		}
	}
}
