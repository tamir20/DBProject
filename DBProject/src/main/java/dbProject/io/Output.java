package dbProject.io;

import dbProject.BPlusTree;
import dbProject.model.Order;

/**
 * This class handles the application's output to the LOG file
 */
public interface Output {
    void writeAction(int transactionId, int actionId, int transactionRunCount);

    void writeWait(int transactionId, int actionId, int transactionRunCount);

    void writeTransactionRestart(int transactionId, int transactionRunCount);

    void writeFreeText(String s);

    void finish(BPlusTree tree, Order order);
}
