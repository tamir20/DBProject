package dbProject;

import dbProject.io.Logger;
import dbProject.io.Parser;
import dbProject.io.ParserImpl;
import dbProject.model.Operation;
import dbProject.model.Transaction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseManager {

    private Parser parser;

    private Logger logger;

    private Scheduler scheduler;

    private Object tree;

    private LockManager lockManager;

    public DatabaseManager() {
        //init everything
        parser = new ParserImpl();
    }

    public void run(){

        List<Transaction> transactionList = parser.parse();

        System.out.println(transactionList);

        //todo:tamir change this to use the transactionList
        List<List<Operation>> tempList = null;

        scheduler = new Scheduler(tempList);

        scheduler.setSchedulerType(Scheduler.SchedulerType.SERIAL);

        Set<Integer> awake = new HashSet<>();

        while (scheduler.hasNext()) {
            OperationDescription od = scheduler.next();
            System.out.println(od.getTransaction() + " - " + od.getOperation());
            //run operation
            /**
             * for example
             *try {
             * btreeManager.insert( k, rid )
             * } catch(LockException e){
             * scheduler.sleepTransaction(transactionId);
             * }
             */
        }
    }
}
