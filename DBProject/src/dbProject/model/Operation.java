package dbProject.model;

public class Operation {

	private int id;

    //delete this
	private int x;

    private Command command;

	public Operation() {
		this.x = 5;
	}

	public int getX() {
		return this.x;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
