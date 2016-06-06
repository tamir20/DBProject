package dbProject.io;

/**
 * This class handles the application's output to the LOG file*/
public interface Output {

    void writeAction(int transactionId, int actionId, int transactionRunCount);

    void writeWait(int transactionId, int actionId, int transactionRunCount);

    void writeTransactionRestart(int transactionId, int transactionRunCount);

    void finish(Object bTree, Object order);
}
