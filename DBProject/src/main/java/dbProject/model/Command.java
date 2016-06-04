package dbProject.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Command {

    ALLOCATE_RECORD("A",16),
    INSERT("I", 7),
    DELETE("D", 7),
    SEARCH("S", 7),
    RANGE_SEARCH("R", 13),
    END_TRANSACTION(";", 0);

    private final String startsWith;
    private final int paramIndex;

    Command(String startsWith, int paramIndex) {
        this.startsWith = startsWith;
        this.paramIndex = paramIndex;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    public static Command getCommand(String line){
        Command result = null;
        for (Command command : Command.values()){
            if (command.getStartsWith().equals(line.substring(0,command.getStartsWith().length()))){
                result = command;
            }
        }
        return result;
    }

    public static List<String> getParameters(String line) {
        List<String> result = null;
        Command command = Command.getCommand(line);

        if (!command.equals(END_TRANSACTION)){
            String parametersCSV = line.substring(command.getParamIndex(), line.length()-1);;
            result = Arrays.asList(parametersCSV.split(","));
        }
        return result;
    }
}
