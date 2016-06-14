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
            if (command.getStartsWith().equals(line.substring(2, 2+command.getStartsWith().length()))){
                result = command;
            }
        }
        return result;
    }

    public static List<String> getParameters(String line) {
        List result = Collections.EMPTY_LIST;
        Command command = Command.getCommand(line);

        int endIdx = line.length()-3;
        int startIdx = command.getParamIndex()+2;
        if (command.equals(ALLOCATE_RECORD) || command.equals(SEARCH)) {
            endIdx = line.lastIndexOf("^")-1;
        } else {
            endIdx = line.length()-3;
        }
        if (line.endsWith(";")) {
            endIdx--;
        }

        //todo:omar extraxt rid parameter
        result = extractCSVtoList(line, startIdx, endIdx);
        return result;
    }

    private static List extractCSVtoList(String line, int startIdx, int endIdx) {
        List result;
        String parametersCSV = line.substring(startIdx, endIdx);
        result = Arrays.asList(parametersCSV.split(","));
        return result;
    }
}
