package dbProject;

import java.util.ArrayList;
import java.util.List;

class TreeNode extends Node {
	private List<Node> sons;

	public TreeNode(int key) {
		this.key = key;
		this.father = null;
		this.sons = new ArrayList<Node>();
	}

	public int getSize() {
		return sons.size();
	}

	public List<Node> getSons() {
		return sons;
	}

	public boolean isLeaf() {
		return false;
	}

}
