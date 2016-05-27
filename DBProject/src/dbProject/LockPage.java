package dbProject;

public class LockPage extends Lock {
	private Object page;

	public LockPage(Object page) {
		this.page = page;
	}

	public Object getPage() {
		return this.page;
	}
}