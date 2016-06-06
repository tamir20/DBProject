package dbProject;

import dbProject.io.Logger;
import dbProject.io.Parser;
import dbProject.io.ParserImpl;
import dbProject.model.Command;
import dbProject.model.Record;
import dbProject.model.Transaction;

import java.util.List;

public class DatabaseManager {

	private final int NODE_SIZE = 5;

	private Parser parser;

	private Logger logger;

	private Scheduler scheduler;

	private LockManager lockManager;

	private BPlusTree tree;

	private Disk disk;

	public DatabaseManager() {
		// init everything
		this.parser = new ParserImpl();
		this.lockManager = new LockManager();
		this.tree = new BPlusTree(NODE_SIZE, this.lockManager);
		this.disk = new Disk();
	}

	public void run() {

		List<Transaction> transactionList = parser.parse();

		System.out.println(transactionList);

		scheduler = new Scheduler(transactionList);

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
						System.out.println("alocatet record with rid = " + rid);
					}
					if (cmd == Command.INSERT) {
						// TODO: change rid = 1 to the rid extracted by the
						// parser, and key = 2 to the key extracted
						try {
							int rid = tree.getValue(2, transactionIndex);
							if (rid < 0) {
								this.tree.insertData(2, 1, transactionIndex);
								System.out.println("inserted successfully");
							} else {
								// TODO: add this line after the above TODO is
								// finished
								// this.scheduler.abortTransaction(transactionIndex);
								System.out.println("there is already a key like this in the tree. aborting");
							}
						} catch (NumberFormatException | LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting");
						}
					}
					if (cmd == Command.SEARCH) {
						int rid = -1;
						try {
							// TODO: change key = 2 to the key extracted by the
							// parser
							rid = this.tree.getValue(2, transactionIndex);
						} catch (LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting");
						}
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
					if(cmd == Command.DELETE){
						// TODO: change key = 2 to the rid extracted by the
						// parser
						try {
							int rid = tree.getValue(2, transactionIndex);
							if (rid >= 0) {
								this.tree.removeData(2, transactionIndex);
								System.out.println("deleted successfully");
							} else {
								// TODO: add this line after the above TODO is
								// finished
								// this.scheduler.abortTransaction(transactionIndex);
								System.out.println("the key is not in the tree. aborting");
							}
						} catch (NumberFormatException | LockException e) {
							this.scheduler.sleepTransaction(transactionIndex);
							System.out.println("transaction " + transactionIndex + " is waiting");
						}
					}
					if(cmd == Command.RANGE_SEARCH){
						//TODO: keep with range search
					}
				}
			}
			
			//TODO: keep with aborted methods

			if(od.getOperation() == -1){
				this.lockManager.unlockEverything(transactionIndex);
				//TODO: check if there are disk records to free and free them
			}
			
			// awake transactions freed by the last transaction
			this.scheduler.awakeTransactions(this.lockManager.awakeTransactions());

			// check for deadlocks
			if (this.lockManager.recommendAbort() != -1) {
				this.scheduler.abortTransaction(this.lockManager.recommendAbort());
			}
		}
	}
}
