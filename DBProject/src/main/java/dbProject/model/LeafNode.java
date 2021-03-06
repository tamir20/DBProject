package dbProject.model;

import java.util.ArrayList;
import java.util.List;

public class LeafNode extends Node {
	private List<Data> entries;
	private LeafNode prev;
	private LeafNode next;

	public LeafNode(int key) {
		this.key = key;
		this.father = null;
		this.entries = new ArrayList<Data>();
		this.prev = null;
		this.next = null;
	}

	public int getSize() {
		return entries.size();
	}

	public List<Data> getEntries() {
		return entries;
	}

	public boolean isLeaf() {
		return true;
	}

	public LeafNode getNext() {
		LeafNode next = this.next;
		while (next != null && next.getSize() == 0) {
			next = next.next;
		}
		return next;
	}

	public void setNext(LeafNode next) {
		this.next = next;
	}

	public LeafNode getPrev() {
		LeafNode prev = this.prev;
		while (prev != null && prev.getSize() == 0) {
			prev = prev.prev;
		}
		return prev;
	}

	public void setPrev(LeafNode prev) {
		this.prev = prev;
	}

	@Override
	public String toString() {
		String result = "{";
		for (int i = 0; i < this.entries.size(); i++) {
			result += this.entries.get(i).toString();
			if (i != this.entries.size() - 1) {
				result += " ";
			}
		}
		result += "}";
		return result;
	}

}
