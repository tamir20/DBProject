package dbProject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class BPlusTree {
	private Node root;
	private int node_size;
	private LockManager lock;

	public BPlusTree(int node_size, LockManager lock) {
		LeafNode root = new LeafNode(0);
		this.node_size = node_size;
		this.root = root;
		this.lock = lock;

		// add -1 to the tree to be the lowest id in the tree for future locks
		data dat = new data(-1, -1);
		insertToLeaf((LeafNode) this.root, -1, dat);
	}

	public int getValue(int key, int transactionIndex) throws LockException {
		// return value by key.
		// @input: key number.
		// @output: relative ID, -1 if not found
		this.lock.lockKeyRead(key, transactionIndex);
		int res = -1;
		LeafNode leaf = getLeaf(key, transactionIndex);
		List<data> data_list = leaf.getEntries();
		for (Iterator<data> i = data_list.iterator(); i.hasNext();) {
			data it_data = i.next();
			if (it_data.key == key) {
				res = it_data.rID;
			}
		}
		this.lock.unlockPageRead(leaf, transactionIndex);
		return res;
	}

	public boolean insertData(int key, int rID, int transactionIndex) throws LockException {
		// enter data to the tree
		// @input: key number and relative ID.
		// @output: true if data added to the tree false if key already
		// occupied.
		LeafNode leaf = getLeaf(key, transactionIndex);
		this.lock.lockKeyRead(getKeyBefore(key, transactionIndex), transactionIndex);
		this.lock.lockKeyWrite(key, transactionIndex);
		data insert_data = new data(key, rID);
		boolean res = insertToLeaf(leaf, key, insert_data);
		if (leaf.getSize() > this.node_size) {
			LeafNode new_leaf = new LeafNode(0);
			this.lock.unlockPageRead(leaf, transactionIndex);
			this.lock.lockPageWrite(leaf, transactionIndex);
			this.lock.lockPageWrite(new_leaf, transactionIndex);
			splitLeaf(leaf, new_leaf);
			new_leaf.setParent(leaf.father);
			if (leaf.father == null) {
				newRoot(leaf, new_leaf);
			} else {
				Node parent = leaf.father;
				new_leaf.setParent(parent);
				insertToInner(parent, new_leaf);
			}
			TreeNode curr_node = (TreeNode) leaf.father;
			while (curr_node.getSize() > this.node_size) {
				TreeNode new_node = new TreeNode(0);
				splitInner(curr_node, new_node);
				if (curr_node.father == null) {
					newRoot(curr_node, new_node);
				} else {
					Node parent = curr_node.father;
					new_node.setParent(parent);
					insertToInner(parent, new_node);
				}
				curr_node = (TreeNode) curr_node.father;
			}
			this.lock.unlockPageWrite(leaf, transactionIndex);
			this.lock.unlockPageWrite(new_leaf, transactionIndex);
		}

		return res;
	}

	public boolean removeData(int key, int transactionIndex) throws LockException {
		// remove data from the tree
		// @input: key number
		// @output: true if data removed from the tree false if not in there.
		this.lock.lockKeyWrite(key, transactionIndex);
		LeafNode leaf = getLeaf(key, transactionIndex);
		for (ListIterator<data> i = leaf.getEntries().listIterator(); i.hasNext();) {
			data dat = i.next();
			if (dat.key == key) {
				i.previous();
				i.remove();
				this.lock.unlockPageRead(leaf, transactionIndex);
				return true;
			}
		}
		this.lock.unlockPageRead(leaf, transactionIndex);
		return false;
	}

	public List<Integer> range_search(int minKey, int maxKey, int transactionIndex) throws LockException {
		List<Integer> list = new LinkedList<Integer>();
		Next next = search(minKey, maxKey, transactionIndex);
		Next prevNext = null;
		if (next == null) {
			return list;
		}
		while (next != null) {
			list.add(next.key);
			prevNext = next;
			next = next(next.key, next.page, maxKey, transactionIndex);
		}
		this.lock.unlockPageRead(prevNext.page, transactionIndex);
		return list;
	}

	private LeafNode getLeaf(int key, int transactionIndex) throws LockException {
		// return the leaf to wich a key belongs
		// @input: key number
		// @output: a pointer to the requested leaf.
		this.lock.lockPageRead(this.root, transactionIndex);
		Node curr_node = this.root;
		while (!curr_node.isLeaf()) {
			List<Node> node_list = ((TreeNode) curr_node).getSons();
			Node last_node = null;
			for (Iterator<Node> i = node_list.iterator(); i.hasNext();) {
				Node it_node = i.next();
				if ((last_node == null || last_node.key < key) && it_node.key >= key) {
					this.lock.lockPageRead(it_node, transactionIndex);
					curr_node = it_node;
					this.lock.unlockPageRead(curr_node.father, transactionIndex);
				}
				last_node = it_node;
			}
			if (last_node.key <= key) {
				this.lock.lockPageRead(last_node, transactionIndex);
				curr_node = last_node;
				this.lock.unlockPageRead(curr_node.father, transactionIndex);
			}
		}
		return (LeafNode) curr_node;
	}

	private void newRoot(Node node, Node new_node) {
		// create a new root and attach two nodes to it.
		// @input: two nodes, the original, and the new one created
		TreeNode new_root = new TreeNode(new_node.getKey());
		List<Node> sons_list = new_root.getSons();
		node.setParent(new_root);
		new_node.setParent(new_root);
		sons_list.add(node);
		sons_list.add(new_node);
		this.root = new_root;
	}

	private boolean insertToLeaf(LeafNode leaf, int key, data insertion) {
		// insert data to given leaf
		// @input: leaf to insert, key number and relative ID.
		// @output: true if data added to the tree false if key already
		// occupied.
		boolean res = false;
		boolean inserted = false;
		List<data> leaf_list = leaf.getEntries();
		if (leaf_list.size() == 0) {
			leaf_list.add(insertion);
			leaf.key = key;
			res = true;
		} else {
			data last_leaf = null;
			for (ListIterator<data> i = leaf_list.listIterator(); i.hasNext() && inserted == false;) {
				data leaf_data = i.next();
				if (leaf_data.key == key) {
					inserted = true;
				} else if (leaf_data.key > key && (last_leaf == null || last_leaf.key < key)) {
					i.previous();
					i.add(insertion);
					inserted = true;
					res = true;
				}
				last_leaf = leaf_data;
			}
			if (!inserted) {
				leaf_list.add(leaf_list.size(), insertion);
				leaf.key = key;
				res = true;
			}
		}
		return res;
	}

	private void insertToInner(Node new_parent, Node new_son) {
		// insert a node to a given node
		// @input: parent node and son node.
		Node last_node = null;
		boolean inserted = false;
		List<Node> sons_list = ((TreeNode) new_parent).getSons();
		for (ListIterator<Node> i = sons_list.listIterator(); i.hasNext() && inserted == false;) {
			Node node = i.next();
			if (node.key >= new_son.getKey() && (last_node == null || last_node.key < new_son.getKey())) {
				i.previous();
				i.add(new_son);
				inserted = true;
			}
			last_node = node;
		}
		if (inserted == false) {
			sons_list.add(sons_list.size(), new_son);
			new_parent.key = new_son.getKey();
		}
	}

	private void splitLeaf(LeafNode original, LeafNode new_leaf) {
		// split data between full leaf and a n empty leaf
		// @input: full leaf, and new empty leaf
		List<data> orig_list = original.getEntries();
		List<data> new_list = new_leaf.getEntries();
		while (orig_list.size() > this.node_size / 2) {
			new_list.add(0, orig_list.get(orig_list.size() - 1));
			orig_list.remove(orig_list.size() - 1);
		}
		original.key = orig_list.get(orig_list.size() - 1).key;
		new_leaf.key = new_list.get(new_list.size() - 1).key;
		original.setNext(new_leaf);
		new_leaf.setPrev(original);
	}

	private void splitInner(TreeNode original, TreeNode new_leaf) {
		// split data between full node and a n empty node
		// @input: full node, and new empty node
		List<Node> orig_list = original.getSons();
		List<Node> new_list = new_leaf.getSons();
		while (orig_list.size() > this.node_size / 2) {
			new_list.add(0, orig_list.get(orig_list.size() - 1));
			new_list.get(0).setParent(new_leaf);
			orig_list.remove(orig_list.size() - 1);
		}
		original.key = orig_list.get(orig_list.size() - 1).key;
		new_leaf.key = new_list.get(new_list.size() - 1).key;
	}

	private int getKeyBefore(int key, int transactionIndex) throws LockException {
		// return value by key.
		// @input: key number.
		// @output: relative ID
		int res = -1;
		LeafNode leaf = getLeaf(key, transactionIndex);
		List<data> data_list = leaf.getEntries();
		for (Iterator<data> i = data_list.iterator(); i.hasNext();) {
			data it_data = i.next();
			if (it_data.key < key) {
				res = it_data.key;
			}
		}
		if (res == -1) {
			LeafNode prev_leaf = leaf.getPrev();
			if (prev_leaf != null) {
				res = prev_leaf.getEntries().get(prev_leaf.getSize() - 1).key;
			}
		}
		return res;
	}

	private Next next(int key, LeafNode leaf, int maxKey, int transactionIndex) throws LockException {
		this.lock.lockKeyRead(key, transactionIndex);
		LeafNode curr_leaf = leaf;
		while (curr_leaf != null) {
			List<data> data_list = curr_leaf.getEntries();
			for (Iterator<data> i = data_list.iterator(); i.hasNext();) {
				data it_data = i.next();
				if (it_data.key > key) {
					if (it_data.key >= maxKey) {
						return null;
					}
					Next res = new Next();
					res.key = it_data.key;
					res.page = curr_leaf;
					return res;
				}
			}
			curr_leaf = curr_leaf.getNext();
			if (curr_leaf != null) {
				this.lock.lockPageRead(curr_leaf, transactionIndex);
				this.lock.unlockPageRead(curr_leaf.getPrev(), transactionIndex);
			}
		}
		return null;
	}

	private Next search(int key, int maxKey, int transactionIndex) throws LockException {
		this.lock.lockKeyRead(getKeyBefore(key, transactionIndex), transactionIndex);
		LeafNode leaf = getLeaf(key, transactionIndex);
		return next(key - 1, leaf, maxKey, transactionIndex);
	}

}

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

class LeafNode extends Node {
	private List<data> entries;
	private LeafNode prev;
	private LeafNode next;

	public LeafNode(int key) {
		this.key = key;
		this.father = null;
		this.entries = new ArrayList<data>();
		this.prev = null;
		this.next = null;
	}

	public int getSize() {
		return entries.size();
	}

	public List<data> getEntries() {
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

}

class data {
	public int key;
	public int rID;

	public data(int key, int rID) {
		this.key = key;
		this.rID = rID;
	}
}
