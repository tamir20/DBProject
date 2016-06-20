package dbProject;

import dbProject.model.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class BPlusTree {
	private Node root;
	private int node_size;
	private LockManager lock;

	public BPlusTree(int node_size, LockManager lock) {
		LeafNode leafNode = new LeafNode(0);
		this.node_size = node_size;
		this.root = leafNode;
		this.lock = lock;

		// add -1 to the tree to be the lowest id in the tree for future locks
		Data dat = new Data(-1, -1);
		insertToLeaf((LeafNode) this.root, -1, dat);
	}

	public int getValue(int key, int transactionIndex) throws LockException {
		// return value by key.
		// @input: key number.
		// @output: relative ID, -1 if not found
		this.lock.lockKeyRead(key, transactionIndex);
		int res = -1;
		LeafNode leaf = getLeaf(key, transactionIndex);
		List<Data> Data_list = leaf.getEntries();
		for (Iterator<Data> i = Data_list.iterator(); i.hasNext();) {
			Data it_data = i.next();
			if (it_data.getKey() == key) {
				res = it_data.getrID();
			}
		}
		this.lock.unlockPageRead(leaf, transactionIndex);
		return res;
	}

	public boolean insertData(int key, int rID, int transactionIndex) throws LockException {
		// enter Data to the tree
		// @input: key number and relative ID.
		// @output: true if Data added to the tree false if key already
		// occupied.
		LeafNode leaf = getLeaf(key, transactionIndex);
		this.lock.lockKeyRead(getKeyBefore(key, transactionIndex), transactionIndex);
		this.lock.lockKeyWrite(key, transactionIndex);
		Data insert_data = new Data(key, rID);
		boolean res = insertToLeaf(leaf, key, insert_data);
		if (leaf.getSize() > this.node_size) {
			LeafNode new_leaf = new LeafNode(0);
			this.lock.unlockPageRead(leaf, transactionIndex);
			this.lock.lockPageWrite(leaf, transactionIndex);
			this.lock.lockPageWrite(new_leaf, transactionIndex);
			splitLeaf(leaf, new_leaf);
			new_leaf.setParent(leaf.getFather());
			if (leaf.getFather() == null) {
				newRoot(leaf, new_leaf);
			} else {
				Node parent = leaf.getFather();
				new_leaf.setParent(parent);
				insertToInner(parent, new_leaf);
			}
			TreeNode curr_node = (TreeNode) leaf.getFather();
			while (curr_node.getSize() > this.node_size) {
				TreeNode new_node = new TreeNode(0);
				splitInner(curr_node, new_node);
				if (curr_node.getFather() == null) {
					newRoot(curr_node, new_node);
				} else {
					Node parent = curr_node.getFather();
					new_node.setParent(parent);
					insertToInner(parent, new_node);
				}
				curr_node = (TreeNode) curr_node.getFather();
			}
			this.lock.unlockPageWrite(leaf, transactionIndex);
			this.lock.unlockPageWrite(new_leaf, transactionIndex);
		} else {
			this.lock.unlockPageRead(leaf, transactionIndex);
		}

		return res;
	}

	public int removeData(int key, int transactionIndex) throws LockException {
		// remove Data from the tree
		// @input: key number
		// @output: the rid of the removed data, or -1 if fail
		this.lock.lockKeyWrite(key, transactionIndex);
		LeafNode leaf = getLeaf(key, transactionIndex);
		for (ListIterator<Data> i = leaf.getEntries().listIterator(); i.hasNext();) {
			Data dat = i.next();
			if (dat.getKey() == key) {
				int rid = dat.getrID();
				i.previous();
				i.remove();
				this.lock.unlockPageRead(leaf, transactionIndex);
				return rid;
			}
		}
		this.lock.unlockPageRead(leaf, transactionIndex);
		return -1;
	}

	public List<Integer> range_search(int minKey, int maxKey, int transactionIndex) throws LockException {
		List<Integer> list = new LinkedList<Integer>();
		Next next = search(minKey, maxKey, transactionIndex);
		Next prevNext = null;
		if (next == null) {
			return list;
		}
		while (next != null) {
			list.add(next.rid);
			prevNext = next;
			next = next(next.key, next.page, maxKey, transactionIndex);
		}
		this.lock.unlockPageRead(prevNext.page, transactionIndex);
		return list;
	}

	@Override
	public String toString() {
		return this.root.toString();
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
				if ((last_node == null || last_node.getKey() < key) && it_node.getKey() >= key) {
					this.lock.lockPageRead(it_node, transactionIndex);
					curr_node = it_node;
					this.lock.unlockPageRead(curr_node.getFather(), transactionIndex);
				}
				last_node = it_node;
			}
			if (last_node.getKey() <= key) {
				this.lock.lockPageRead(last_node, transactionIndex);
				curr_node = last_node;
				this.lock.unlockPageRead(curr_node.getFather(), transactionIndex);
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

	private boolean insertToLeaf(LeafNode leaf, int key, Data insertion) {
		// insert Data to given leaf
		// @input: leaf to insert, key number and relative ID.
		// @output: true if Data added to the tree false if key already
		// occupied.
		boolean res = false;
		boolean inserted = false;
		List<Data> leaf_list = leaf.getEntries();
		if (leaf_list.size() == 0) {
			leaf_list.add(insertion);
			leaf.setKey(key);
			res = true;
		} else {
			Data last_leaf = null;
			for (ListIterator<Data> i = leaf_list.listIterator(); i.hasNext() && !inserted;) {
				Data leaf_data = i.next();
				if (leaf_data.getKey() == key) {
					inserted = true;
				} else if (leaf_data.getKey() > key && (last_leaf == null || last_leaf.getKey() < key)) {
					i.previous();
					i.add(insertion);
					inserted = true;
					res = true;
				}
				last_leaf = leaf_data;
			}
			if (!inserted) {
				leaf_list.add(leaf_list.size(), insertion);
				leaf.setKey(key);
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
		for (ListIterator<Node> i = sons_list.listIterator(); i.hasNext() && !inserted;) {
			Node node = i.next();
			if (node.getKey() >= new_son.getKey() && (last_node == null || last_node.getKey() < new_son.getKey())) {
				i.previous();
				i.add(new_son);
				inserted = true;
			}
			last_node = node;
		}
		if (inserted == false) {
			sons_list.add(sons_list.size(), new_son);
			new_parent.setKey(new_son.getKey());
		}
	}

	private void splitLeaf(LeafNode original, LeafNode new_leaf) {
		// split Data between full leaf and a n empty leaf
		// @input: full leaf, and new empty leaf
		List<Data> orig_list = original.getEntries();
		List<Data> new_list = new_leaf.getEntries();
		while (orig_list.size() > this.node_size / 2) {
			new_list.add(0, orig_list.get(orig_list.size() - 1));
			orig_list.remove(orig_list.size() - 1);
		}
		// original.key = orig_list.get(orig_list.size() - 1).key;
		original.setKey(orig_list.get(orig_list.size() - 1).getKey());
		new_leaf.setKey(new_list.get(new_list.size() - 1).getKey());
		original.setNext(new_leaf);
		new_leaf.setPrev(original);
	}

	private void splitInner(TreeNode original, TreeNode new_leaf) {
		// split Data between full node and a n empty node
		// @input: full node, and new empty node
		List<Node> orig_list = original.getSons();
		List<Node> new_list = new_leaf.getSons();
		while (orig_list.size() > this.node_size / 2) {
			new_list.add(0, orig_list.get(orig_list.size() - 1));
			new_list.get(0).setParent(new_leaf);
			orig_list.remove(orig_list.size() - 1);
		}
		original.setKey(orig_list.get(orig_list.size() - 1).getKey());
		new_leaf.setKey(new_list.get(new_list.size() - 1).getKey());
	}

	private int getKeyBefore(int key, int transactionIndex) throws LockException {
		// return value by key.
		// @input: key number.
		// @output: relative ID
		int res = -1;
		LeafNode leaf = getLeaf(key, transactionIndex);
		List<Data> Data_list = leaf.getEntries();
		for (Iterator<Data> i = Data_list.iterator(); i.hasNext();) {
			Data it_data = i.next();
			if (it_data.getKey() < key) {
				res = it_data.getKey();
			}
		}
		if (res == -1) {
			LeafNode prev_leaf = leaf.getPrev();
			if (prev_leaf != null) {
				res = prev_leaf.getEntries().get(prev_leaf.getSize() - 1).getKey();
			}
		}
		return res;
	}

	private Next next(int key, LeafNode leaf, int maxKey, int transactionIndex) throws LockException {
		this.lock.lockKeyRead(key, transactionIndex);
		LeafNode curr_leaf = leaf;
		while (curr_leaf != null) {
			List<Data> Data_list = curr_leaf.getEntries();
			for (Iterator<Data> i = Data_list.iterator(); i.hasNext();) {
				Data it_data = i.next();
				if (it_data.getKey() > key) {
					if (it_data.getKey() >= maxKey) {
						return null;
					}
					Next res = new Next();
					res.key = it_data.getKey();
					res.rid = it_data.getrID();
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
		Next next = next(key - 1, leaf, maxKey, transactionIndex);
		if (next == null) {
			this.lock.unlockPageRead(leaf, transactionIndex);
		}
		return next;
	}

}
