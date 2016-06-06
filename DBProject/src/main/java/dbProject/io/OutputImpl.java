package dbProject.io;

/**
 * Created by omar on 6/6/16.
 */
public class OutputImpl implements Output {


    public static final String TRANSACTION_STRING = "Transaction %d action %d";
    public static final String TRANSACTION_WITH_RUNCOUNT_STRING = "Transaction %d(%d) action %d";

    public static final String TRANSACTION_WAIT_STRING = "Transaction %d action %d WAITING";
    public static final String TRANSACTION_WITH_RUNCOUNT_WAIT_STRING = "Transaction %d(%d) action %d WAITING";

    public static final String TRANSACTION_ABORT_STRING= "Transaction %d restart";

    @Override
    public void writeAction(int transactionId, int actionId, int transactionRunCount) {
        String str;

        if (transactionRunCount > 1){
            str = String.format(TRANSACTION_WITH_RUNCOUNT_STRING, transactionId, transactionRunCount, actionId);
        } else {
            str = String.format(TRANSACTION_STRING, transactionId, actionId);
        }
        System.out.println(str);

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

    }

    @Override
    public void writeTransactionRestart(int transactionId, int transactionRunCount) {
        System.out.println(String.format(TRANSACTION_ABORT_STRING, transactionId));
    }

    @Override
    public void finish(Object bTree, Object order) {
        System.out.println("FINISHED");
    }
}
