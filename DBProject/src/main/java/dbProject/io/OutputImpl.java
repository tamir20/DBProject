package dbProject.io;

import dbProject.BPlusTree;
import dbProject.Order;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by omar on 6/6/16.
 */
public class OutputImpl implements Output {


    public static final String TRANSACTION_STRING = "Transaction %d action %d";
    public static final String TRANSACTION_WITH_RUNCOUNT_STRING = "Transaction %d(%d) action %d";

    public static final String TRANSACTION_WAIT_STRING = "Transaction %d action %d WAITING";
    public static final String TRANSACTION_WITH_RUNCOUNT_WAIT_STRING = "Transaction %d(%d) action %d WAITING";

    public static final String TRANSACTION_ABORT_STRING= "Transaction %d restart";

    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter out;

    public OutputImpl() {

        try {
            this.fw = new FileWriter("LOG.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.bw = new BufferedWriter(fw);
        this.out = new PrintWriter(bw);
    }

    @Override
    public void writeAction(int transactionId, int actionId, int transactionRunCount) {
        String str;

        if (transactionRunCount > 1){
            str = String.format(TRANSACTION_WITH_RUNCOUNT_STRING, transactionId, transactionRunCount, actionId);
        } else {
            str = String.format(TRANSACTION_STRING, transactionId, actionId);
        }
        System.out.println(str);
        out.println(str);

    }

    @Override
    public void writeWait(int transactionId, int actionId, int transactionRunCount) {

        String str;

        if (transactionRunCount > 1){
            str = String.format(TRANSACTION_WITH_RUNCOUNT_WAIT_STRING, transactionId, transactionRunCount, actionId);
        } else {
            str = String.format(TRANSACTION_WAIT_STRING, transactionId, actionId);
        }
        System.out.println(str);
        out.println(str);
    }

    @Override
    public void writeTransactionRestart(int transactionId, int transactionRunCount) {
        String str = String.format(TRANSACTION_ABORT_STRING, transactionId);
        System.out.println(str);
        out.println(str);
    }

    @Override
    public void finish(BPlusTree tree, Order order) {
        System.out.println(tree);
        out.println(tree);
        System.out.println(order);
        out.println(order);
        out.flush();

    }

    @Override
    public void writeFreeText(String s) {
        System.out.println(s);
        out.println(s);
    }
}
