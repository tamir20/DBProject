package dbProject.model;

import java.util.List;

public class Operation {

	private int id;

    private Command command;

    private List<String> args;

    public Operation(int id, Command command, List<String> args) {
        this.id = id;
        this.command = command;
        this.args = args;
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

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", command=" + command +
                ", args=" + args +
                '}';
    }
}
