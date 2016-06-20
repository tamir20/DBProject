package dbProject.model;

import dbProject.Lock;

public class LockPage extends Lock {
	private Object page;

	public LockPage(Object page) {
		this.page = page;
	}

	public Object getPage() {
		return this.page;
	}
	
	@Override
	public String toString() {
		return "{object: "+ this.page.toString()+ " lock: "+super.toString()+"}";
	}
}