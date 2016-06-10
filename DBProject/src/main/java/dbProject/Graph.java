package dbProject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Graph {
	private List<GraphNode> vertices;
	// for pseudo random scheduler type, need to get the random variable (with
	// seed) from the constructor
	private Random random;

	public Graph(Random random) {
		this.vertices = new LinkedList<GraphNode>();
		this.random = random;
	}

	public void addEdge(int from, int to) {
		GraphNode Vfrom = null;
		GraphNode Vto = null;
		for (int i = 0; i < this.vertices.size(); i++) {
			if (this.vertices.get(i).getTransaction() == from) {
				Vfrom = this.vertices.get(i);
			}
		}
		if (Vfrom == null) {
			Vfrom = new GraphNode(from);
			this.vertices.add(Vfrom);
		}
		for (int i = 0; i < this.vertices.size(); i++) {
			if (this.vertices.get(i).getTransaction() == to) {
				Vto = this.vertices.get(i);
			}
		}
		if (Vto == null) {
			Vto = new GraphNode(to);
			this.vertices.add(Vto);
		}
		Vto.addNodeBefore(Vfrom);
	}

	public int findRandomTransactionInCycle() {
		// a simple way of using topological sorting in order to find cycle
		// in graph. Instead of removing a vertex with in degree 0 i mark it
		// (update the boolean variable of "in degree 0" to true)

		// i want to choose random transaction because there can be a situation
		// where the transactions depend on each other (for example one inserts
		// a node and the other removes it) so aborting the depended transaction
		// (in our case the transaction which removes a node) every time will
		// still lead to infinite loop

		List<Integer> transactionsInCycle = new ArrayList<>();

		for (int i = 0; i < this.vertices.size(); i++) {
			for (int j = 0; j < this.vertices.size(); j++) {
				this.vertices.get(j).updateInDegree();
			}
		}
		for (int i = 0; i < this.vertices.size(); i++) {
			if (!this.vertices.get(i).isInDegreeZero()) {
				transactionsInCycle.add(this.vertices.get(i).getTransaction());
			}
		}
		if (transactionsInCycle.isEmpty()) {
			return -1;
		}
		return transactionsInCycle.get(this.random.nextInt(transactionsInCycle.size()));
	}
}