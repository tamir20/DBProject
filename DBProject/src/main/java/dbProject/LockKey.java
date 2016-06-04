package dbProject;

public class LockKey extends Lock {
	private int key;

	public LockKey(int key) {
		this.key = key;
	}

	public int getKey() {
		return this.key;
	}
}
