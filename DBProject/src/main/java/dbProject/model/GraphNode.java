package dbProject.model;

import java.util.LinkedList;
import java.util.List;

public class GraphNode {

	private int transaction;
	private List<GraphNode> nodesBefore;
	private Boolean InDegreeZero;

	public GraphNode(int transaction) {
		this.transaction = transaction;
		this.nodesBefore = new LinkedList<GraphNode>();
		this.InDegreeZero = false;
	}

	public void addNodeBefore(GraphNode node) {
		for (int i = 0; i < this.nodesBefore.size(); i++) {
			if (this.nodesBefore.get(i).getTransaction() == node.getTransaction()) {
				return;
			}
		}
		this.nodesBefore.add(node);
	}

	public Boolean isInDegreeZero() {
		return this.InDegreeZero;
	}

	public void updateInDegree() {
		for (int i = 0; i < this.nodesBefore.size(); i++) {
			if (!this.nodesBefore.get(i).isInDegreeZero()) {
				this.InDegreeZero = false;
				return;
			}
		}
		this.InDegreeZero = true;
	}

	public int getTransaction() {
		return this.transaction;
	}
}
