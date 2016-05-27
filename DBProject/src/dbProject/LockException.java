package dbProject;

public class LockException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8678750563018825787L;

	public LockException() {
		super("Already locked");
	}
}
