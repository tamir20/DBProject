package dbProject.model;

import java.util.List;

public class ParsedCommands {

    List<Transaction> transactions;

    SchedulerType schedulerType;

    long seed;

    public ParsedCommands(List<Transaction> transactions, SchedulerType schedulerType, long seed) {
        this.transactions = transactions;
        this.schedulerType = schedulerType;
        this.seed = seed;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public SchedulerType getSchedulerType() {
        return schedulerType;
    }

    public void setSchedulerType(SchedulerType schedulerType) {
        this.schedulerType = schedulerType;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public String toString() {
        return "ParsedCommands{" +
                "transactions=" + transactions +
                ", schedulerType=" + schedulerType +
                ", seed=" + seed +
                '}';
    }
}
