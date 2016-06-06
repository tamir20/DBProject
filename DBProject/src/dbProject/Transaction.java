package dbProject;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private int id;

    private int runCount;

    private List<Operation> operationList;

    public Transaction(int id) {
        this.id = id;
        operationList = new ArrayList<>();
    }

    public Operation get(int i){
        return operationList.get(i);
    }

    public int size(){
        return operationList.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public List<Operation> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<Operation> operationList) {
        this.operationList = operationList;
    }

    public void add(Operation operation) {
        operationList.add(operation);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "runCount=" + runCount +
                ", id=" + id +
                ", operationList=" + operationList +
                "}\n";
    }
}
