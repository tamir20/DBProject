package dbProject;

import java.util.LinkedList;
import java.util.List;

public class Graph {
	private List<GraphNode> vertices;

	public Graph() {
		this.vertices = new LinkedList<GraphNode>();
	}

	public void addEdge(int from, int to) {
		GraphNode Vfrom = null;
		GraphNode Vto = null;
		for (int i = 0; i < this.vertices.size(); i++) {
			if (this.vertices.get(i).getTransaction() == from) {
				Vfrom = this.vertices.get(i);
			}
			if (this.vertices.get(i).getTransaction() == to) {
				Vto = this.vertices.get(i);
			}
		}
		if (Vfrom == null) {
			Vfrom = new GraphNode(from);
			this.vertices.add(Vfrom);
		}
		if (Vto == null) {
			Vto = new GraphNode(to);
			this.vertices.add(Vto);
		}
		Vto.addNodeBefore(Vfrom);
	}

	public int findTransactionInCycle() {
		// a simple way of using topological sorting in order to find cycle
		// in graph. Instead of removing a vertex with in degree 0 i mark it
		// (update the boolean variable of "in degree 0" to true)
		for (int i = 0; i < this.vertices.size(); i++) {
			for (int j = 0; j < this.vertices.size(); j++) {
				this.vertices.get(j).updateInDegree();
			}
		}
		for (int i = 0; i < this.vertices.size(); i++) {
			if (!this.vertices.get(i).isInDegreeZero()) {
				return this.vertices.get(i).getTransaction();
			}
		}
		return -1;
	}
}