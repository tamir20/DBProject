package dbProject.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode extends Node {
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

	@Override
	public String toString() {
		String result = "";
		result += "{";
		for (int i = 0; i < sons.size(); i++) {
			result += sons.get(i).toString();
		}
		result += "}";
		return result;
	}

}
