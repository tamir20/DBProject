package dbProject;

abstract class Node {
	protected Node father;
	protected int key;

	public void setParent(Node parent) {
		this.father = parent;
	}

	public int getKey() {
		return key;
	}

	abstract boolean isLeaf();
}