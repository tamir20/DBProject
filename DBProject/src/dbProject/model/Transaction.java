package dbProject.model;

import java.util.List;

public class Transaction {

    private int id;

    private int runCount;

    private List<Operation> operationList;

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
}
