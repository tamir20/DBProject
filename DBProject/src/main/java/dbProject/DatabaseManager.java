package dbProject;

import dbProject.io.Output;
import dbProject.io.OutputImpl;
import dbProject.io.Parser;
import dbProject.io.ParserImpl;
import dbProject.model.*;

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

	private Map<String, Integer> variables;

	private Map<Integer, Integer> runCount;

	private Order order;

	public DatabaseManager() {
		// init everything
		this.parser = new ParserImpl();
		this.lockManager = new LockManager(new Random(0));
		this.tree = new BPlusTree(NODE_SIZE, this.lockManager);
		this.disk = new Disk();
		this.deletedRidLists = new HashMap<Integer, Set<Integer>>();
		this.variables = new HashMap<String, Integer>();
		this.runCount = new HashMap<Integer, Integer>();
		this.order = new Order();
		this.output = new OutputImpl();
	}

	public void setSeed(long seed) {

	}

	public void run() {

		ParsedCommands parsedCommands = parser.parse();

		List<Transaction> transactionList = parsedCommands.getTransactions();

		for (int i = 0; i < transactionList.size(); i++) {
			this.deletedRidLists.put(transactionList.get(i).getId(), new HashSet<Integer>());
		}

		System.out.println(transactionList);

		initialize(parsedCommands, transactionList);

		while (scheduler.hasNext()) {
			OperationDescription od = scheduler.next();
			int transactionIndex = od.getTransaction();
			this.output.writeAction(transactionIndex, od.getOperation(), this.runCount.get(transactionIndex));
			this.order.add(od.getTransaction() + "-" + od.getOperation());
//			System.out.print("run number " + this.runCount.get(transactionIndex) + " of transaction " + transactionIndex
//					+ " op " + od.getOperation() + ": ");
			Command cmd;
			if (od.isAborted() == false) {
				if (od.getOperation() != -1) {
					cmd = getTransactionByID(transactionList, od.getTransaction()).getByID(od.getOperation())
							.getCommand();
					List<String> args = getTransactionByID(transactionList, od.getTransaction())
							.getByID(od.getOperation()).getArgs();
					if (cmd == Command.ALLOCATE_RECORD) {
						int rid = this.disk.allocateRecord(Integer.parseInt(removeSpaces(args.get(0))),
								removeSpaces(args.get(1)), removeSpaces(args.get(2)));
						String variable = removeSpaces(args.get(3));
						this.variables.put(variable, rid);
						System.out.println("allocated record with rid = " + rid);
					}
					if (cmd == Command.INSERT) {
						int key = Integer.parseInt(removeSpaces(args.get(0)));
						int variableValue = this.variables.get(removeSpaces(args.get(1)));
						try {
							this.lockManager.lockKeyWrite(key, transactionIndex);
							int rid = tree.getValue(key, transactionIndex);
							if (rid < 0) {
								this.tree.insertData(key, variableValue, transactionIndex);
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
							this.output.writeWait(transactionIndex, od.getOperation(), this.runCount.get(transactionIndex));
						}
					}
					if (cmd == Command.DELETE) {
						int key = Integer.parseInt(removeSpaces(args.get(0)));
						String variable = removeSpaces(args.get(1));
						try {
							this.lockManager.lockKeyWrite(key, transactionIndex);
							int rid = tree.getValue(key, transactionIndex);
							if (rid >= 0) {
								int ridDeleted = this.tree.removeData(key, transactionIndex);
								this.variables.put(variable, ridDeleted);
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
							this.output.writeWait(transactionIndex, od.getOperation(), this.runCount.get(transactionIndex));
						}
					}
					if (cmd == Command.SEARCH) {
						int key = Integer.parseInt(removeSpaces(args.get(0)));
						String variable = removeSpaces(args.get(1));
						Boolean successfullSearch = true;
						int rid = -1;
						try {
							rid = this.tree.getValue(key, transactionIndex);
						} catch (LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting while SEARCH");
							this.output.writeWait(transactionIndex, od.getOperation(), this.runCount.get(transactionIndex));
							successfullSearch = false;
						}
						if (successfullSearch) {
							if (rid < 0) {
								System.out.println("no key found, returning 0");
							} else {
								Record record = this.disk.readRecord(rid);
								if (record != null) {
									System.out.println("v1: " + record.getV1() + ", v2: " + record.getV2());
									this.variables.put(variable, record.getK());
								} else {
									System.out.println("the rid retrieved from the tree cant be found on the disk");
								}
							}
						}
					}
					if (cmd == Command.RANGE_SEARCH) {
						Boolean successfullRangeSearch = true;
						int min = Integer.parseInt(removeSpaces(args.get(0)));
						int max = Integer.parseInt(removeSpaces(args.get(1)));
						int maxStrings = Integer.parseInt(removeSpaces(args.get(2)));
						List<Integer> rids = null;
						try {
							rids = this.tree.range_search(min, max, transactionIndex);
						} catch (LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting while RANGE SEARCH");
							this.output.writeWait(transactionIndex, od.getOperation(), this.runCount.get(transactionIndex));
							successfullRangeSearch = false;
						}
						if (successfullRangeSearch) {
							if (rids == null) {
								System.out.println("");
							} else {
								String result = "";
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
				cmd = getTransactionByID(transactionList, od.getTransaction()).getByID(od.getOperation()).getCommand();
				List<String> args = getTransactionByID(transactionList, od.getTransaction()).getByID(od.getOperation())
						.getArgs();
				if (cmd == Command.ALLOCATE_RECORD) {
					this.disk.freeRecord(Integer.parseInt(removeSpaces(args.get(0))));
					System.out.println("freed record with rid = " + Integer.parseInt(removeSpaces(args.get(0))));
				}
				if (cmd == Command.INSERT) {
					int key = Integer.parseInt(removeSpaces(args.get(0)));
					try {
						this.tree.removeData(key, transactionIndex);
						System.out.println("deleted successfully while aborting");
					} catch (NumberFormatException | LockException e) {
						this.scheduler.sleepTransaction(transactionIndex);
						System.out.println(
								"transaction " + transactionIndex + " has failed to delete while aborting. BUG");
					}
				}
				if (cmd == Command.DELETE) {
					int key = Integer.parseInt(removeSpaces(args.get(0)));
					int ridDeleted = this.variables.get(removeSpaces(args.get(1)));
					try {
						this.tree.insertData(key, ridDeleted, transactionIndex);
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
				this.runCount.put(transactionIndex, runCount.get(transactionIndex) + 1);
				this.output.writeTransactionRestart(transactionIndex, this.runCount.get(transactionIndex));
			}

			// awake transactions freed by the last transaction
			this.scheduler.awakeTransactions(this.lockManager.awakeTransactions());

			// check for deadlocks
			if (this.lockManager.recommendAbort(this.scheduler.getAbortingTransactions()) != -1) {
				int abortedTransaction = this.lockManager.recommendAbort(this.scheduler.getAbortingTransactions());
				this.scheduler.abortTransaction(abortedTransaction);
				System.out.println("aborted transaction " + abortedTransaction + " due to deadlock");
			}
			System.out.println();
		}
		this.output.finish(this.tree, this.order);
		System.out.println("order of execution:");
		System.out.println(this.order.toString());
	}

	private Transaction getTransactionByID(List<Transaction> transactionList, int transaction) {
		for (int i = 0; i < transactionList.size(); i++) {
			if (transactionList.get(i).getId() == transaction) {
				return transactionList.get(i);
			}
		}
		return null;
	}

	String removeSpaces(String st) {
		return st.replaceAll("\\s+", "");
	}

	private void initialize(ParsedCommands parsedCommands, List<Transaction> transactionList) {
		scheduler = new Scheduler(transactionList);
		scheduler.setSchedulerType(parsedCommands.getSchedulerType());
		scheduler.setSeed(parsedCommands.getSeed());
		lockManager = new LockManager(new Random(parsedCommands.getSeed()));
		this.tree = new BPlusTree(NODE_SIZE, this.lockManager);

		// initialize the runCount
		for (int i = 0; i < transactionList.size(); i++) {
			this.runCount.put(transactionList.get(i).getId(), 1);
		}
	}
}
