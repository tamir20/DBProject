package dbProject.model;

import java.util.ArrayList;
import java.util.List;

public class Order {

	private List<String> order;

	public Order() {
		this.order = new ArrayList<String>();
	}

	public void add(String st) {
		for (int i = 0; i < this.order.size(); i++) {
			if (this.order.get(i).equals(st)) {
				this.order.remove(i);
			}
		}
		this.order.add(st);
	}

	@Override
	public String toString() {
		return this.order.toString();
	}
}
