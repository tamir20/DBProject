package dbProject.model;

public class Node {
	protected Node father;
	protected int key;

	public void setParent(Node parent) {
		this.father = parent;
	}

	public int getKey() {
		return key;
	}

    public Node getFather() {
        return father;
    }

    public boolean isLeaf(){
        return false;
    }

    public void setKey(int key) {
        this.key = key;
    }
    
}