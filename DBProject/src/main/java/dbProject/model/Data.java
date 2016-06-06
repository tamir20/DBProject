package dbProject.model;

public class Data {
	private int key;
	private int rID;

	public Data(int key, int rID) {
		this.key = key;
		this.rID = rID;
	}

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getrID() {
        return rID;
    }

    public void setrID(int rID) {
        this.rID = rID;
    }
}
